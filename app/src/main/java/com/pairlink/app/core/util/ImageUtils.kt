package com.pairlink.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Base64
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {

    /**
     * Converts an image model (URL, Base64 data string, Uri, etc.) into a format Coil can render directly.
     */
    fun getAvatarModel(model: Any?): Any? {
        if (model == null) return null
        if (model is String) {
            val trimmed = model.trim()
            if (trimmed.isBlank()) return null
            if (trimmed.startsWith("data:image", ignoreCase = true) && trimmed.contains("base64,")) {
                val base64Content = trimmed.substringAfter("base64,")
                return try {
                    Base64.decode(base64Content, Base64.DEFAULT)
                } catch (e: Exception) {
                    Timber.w(e, "Failed to decode base64 avatar")
                    null
                }
            }
            return trimmed
        }
        return model
    }

    /**
     * Compresses an image from a content Uri into an optimized byte array.
     */
    fun compressUriToByteArray(
        context: Context,
        uri: Uri,
        maxDimension: Int = 360,
        quality: Int = 75
    ): ByteArray? {
        return try {
            val input: InputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(input)
            input.close()
            if (originalBitmap == null) return null

            // Correct EXIF orientation if needed
            val orientedBitmap = fixOrientation(context, uri, originalBitmap)

            // Scale to max dimension
            val width = orientedBitmap.width
            val height = orientedBitmap.height
            val scale = (maxDimension.toFloat() / maxOf(width, height)).coerceAtMost(1.0f)

            val scaledBitmap = if (scale < 1.0f) {
                Bitmap.createScaledBitmap(
                    orientedBitmap,
                    (width * scale).toInt().coerceAtLeast(1),
                    (height * scale).toInt().coerceAtLeast(1),
                    true
                )
            } else {
                orientedBitmap
            }

            val stream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            val bytes = stream.toByteArray()
            stream.close()
            Timber.i("Compressed image from URI. Size: %d KB", bytes.size / 1024)
            bytes
        } catch (e: Exception) {
            Timber.e(e, "Error compressing image from URI: %s", e.message)
            null
        }
    }

    /**
     * Compresses an image from a content Uri into a compact Base64 data URL string.
     */
    fun compressUriToBase64(
        context: Context,
        uri: Uri,
        maxDimension: Int = 360,
        quality: Int = 75
    ): String? {
        val bytes = compressUriToByteArray(context, uri, maxDimension, quality) ?: return null
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64"
    }

    private fun fixOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val orientation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)
                    exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                } ?: ExifInterface.ORIENTATION_NORMAL
            } else {
                ExifInterface.ORIENTATION_NORMAL
            }

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                else -> return bitmap
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (_: Exception) {
            bitmap
        }
    }
}
