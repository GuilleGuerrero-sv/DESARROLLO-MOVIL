package com.creacionesnormita.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.creacionesnormita.mobile.core.design.Blush
import com.creacionesnormita.mobile.core.design.Ink

/** Círculo con el icono de persona. Se usa en el perfil de la cuenta, el resumen de citas y el menú lateral. */
@Composable
fun AvatarPerfil(size: Dp, modifier: Modifier = Modifier, iconSize: Dp = 24.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Blush),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Outlined.Person, contentDescription = null, tint = Ink, modifier = Modifier.size(iconSize))
    }
}
