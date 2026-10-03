package com.creacionesnormita.mobile.features.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.R
import com.creacionesnormita.mobile.core.design.Blush
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.Paper
import com.creacionesnormita.mobile.core.design.SoftInk
import com.creacionesnormita.mobile.ui.components.ActionButton

/**
 * Pantalla que se muestra cuando el administrador desactivó la cuenta (profiles.activo = false).
 * Usa el mismo fondo (Paper) y el mismo bloque de logo (Blush) que la pantalla de login.
 */
@Composable
fun PantallaCuentaSuspendida(onVolverAlLogin: () -> Unit) {
    // El botón "atrás" del sistema también lleva al login (no hay a dónde más regresar).
    BackHandler(onBack = onVolverAlLogin)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Blush),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_normita),
                    contentDescription = "Creaciones Normita",
                    modifier = Modifier.fillMaxWidth(.58f),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "¡Oops!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Marca
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tu cuenta ha sido suspendida. No podrás volver a iniciar sesión con ella, " +
                        "pero puedes crear una nueva cuando quieras.",
                color = SoftInk,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
            )

            ActionButton(
                text = "Volver al login",
                onClick = onVolverAlLogin,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}