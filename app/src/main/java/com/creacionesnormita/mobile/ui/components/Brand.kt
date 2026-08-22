package com.creacionesnormita.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.R
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line

@Composable
fun BrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(72.dp)
            .height(32.dp)
            .border(1.dp, Ink)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_normita),
            contentDescription = "Creaciones Normita",
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun TinyLogoText(modifier: Modifier = Modifier) {
    Text(
        text = "CREACIONES NORMITA",
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Line
        )
    )
}
