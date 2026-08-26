package com.creacionesnormita.mobile.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import kotlinx.coroutines.delay

/**
 * Carrusel que avanza solo cada [intervaloMs] milisegundos, y también
 * permite deslizar manualmente. Si [imagenes] está vacío, muestra un placeholder.
 */
@Composable
fun AutoCarousel(
    imagenes: List<String>,
    modifier: Modifier = Modifier,
    intervaloMs: Long = 3500L,
) {
    if (imagenes.isEmpty()) {
        WireImage("Sin imágenes todavía", modifier = modifier)
        return
    }

    val pagerState = rememberPagerState(pageCount = { imagenes.size })

    // Avanza automáticamente, pero respeta al usuario: si el usuario está
    // arrastrando el carrusel, no interfiere hasta que suelte.
    LaunchedEffect(pagerState, imagenes.size) {
        while (true) {
            delay(intervaloMs)
            if (!pagerState.isScrollInProgress && imagenes.size > 1) {
                val siguiente = (pagerState.currentPage + 1) % imagenes.size
                pagerState.animateScrollToPage(siguiente)
            }
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { pagina ->
            AsyncImage(
                model = imagenes[pagina],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        if (imagenes.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
            ) {
                repeat(imagenes.size) { indice ->
                    val activo = indice == pagerState.currentPage
                    val tamano by animateDpAsState(if (activo) 8.dp else 6.dp, label = "dot-size")
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(tamano)
                            .clip(CircleShape)
                            .background(if (activo) Ink else Line.copy(alpha = 0.8f)),
                    )
                }
            }
        }
    }
}