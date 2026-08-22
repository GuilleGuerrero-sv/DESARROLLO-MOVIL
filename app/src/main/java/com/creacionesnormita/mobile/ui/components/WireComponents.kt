package com.creacionesnormita.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.SoftInk

@Composable
fun ActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, filled: Boolean = true) {
    if (filled) {
        Button(
            onClick = onClick,
            modifier = modifier.height(44.dp),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Marca, contentColor = Color.White)
        ) {
            Text(text = text.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(44.dp),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink)
        ) {
            Text(text = text.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PlaceholderLines(modifier: Modifier = Modifier, widths: List<Float> = listOf(.5f, .88f, .72f)) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        widths.forEach { width ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(width)
                    .height(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Line)
            )
        }
    }
}

@Composable
fun WireImage(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, Ink)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = SoftInk, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun StatPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = SoftInk, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DotsIndicator(active: Boolean, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(if (active && index == 0) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (active && index == 0) Ink else Line)
            )
        }
    }
}

@Composable
fun SectionDivider(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Line)
    )
}
