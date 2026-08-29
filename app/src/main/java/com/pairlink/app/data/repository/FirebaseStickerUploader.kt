package com.pairlink.app.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object FirebaseStickerUploader {

    /**
     * Uploads local sticker file to cloud storage (Firebase Storage with fallbacks)
     * so both user and partner can view custom WhatsApp stickers globally across any device.
     */
    suspend fun uploadStickerIfNeeded(context: Context, stickerUrl: String): String = withContext(Dispatchers.IO) {
        if (stickerUrl.isBlank()) return@withContext ""

        // If it's already a remote HTTP/HTTPS URL or android.resource URI, return directly
        if (stickerUrl.startsWith("http://") || stickerUrl.startsWith("https://") || stickerUrl.startsWith("android.resource://")) {
            return@withContext stickerUrl
        }

        val fileToUpload: File? = when {
            stickerUrl.startsWith("content://") -> {
                try {
                    val uri = Uri.parse(stickerUrl)
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes != null && bytes.isNotEmpty()) {
                        val tempFile = File(context.cacheDir, "temp_stk_${System.currentTimeMillis()}_${(100..999).random()}.webp")
                        tempFile.writeBytes(bytes)
                        tempFile
                    } else null
                } catch (e: Exception) {
                    Timber.w(e, "Error resolving content URI in FirebaseStickerUploader")
                    null
                }
            }
            stickerUrl.startsWith("file://") -> File(Uri.parse(stickerUrl).path ?: "")
            else -> File(stickerUrl)
        }

        if (fileToUpload == null || !fileToUpload.exists() || fileToUpload.length() == 0L) {
            Timber.w("Sticker file does not exist locally: %s", stickerUrl)
            return@withContext stickerUrl
        }

        // Method 1: Try Firebase Storage
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "user_${System.currentTimeMillis()}"
            val timestamp = System.currentTimeMillis()
            val ext = if (fileToUpload.name.endsWith(".gif", ignoreCase = true)) "gif" else "webp"
            val storageRef = FirebaseStorage.getInstance().reference.child("stickers/$uid/stk_${timestamp}_${(100..999).random()}.$ext")

            storageRef.putFile(Uri.fromFile(fileToUpload)).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            if (downloadUrl.isNotBlank()) {
                Timber.d("Successfully uploaded custom sticker to Firebase Storage: %s", downloadUrl)
                return@withContext downloadUrl
            }
        } catch (e: Exception) {
            Timber.w(e, "Firebase Storage upload failed for %s, trying high-speed cloud fallback...", stickerUrl)
        }

        // Method 2: High-speed Cloud Multipart Upload (Catbox API)
        try {
            val uploadedUrl = uploadFileToCatbox(fileToUpload)
            if (uploadedUrl.isNotBlank() && (uploadedUrl.startsWith("http://") || uploadedUrl.startsWith("https://"))) {
                Timber.d("Successfully uploaded custom sticker to Cloud: %s", uploadedUrl)
                return@withContext uploadedUrl
            }
        } catch (e: Exception) {
            Timber.e(e, "Cloud multipart upload failed for %s", stickerUrl)
        }

        return@withContext stickerUrl
    }

    private fun uploadFileToCatbox(file: File): String {
        val boundary = "*****${UUID.randomUUID()}*****"
        val lineEnd = "\r\n"
        val twoHyphens = "--"
        val url = URL("https://catbox.moe/user/api.php")
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.doOutput = true
        connection.useCaches = false
        connection.requestMethod = "POST"
        connection.setRequestProperty("Connection", "Keep-Alive")
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        DataOutputStream(connection.outputStream).use { outputStream ->
            // Field: reqtype = fileupload
            outputStream.writeBytes(twoHyphens + boundary + lineEnd)
            outputStream.writeBytes("Content-Disposition: form-data; name=\"reqtype\"$lineEnd")
            outputStream.writeBytes(lineEnd)
            outputStream.writeBytes("fileupload" + lineEnd)

            // Field: fileToUpload
            val mime = if (file.name.endsWith(".gif", ignoreCase = true)) "image/gif" else "image/webp"
            outputStream.writeBytes(twoHyphens + boundary + lineEnd)
            outputStream.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"${file.name}\"$lineEnd")
            outputStream.writeBytes("Content-Type: $mime$lineEnd")
            outputStream.writeBytes(lineEnd)

            FileInputStream(file).use { inputStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
            outputStream.writeBytes(lineEnd)
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
            outputStream.flush()
        }

        if (connection.responseCode == 200) {
            val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText().trim() }
            return response
        } else {
            Timber.e("Catbox upload error code: %d", connection.responseCode)
        }
        return ""
    }
}
