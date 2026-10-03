package com.creacionesnormita.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.creacionesnormita.mobile.core.design.Blush
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Sage
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Perfil

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
    enabled: Boolean = true,
) {
    if (filled) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(44.dp),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Marca, contentColor = Color.White)
        ) {
            Text(text = text.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
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

/**
 * Igual que StatPill visualmente, pero clickeable y con estado de selección.
 * Se usa para filtros reales (Colección, tipo de envío en Cotizar), a diferencia
 * de StatPill que es solo una etiqueta informativa estática.
 */
@Composable
fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Marca else Color.White)
            .border(1.dp, if (selected) Marca else Line, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else SoftInk,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RoleBadge(rol: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when {
        rol.equals(Perfil.ROL_ADMINISTRADOR, ignoreCase = true) || rol.equals("admin", ignoreCase = true) -> Color(0xFFC59B27) to Color.White
        rol.equals(Perfil.ROL_EMPLEADO, ignoreCase = true) || rol.equals("empleado", ignoreCase = true) -> Blush to Ink
        else -> Sage to Ink
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = rol.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}