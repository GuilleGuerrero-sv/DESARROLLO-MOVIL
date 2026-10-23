package com.creacionesnormita.mobile.features.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Perfil
import com.creacionesnormita.mobile.ui.components.AvatarPerfil

@Composable
internal fun PerfilResumenCard(perfil: Perfil?, email: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AvatarPerfil(size = 58.dp, iconSize = 28.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(perfil?.nombre ?: "Usuario en sesión", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(email, color = SoftInk, fontSize = 12.sp)
                Text(
                    text = perfil?.celular?.ifBlank { "WhatsApp no registrado" } ?: "WhatsApp no registrado",
                    color = SoftInk,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
