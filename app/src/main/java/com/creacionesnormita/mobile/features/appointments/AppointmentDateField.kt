package com.creacionesnormita.mobile.features.appointments

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creacionesnormita.mobile.core.design.Ink
import com.creacionesnormita.mobile.core.design.Line
import com.creacionesnormita.mobile.core.design.Marca
import com.creacionesnormita.mobile.core.design.SoftInk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppointmentDateField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = fechaCitaAMillis(value)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showPicker = true }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("FECHA", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            placeholder = { Text("Seleccionar fecha") },
            trailingIcon = {
                Icon(Icons.Outlined.CalendarToday, contentDescription = "Seleccionar fecha")
            },
            isError = errorText != null,
            supportingText = errorText?.let { mensaje ->
                { Text(mensaje, fontSize = 10.sp) }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Ink,
                disabledBorderColor = if (errorText != null) MaterialTheme.colorScheme.error else Line,
                disabledLabelColor = if (errorText != null) MaterialTheme.colorScheme.error else SoftInk,
                disabledPlaceholderColor = SoftInk,
                disabledTrailingIconColor = Ink,
                disabledSupportingTextColor = MaterialTheme.colorScheme.error,
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onValueChange(millisAFechaCita(millis))
                        }
                        showPicker = false
                    }
                ) {
                    Text("Aceptar", color = Marca, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancelar", color = SoftInk)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
