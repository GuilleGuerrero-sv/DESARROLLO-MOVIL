package com.creacionesnormita.mobile.features.home

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.creacionesnormita.mobile.ui.components.WireImage

/**
 * Video de muestra de la pantalla de inicio.
 * - Si queda vacío (""), se reproduce el archivo local res/raw/video_muestra.mp4
 * - Si pones una URL (por ejemplo de Cloudinary, terminada en .mp4), se reproduce desde internet
 *   (requiere el permiso INTERNET en el AndroidManifest.xml).
 */
private const val VIDEO_MUESTRA_URL = ""

/**
 * Video de muestra en formato 16:9 con controles de reproducción.
 * Usa Media3 (ExoPlayer), que es más estable que VideoView dentro de un LazyColumn.
 * El archivo local se busca por nombre (res/raw/video_muestra.mp4), así que
 * el proyecto compila aunque todavía no lo hayas agregado.
 */

@OptIn(UnstableApi::class)
@Composable
internal fun VideoMuestra(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val resId = remember {
        context.resources.getIdentifier("video_muestra", "raw", context.packageName)
    }

    if (VIDEO_MUESTRA_URL.isBlank() && resId == 0) {
        WireImage(
            label = "Agrega res/raw/video_muestra.mp4",
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
        return
    }

    // Proporción real del video (ancho / alto). Empieza en 16:9 hasta que cargue.
    var aspecto by remember { mutableStateOf(16f / 9f) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = if (VIDEO_MUESTRA_URL.isBlank()) {
                Uri.parse("android.resource://${context.packageName}/$resId")
            } else {
                Uri.parse(VIDEO_MUESTRA_URL)
            }
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0f // video sin sonido
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    aspecto = videoSize.width * videoSize.pixelWidthHeightRatio / videoSize.height
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            // Se adapta al video, pero con límites para que uno muy vertical no ocupe toda la pantalla
            .aspectRatio(aspecto.coerceIn(0.75f, 1.78f))
            .clip(RoundedCornerShape(16.dp))
    )
}
