package com.appfinanza.cruscotto.ui.incassi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncassoDialog(
    onConferma: (data: LocalDate, cliente: String, prestazione: String, importo: Double) -> Unit,
    onAnnulla: () -> Unit
) {
    var cliente by remember { mutableStateOf("") }
    var prestazione by remember { mutableStateOf("") }
    var importoTesto by remember { mutableStateOf("") }
    var mostraDatePicker by remember { mutableStateOf(false) }
    var dataSelezionata by remember { mutableStateOf(LocalDate.now()) }

    val importo = importoTesto.replace(",", ".").toDoubleOrNull()
    val valido = cliente.isNotBlank() && prestazione.isNotBlank() && importo != null && importo > 0

    AlertDialog(
        onDismissRequest = onAnnulla,
        title = { Text("Nuovo incasso") },
        text = {
            Column {
                OutlinedTextField(
                    value = dataSelezionata.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    trailingIcon = {
                        TextButton(onClick = { mostraDatePicker = true }) { Text("Cambia") }
                    }
                )
                OutlinedTextField(
                    value = cliente,
                    onValueChange = { cliente = it },
                    label = { Text("Cliente") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = prestazione,
                    onValueChange = { prestazione = it },
                    label = { Text("Prestazione") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = importoTesto,
                    onValueChange = { importoTesto = it },
                    label = { Text("Incasso (€)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = { onConferma(dataSelezionata, cliente.trim(), prestazione.trim(), importo ?: 0.0) }
            ) { Text("Salva") }
        },
        dismissButton = {
            TextButton(onClick = onAnnulla) { Text("Annulla") }
        }
    )

    if (mostraDatePicker) {
        val statoDatePicker = rememberDatePickerState(
            initialSelectedDateMillis = dataSelezionata.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostraDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    statoDatePicker.selectedDateMillis?.let { millis ->
                        dataSelezionata = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    mostraDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostraDatePicker = false }) { Text("Annulla") }
            }
        ) {
            DatePicker(state = statoDatePicker)
        }
    }
}
