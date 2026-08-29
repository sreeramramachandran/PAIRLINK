package com.pairlink.app.core.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

/**
 * Custom ActivityResultContract that explicitly forces Android CTS-compliant System File Browser
 * (DocumentsUI / File Manager) across all OEMs (Samsung, Xiaomi, Vivo, Oppo, OnePlus, Pixel).
 * Prevents OEM Gallery app hijacking when selecting WhatsApp .gif or .webp stickers.
 */
class StickerPickerContract : ActivityResultContract<Unit, List<Uri>>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        val openDocIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/webp", "image/gif", "image/png", "image/jpeg", "image/*"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }

        // Verify if system document manager resolves this intent
        return if (openDocIntent.resolveActivity(context.packageManager) != null) {
            openDocIntent
        } else {
            // Fallback for older or customized ROMs
            Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/webp", "image/gif", "image/png", "image/jpeg", "image/*"))
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        if (resultCode != Activity.RESULT_OK || intent == null) return emptyList()
        val uris = mutableListOf<Uri>()

        intent.data?.let { uris.add(it) }

        intent.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {
                uris.add(clipData.getItemAt(i).uri)
            }
        }

        return uris
    }
}
