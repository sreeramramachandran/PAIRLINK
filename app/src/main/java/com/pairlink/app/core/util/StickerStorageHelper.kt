package com.pairlink.app.core.util

import android.content.Context
import android.net.Uri
import com.pairlink.app.domain.model.StickerItem
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

/**
 * Helper utility for copying imported WhatsApp/device sticker files to internal app storage
 * and saving metadata persistently so stickers don't have to be re-uploaded.
 */
object StickerStorageHelper {

    private const val PREFS_NAME = "pairlink_imported_stickers_prefs"
    private const val KEY_STICKERS_JSON = "key_imported_stickers_json"

    fun saveImportedStickers(context: Context, uris: List<Uri>): List<StickerItem> {
        val stickersDir = File(context.filesDir, "imported_stickers")
        if (!stickersDir.exists()) {
            stickersDir.mkdirs()
        }

        val newlyAdded = mutableListOf<StickerItem>()

        for (uri in uris) {
            try {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {}

                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null && bytes.isNotEmpty()) {
                    val timestamp = System.currentTimeMillis()
                    val mimeType = context.contentResolver.getType(uri) ?: ""
                    val ext = if (mimeType.contains("gif", ignoreCase = true) || uri.toString().endsWith(".gif", ignoreCase = true)) "gif" else "webp"
                    val fileName = "stk_${timestamp}_${(1000..9999).random()}.$ext"
                    val destFile = File(stickersDir, fileName)

                    FileOutputStream(destFile).use { it.write(bytes) }

                    if (destFile.exists() && destFile.length() > 0) {
                        val item = StickerItem(
                            id = "imported_${destFile.name}",
                            name = "WhatsApp Sticker",
                            packName = "My WhatsApp Stickers",
                            urlOrRes = destFile.absolutePath,
                            emojiFallback = "🎭",
                            isCustom = true
                        )
                        newlyAdded.add(item)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to copy imported sticker URI: %s", uri)
            }
        }

        // Save updated list persistently
        val existing = loadImportedStickers(context)
        val combined = newlyAdded + existing
        persistStickersList(context, combined)

        return combined
    }

    fun loadImportedStickers(context: Context): List<StickerItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_STICKERS_JSON, null) ?: return emptyList()

        val list = mutableListOf<StickerItem>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val filePath = obj.optString("urlOrRes", "")
                if (filePath.isNotBlank() && File(filePath).exists()) {
                    list.add(
                        StickerItem(
                            id = obj.optString("id"),
                            name = obj.optString("name", "WhatsApp Sticker"),
                            packName = obj.optString("packName", "My WhatsApp Stickers"),
                            urlOrRes = filePath,
                            emojiFallback = obj.optString("emojiFallback", "🎭"),
                            isCustom = true
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading saved stickers JSON")
        }
        return list
    }

    private fun persistStickersList(context: Context, list: List<StickerItem>) {
        try {
            val jsonArray = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("name", item.name)
                    put("packName", item.packName)
                    put("urlOrRes", item.urlOrRes)
                    put("emojiFallback", item.emojiFallback)
                }
                jsonArray.put(obj)
            }
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_STICKERS_JSON, jsonArray.toString()).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error persisting stickers JSON")
        }
    }

    fun updateStickerCloudUrl(context: Context, stickerId: String, cloudUrl: String) {
        if (cloudUrl.isBlank() || !cloudUrl.startsWith("http")) return
        val existing = loadImportedStickers(context).toMutableList()
        val index = existing.indexOfFirst { it.id == stickerId }
        if (index != -1) {
            val updated = existing[index].copy(urlOrRes = cloudUrl)
            existing[index] = updated
            persistStickersList(context, existing)
        }
    }
}
