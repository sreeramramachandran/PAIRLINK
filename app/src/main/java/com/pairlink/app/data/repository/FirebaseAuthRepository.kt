package com.pairlink.app.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.pairlink.app.core.util.ImageUtils
import com.pairlink.app.domain.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production Firebase Authentication and Firestore User Profile Repository.
 * Handles lifetime identity, guaranteed single Partner ID generation, real-time snapshot sync,
 * and robust session recovery across app restarts, reboots, and logouts.
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var userDocListener: ListenerRegistration? = null

    private val _currentUser = MutableStateFlow(UserProfile())
    override val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    init {
        scope.launch {
            auth.addAuthStateListener { firebaseAuth ->
                val currentFirebaseUser = firebaseAuth.currentUser
                if (currentFirebaseUser != null) {
                    Timber.d("FirebaseAuth state changed: user=%s", currentFirebaseUser.uid)
                    attachUserProfileListener(currentFirebaseUser.uid)
                } else {
                    Timber.d("FirebaseAuth state changed: signed out")
                    userDocListener?.remove()
                    userDocListener = null
                    _currentUser.value = UserProfile()
                }
            }

            val initialUser = auth.currentUser
            if (initialUser != null) {
                attachUserProfileListener(initialUser.uid)
            }
        }
    }

    private fun attachUserProfileListener(uid: String) {
        userDocListener?.remove()
        userDocListener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Firestore snapshot listener error for user: %s", uid)
                    return@addSnapshotListener
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        val profile = snapshot.toObject(UserProfile::class.java)
                        if (profile != null) {
                            _currentUser.value = profile
                            Timber.d("User profile updated via snapshot: uid=%s, partnerId=%s, partnerUid=%s", uid, profile.partnerId, profile.partnerUid)
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing user profile snapshot for UID: %s", uid)
                }
            }
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun login(phone: String, pin: String): Result<UserProfile> = withContext(ioDispatcher) {
        val cleanPhone = phone.filter { it.isDigit() }
        if (cleanPhone.length < 8) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid phone number."))
        }
        if (pin.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        val syntheticEmail = phoneToSyntheticEmail(cleanPhone)
        Timber.i("Attempting login for phone: %s (syntheticEmail: %s)", cleanPhone, syntheticEmail)

        try {
            val authResult = auth.signInWithEmailAndPassword(syntheticEmail, pin).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("Firebase UID is null.")

            val userDoc = firestore.collection("users").document(uid).get().await()
            if (!userDoc.exists()) {
                throw IllegalStateException("User document not found in Firestore.")
            }

            val profile = userDoc.toObject(UserProfile::class.java)
                ?: throw IllegalStateException("Failed to parse user profile.")

            _currentUser.value = profile
            attachUserProfileListener(uid)
            Timber.i("Login successful for UID: %s (Partner ID: %s, PartnerUid: %s)", uid, profile.partnerId, profile.partnerUid)
            Result.success(profile)
        } catch (e: Exception) {
            Timber.e(e, "Login failed for phone: %s. Error: %s", cleanPhone, e.message)
            handleAuthException(e)
        }
    }

    override suspend fun register(
        username: String,
        phone: String,
        dob: String,
        pin: String,
        imageUri: Uri?
    ): Result<UserProfile> = withContext(ioDispatcher) {
        val cleanUsername = username.trim()
        val cleanPhone = phone.filter { it.isDigit() }

        if (cleanUsername.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a username."))
        }
        if (cleanPhone.length < 8) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid phone number."))
        }
        if (pin.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }
        if (dob.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please select your date of birth."))
        }

        val syntheticEmail = phoneToSyntheticEmail(cleanPhone)
        Timber.i("Attempting registration for username: %s, phone: %s (syntheticEmail: %s)", cleanUsername, cleanPhone, syntheticEmail)

        try {
            val authResult = auth.createUserWithEmailAndPassword(syntheticEmail, pin).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("Failed to create Firebase user.")
            Timber.i("Firebase user created with UID: %s", uid)

            // Upload profile picture if provided
            var profileImageUrl = ""
            if (imageUri != null) {
                try {
                    val compressedBytes = ImageUtils.compressUriToByteArray(context, imageUri)
                    val uploadResult = withTimeoutOrNull(10000L) {
                        try {
                            val imageRef = storage.reference.child("profile_images/$uid.jpg")
                            if (compressedBytes != null) {
                                imageRef.putBytes(compressedBytes).await()
                            } else {
                                imageRef.putFile(imageUri).await()
                            }
                            imageRef.downloadUrl.await().toString()
                        } catch (e: Exception) {
                            Timber.w(e, "Firebase Storage upload error: %s", e.message)
                            null
                        }
                    }
                    profileImageUrl = uploadResult ?: ImageUtils.compressUriToBase64(context, imageUri) ?: ""
                    Timber.i("Profile picture prepared for registration (length: %d)", profileImageUrl.length)
                } catch (imgError: Exception) {
                    Timber.w(imgError, "Failed to process profile picture: %s", imgError.message)
                    profileImageUrl = ImageUtils.compressUriToBase64(context, imageUri) ?: ""
                }
            }

            // Generate unique Partner ID ONLY ONCE during registration: PAIR-XXXXXXXX
            val uniquePartnerId = generateUniquePartnerId()

            val newProfile = UserProfile(
                uid = uid,
                username = cleanUsername,
                phoneNumber = cleanPhone,
                partnerId = uniquePartnerId,
                partnerUid = null,
                dateOfBirth = dob,
                profileImageUrl = profileImageUrl,
                mood = "Happy",
                status = "Available",
                relationshipDate = null,
                partnerNickname = "",
                statusMessage = "Connected to our private sanctuary. ✨",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                fcmToken = null
            )

            firestore.collection("users").document(uid).set(newProfile).await()
            _currentUser.value = newProfile
            attachUserProfileListener(uid)
            Timber.i("Registration complete for UID: %s, Lifetime PartnerID: %s", uid, uniquePartnerId)
            Result.success(newProfile)
        } catch (e: Exception) {
            Timber.e(e, "Registration failed for phone: %s. Error: %s", cleanPhone, e.message)
            handleAuthException(e)
        }
    }

    override suspend fun updateProfile(
        username: String,
        nickname: String,
        dob: String,
        avatarUrl: String?
    ): Result<Unit> = withContext(ioDispatcher) {
        val uid = auth.currentUser?.uid ?: _currentUser.value.uid
        if (uid.isBlank()) return@withContext Result.failure(IllegalStateException("Not authenticated."))

        val current = _currentUser.value
        val newUsername = if (username.isNotBlank()) username else current.username
        val newNickname = nickname
        val newDob = if (dob.isNotBlank()) dob else current.dateOfBirth
        val newAvatarUrl = if (!avatarUrl.isNullOrBlank()) avatarUrl else current.profileImageUrl

        val updated = current.copy(
            username = newUsername,
            partnerNickname = newNickname,
            dateOfBirth = newDob,
            profileImageUrl = newAvatarUrl,
            updatedAt = System.currentTimeMillis()
        )
        _currentUser.value = updated

        if (auth.currentUser != null) {
            try {
                val updateMap = mutableMapOf<String, Any>(
                    "partnerNickname" to newNickname,
                    "updatedAt" to System.currentTimeMillis()
                )
                if (username.isNotBlank()) {
                    updateMap["username"] = newUsername
                }
                if (dob.isNotBlank()) {
                    updateMap["dateOfBirth"] = newDob
                }
                if (!avatarUrl.isNullOrBlank()) {
                    updateMap["profileImageUrl"] = newAvatarUrl
                }

                // Strictly update only editable fields; never overwrite immutable lifetime fields (partnerId, uid, phone, createdAt)
                firestore.collection("users").document(uid).set(
                    updateMap,
                    SetOptions.merge()
                ).await()
                Timber.i("Profile updated in Firestore: %s", updateMap.keys)

                // Send profile picture update notification to partner
                if (!avatarUrl.isNullOrBlank() && !current.partnerUid.isNullOrBlank()) {
                    val notifId = "avatar_${System.currentTimeMillis()}_${(100..999).random()}"
                    val senderName = newUsername.ifBlank { "Partner" }
                    val title = "✨ $senderName updated profile picture"
                    val notifMessage = "Check out your partner's fresh new avatar in your sanctuary! 💕"

                    firestore.collection("notifications").document(notifId).set(
                        mapOf(
                            "id" to notifId,
                            "title" to title,
                            "message" to notifMessage,
                            "type" to "SYSTEM",
                            "senderUid" to uid,
                            "receiverUid" to current.partnerUid,
                            "iconName" to "person",
                            "timestamp" to System.currentTimeMillis(),
                            "read" to false
                        ),
                        SetOptions.merge()
                    )

                    com.pairlink.app.core.util.FcmPushHelper.sendPushNotificationToPartner(
                        partnerUid = current.partnerUid,
                        title = title,
                        message = notifMessage,
                        type = "SYSTEM",
                        senderUid = uid,
                        iconName = "person"
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "Could not update user profile in Firestore immediately: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    override suspend fun updateRelationshipDate(startDate: String): Result<Unit> = withContext(ioDispatcher) {
        val uid = auth.currentUser?.uid ?: _currentUser.value.uid
        if (uid.isBlank()) return@withContext Result.failure(IllegalStateException("Not authenticated."))

        _currentUser.value = _currentUser.value.copy(
            relationshipDate = startDate,
            updatedAt = System.currentTimeMillis()
        )

        if (auth.currentUser != null) {
            try {
                firestore.collection("users").document(uid).set(
                    mapOf(
                        "relationshipDate" to startDate,
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                Timber.w(e, "Could not update relationshipDate in Firestore immediately: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    override suspend fun unpair(): Result<Unit> = withContext(ioDispatcher) {
        val uid = auth.currentUser?.uid ?: _currentUser.value.uid
        if (uid.isBlank()) return@withContext Result.failure(IllegalStateException("Not authenticated."))

        _currentUser.value = _currentUser.value.copy(
            partnerUid = null,
            partnerNickname = "",
            relationshipDate = null,
            updatedAt = System.currentTimeMillis()
        )

        if (auth.currentUser != null) {
            try {
                firestore.collection("users").document(uid).set(
                    mapOf(
                        "partnerUid" to null,
                        "relationshipDate" to null,
                        "partnerNickname" to "",
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                Timber.w(e, "Could not clear pairing in Firestore: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    override suspend fun logout(): Result<Unit> = withContext(ioDispatcher) {
        try {
            userDocListener?.remove()
            userDocListener = null
            auth.signOut()
            _currentUser.value = UserProfile()
            Timber.i("User logged out successfully")
        } catch (e: Exception) {
            Timber.w(e, "Error during signOut: %s", e.message)
        }
        Result.success(Unit)
    }

    override suspend fun fetchCurrentUserProfile(): Result<UserProfile?> = withContext(ioDispatcher) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            return@withContext Result.success(null)
        }

        try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists()) {
                val profile = doc.toObject(UserProfile::class.java)
                if (profile != null) {
                    _currentUser.value = profile
                    attachUserProfileListener(uid)
                    Timber.d("Fetched profile from Firestore for UID: %s (partnerUid=%s)", uid, profile.partnerUid)
                    return@withContext Result.success(profile)
                }
            }
            Result.success(_currentUser.value)
        } catch (e: Exception) {
            Timber.w(e, "Could not fetch user profile from Firestore: %s", e.message)
            Result.success(_currentUser.value)
        }
    }

    private fun phoneToSyntheticEmail(cleanPhone: String): String {
        return "$cleanPhone@pairlink.app"
    }

    private suspend fun generateUniquePartnerId(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val random = SecureRandom()

        var attempts = 0
        while (attempts < 5) {
            val sb = StringBuilder(8)
            repeat(8) {
                sb.append(chars[random.nextInt(chars.length)])
            }
            val partnerId = "PAIR-$sb"

            try {
                val query = firestore.collection("users")
                    .whereEqualTo("partnerId", partnerId)
                    .limit(1)
                    .get()
                    .await()
                if (query.isEmpty) {
                    return partnerId
                }
            } catch (_: Exception) {
                return partnerId
            }
            attempts++
        }
        return "PAIR-${System.currentTimeMillis().toString().takeLast(8).uppercase()}"
    }

    /**
     * Translates raw Firebase exceptions into crystal-clear, informative messages.
     */
    private fun handleAuthException(e: Exception): Result<UserProfile> {
        val rawMessage = e.message ?: ""
        val errorCode = (e as? FirebaseAuthException)?.errorCode ?: ""

        Timber.e(e, "FirebaseAuthException caught [code=%s, message=%s]", errorCode, rawMessage)

        val userFriendlyMessage = when {
            rawMessage.contains("API key not valid", ignoreCase = true) ||
            rawMessage.contains("API_KEY_INVALID", ignoreCase = true) ||
            errorCode.contains("INVALID_API_KEY", ignoreCase = true) ->
                "Invalid API Key: The API key in google-services.json is invalid or not authorized. Please check your Firebase Console."

            errorCode == "ERROR_OPERATION_NOT_ALLOWED" ||
            rawMessage.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) ||
            rawMessage.contains("disabled", ignoreCase = true) && rawMessage.contains("auth", ignoreCase = true) ->
                "Authentication disabled: Email/Password sign-in is disabled in your Firebase project. Please enable it in Firebase Console > Authentication > Sign-in method."

            e is FirebaseAuthUserCollisionException ||
            errorCode == "ERROR_EMAIL_ALREADY_IN_USE" ||
            rawMessage.contains("EMAIL_EXISTS", ignoreCase = true) ->
                "An account with this phone number already exists. Please log in."

            e is FirebaseAuthWeakPasswordException ||
            errorCode == "ERROR_WEAK_PASSWORD" ||
            rawMessage.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "Password is too weak. Please use at least 6 characters."

            e is FirebaseAuthInvalidCredentialsException ||
            errorCode == "ERROR_INVALID_CREDENTIAL" ||
            errorCode == "ERROR_WRONG_PASSWORD" ||
            rawMessage.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
            rawMessage.contains("INVALID_PASSWORD", ignoreCase = true) ->
                "Invalid phone number or password. Please check and try again."

            e is FirebaseAuthInvalidUserException ||
            errorCode == "ERROR_USER_NOT_FOUND" ||
            rawMessage.contains("USER_NOT_FOUND", ignoreCase = true) ->
                "No registered account found with this phone number."

            errorCode == "ERROR_USER_DISABLED" ||
            rawMessage.contains("USER_DISABLED", ignoreCase = true) ->
                "Account disabled: This account has been disabled by an administrator."

            e is FirebaseNetworkException ||
            errorCode == "ERROR_NETWORK_REQUEST_FAILED" ||
            rawMessage.contains("network error", ignoreCase = true) ||
            rawMessage.contains("Unable to resolve host", ignoreCase = true) ->
                "Network unavailable: Please check your internet connection and try again."

            errorCode == "ERROR_TOO_MANY_REQUESTS" ||
            rawMessage.contains("TOO_MANY_ATTEMPTS_TRY_LATER", ignoreCase = true) ->
                "Too many attempts. Please try again in a few moments."

            errorCode == "ERROR_APP_NOT_AUTHORIZED" ||
            rawMessage.contains("APP_NOT_AUTHORIZED", ignoreCase = true) ->
                "Configuration error: App not authorized to use Firebase. Please verify package name and SHA-1 fingerprint in Firebase Console."

            rawMessage.contains("[") && rawMessage.contains("]") -> {
                val extracted = rawMessage.substringAfter("[").substringBefore("]").trim()
                if (extracted.isNotBlank()) extracted else rawMessage
            }

            rawMessage.isNotBlank() -> {
                rawMessage.replace("com.google.firebase.FirebaseException: ", "")
                    .replace("com.google.firebase.auth.FirebaseAuthException: ", "")
                    .replace("An internal error has occurred.", "Configuration error:")
                    .trim()
            }

            else -> "Authentication failed. Please check your network and Firebase configuration."
        }

        return Result.failure(Exception(userFriendlyMessage, e))
    }
}
