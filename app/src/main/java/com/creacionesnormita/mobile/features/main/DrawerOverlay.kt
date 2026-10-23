package com.creacionesnormita.mobile.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.core.design.Blush
import com.creacionesnormita.mobile.core.design.Gold
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.core.model.Perfil
import com.creacionesnormita.mobile.ui.components.ActionButton
import com.creacionesnormita.mobile.ui.components.AvatarPerfil
import com.creacionesnormita.mobile.ui.components.BrandMark
import com.creacionesnormita.mobile.ui.components.RoleBadge

@Composable
internal fun DrawerOverlay(
    perfil: Perfil?,
    userEmail: String,
    onClose: () -> Unit,
    onOpenGestionCatalogo: () -> Unit,
    onOpenGestionUsuarios: () -> Unit
) {
    val esEmpleadoOrAdmin = perfil?.esEmpleado == true
    val esAdmin = perfil?.esAdmin == true

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(.85f)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = Ink)
                }
                BrandMark()
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            // User Info Header in Drawer
            Card(
                colors = CardDefaults.cardColors(containerColor = Blush.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AvatarPerfil(size = 44.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            perfil?.nombre ?: "Cliente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Ink
                        )
                        RoleBadge(rol = perfil?.rol ?: Perfil.ROL_CLIENTE)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            if (esEmpleadoOrAdmin) {
                // --- Secciones de Gestión Exclusivas para Empleados y Administradores ---
                Text(
                    "PANEL DE GESTIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Marca,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Paper)
                        .clickable { onOpenGestionCatalogo() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Inventory, contentDescription = null, tint = Marca, modifier = Modifier.size(22.dp))
                    Text("Gestión de Catálogo", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 14.dp))
                }

                if (esAdmin) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Paper)
                            .clickable { onOpenGestionUsuarios() }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = Color(0xFFC59B27), modifier = Modifier.size(22.dp))
                        Text("Gestión de Clientes y Usuarios", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 14.dp))
                    }
                }
            } else {
                // --- Secciones Estándar para Clientes ---
                val opcionesStandard = listOf("Colección", "Citas", "Cotizaciones", "Preguntas frecuentes", "WhatsApp directo")
                opcionesStandard.forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClose() }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Outlined.StarBorder
                                1 -> Icons.Outlined.CalendarMonth
                                2 -> Icons.AutoMirrored.Outlined.ReceiptLong
                                else -> Icons.AutoMirrored.Outlined.Send
                            },
                            contentDescription = null,
                            tint = Ink,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(label, color = SoftInk, fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp))
                    }
                }
                Spacer(Modifier.weight(1f))
                ActionButton(text = "WhatsApp directo", onClick = {}, modifier = Modifier.padding(top = 22.dp).fillMaxWidth(), filled = false)
            }
        }
        Box(
            modifier = Modifier
                .weight(.15f)
                .fillMaxSize()
                .background(Gold.copy(alpha = .28f))
                .clickable { onClose() }
        )
    }
}
