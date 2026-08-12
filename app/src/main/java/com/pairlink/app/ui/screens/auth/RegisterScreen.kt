package com.pairlink.app.ui.screens.auth

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField
import java.time.LocalDate

/**
 * Register Screen matching Stitch design with flexible phone validation, photo picker,
 * DatePickerDialog for DOB, and clear error banners.
 */
@Composable
fun RegisterScreen(
    onRegisterSubmit: (username: String, phone: String, dob: String, pin: String, confirmPin: String, imageUri: Uri?) -> Unit,
    onNavigateToLogin: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val initialDate = try {
        if (dob.isNotBlank()) LocalDate.parse(dob) else LocalDate.of(1998, 1, 1)
    } catch (_: Exception) {
        LocalDate.of(1998, 1, 1)
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                dob = selected.toString()
                localError = null
                onClearError()
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )
    }

    val cleanDigits = phone.filter { it.isDigit() }
    val isPhoneValid = cleanDigits.length in 8..15
    val displayError = localError ?: errorMessage

    AnimatedMeshBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
                contentPadding = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "PairLink",
                            color = DesignTokens.Colors.PrimaryPink,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Create your digital sanctuary.",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    // Error Banner
                    AnimatedVisibility(
                        visible = !displayError.isNullOrBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                .background(DesignTokens.Colors.DangerRoseSurface)
                                .border(1.dp, DesignTokens.Colors.DangerRose.copy(alpha = 0.5f), RoundedCornerShape(DesignTokens.Radius.Medium))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = DesignTokens.Colors.DangerRose,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = displayError ?: "",
                                color = Color(0xFFFFE4E6),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Photo Upload Area
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.5.dp, DesignTokens.Colors.PrimaryPink.copy(alpha = 0.5f), CircleShape)
                                .clickable { photoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Selected Photo",
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Add Photo",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Text(
                            text = if (selectedImageUri != null) "Photo selected" else "Upload photo (optional)",
                            color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }

                    // Form Fields
                    GlassTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            localError = null
                            onClearError()
                        },
                        placeholder = "Username",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User",
                                tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        GlassTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                localError = null
                                onClearError()
                            },
                            placeholder = "Phone Number (e.g. 9876543210)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Phone",
                                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassTextField(
                            value = dob,
                            onValueChange = {
                                dob = it
                                localError = null
                                onClearError()
                            },
                            placeholder = "Date of Birth (YYYY-MM-DD)",
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Pick Date",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { datePickerDialog.show() }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    GlassTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            localError = null
                            onClearError()
                        },
                        placeholder = "Password (min. 6 characters)",
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Visibility",
                                tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { isPasswordVisible = !isPasswordVisible }
                            )
                        }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        GlassTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                localError = null
                                onClearError()
                            },
                            placeholder = "Confirm Password",
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LockReset,
                                    contentDescription = "Confirm Password",
                                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                                )
                            }
                        )
                    }

                    // Register Action Button with explicit helpful feedback
                    GlassButton(
                        text = if (isLoading) "Registering..." else "Register",
                        enabled = !isLoading,
                        onClick = {
                            when {
                                username.trim().isBlank() -> {
                                    localError = "Please enter your username."
                                }
                                !isPhoneValid -> {
                                    localError = "Please enter a valid phone number (at least 8–10 digits)."
                                }
                                dob.trim().isBlank() -> {
                                    localError = "Please select your date of birth."
                                    datePickerDialog.show()
                                }
                                password.length < 6 -> {
                                    localError = "Password must be at least 6 characters."
                                }
                                password != confirmPassword -> {
                                    localError = "Passwords do not match."
                                }
                                else -> {
                                    localError = null
                                    onRegisterSubmit(username.trim(), cleanDigits, dob.trim(), password, confirmPassword, selectedImageUri)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = if (isLoading) {
                            {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = DesignTokens.Colors.PrimaryPink,
                                    strokeWidth = 2.dp
                                )
                            }
                        } else null,
                        trailingIcon = if (!isLoading) {
                            {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Arrow",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )

                    // Footer Link
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Log in",
                            color = DesignTokens.Colors.PrimaryPink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onNavigateToLogin)
                        )
                    }
                }
            }
        }
    }
}
