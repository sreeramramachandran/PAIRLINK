package com.pairlink.app.ui.components

import android.net.Uri
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.pairlink.app.core.designsystem.DesignTokens
import java.io.File

/**
 * Reusable animated GIF / WebP / WhatsApp sticker rendering component.
 * Safely resolves raw file paths, content URIs, resources, and HTTP/HTTPS URLs.
 */
@Composable
fun AnimatedStickerImage(
    stickerUrl: String,
    modifier: Modifier = Modifier,
    emojiFallback: String = "💖",
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .build()
    }

    val modelData: Any? = remember(stickerUrl) {
        val clean = stickerUrl.trim()
        when {
            clean.isBlank() -> null
            clean.startsWith("http://") || clean.startsWith("https://") || clean.startsWith("content://") || clean.startsWith("android.resource://") || clean.startsWith("file://") -> clean
            clean.startsWith("/") -> {
                val file = File(clean)
                if (file.exists()) file else Uri.fromFile(file)
            }
            else -> {
                val file = File(clean)
                if (file.exists()) file else clean
            }
        }
    }

    if (modelData != null) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(modelData)
                .crossfade(true)
                .build(),
            imageLoader = imageLoader,
            contentDescription = "Animated Sticker Mood",
            contentScale = contentScale,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        color = DesignTokens.Colors.PrimaryCrimson,
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFFFF0F3)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emojiFallback, fontSize = 24.sp)
                }
            },
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(Color(0xFFFFF0F3)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emojiFallback, fontSize = 24.sp)
        }
    }
}
