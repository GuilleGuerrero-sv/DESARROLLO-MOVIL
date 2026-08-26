package com.creacionesnormita.mobile.core.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta tomada del proyecto web de Creaciones Normita para que la app no se vea como algo aparte.
val Marca = Color(0xFFA83B78)
val MarcaOscura = Color(0xFF943066)
val Ink = Color(0xFF171717)
val SoftInk = Color(0xFF6F6F6F)
val Line = Color(0xFFE6DDE3)
val Paper = Color(0xFFF8F3F6)
val Surface = Color(0xFFFFFFFF)
val Blush = Color(0xFFF3D8E7)
val Sage = Color(0xFFE7EFEA)
val Gold = Color(0xFFD2AD62)

private val LightScheme = lightColorScheme(
    primary = Marca,
    onPrimary = Color.White,
    secondary = Gold,
    onSecondary = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    outline = Line
)

@Composable
fun CreacionesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
