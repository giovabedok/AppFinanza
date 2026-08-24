package com.appfinanza.cruscotto.ui.spese

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.data.model.CategorieSpesa
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.components.SchedaBordo
import com.appfinanza.cruscotto.ui.components.SelettoreMeseSemplice
import com.appfinanza.cruscotto.ui.components.coloriCampoScuro
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Senape
import androidx.compose.foundation.shape.CircleShape
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeseScreen(viewModel: SpeseViewModel) {
    val spese by viewModel.spese.collectAsStateWithLifecycle()

    var meseVisualizzato by remember { mutableStateOf(YearMonth.now()) }
    var data by remember { mutableStateOf(LocalDate.now()) }
    var descrizione by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CategorieSpesa.LISTA.first()) }
    var importoTesto by remember { mutableStateOf("") }
    var mostraDatePicker by remember { mutableStateOf(false) }
    var espansoCategoria by remember { mutableStateOf(false) }

    val delMese = spese.filter { YearMonth.from(it.data) == meseVisualizzato }.sortedByDescending { it.data }
    val totale = delMese.sumOf { it.importo }
    val importo = importoTesto.replace(",", ".").toDoubleOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Petrolio)
                .padding(16.dp)
        ) {
            Text("REGISTRA UNA SPESA DI LAVORO", style = MaterialTheme.typography.labelSmall, color = Color(0xFF9DC0C4))
            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = data.format(formatoData),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { mostraDatePicker = true }) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = "Cambia data")
                        }
                    },
                    colors = coloriCampoScuro(),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = importoTesto,
                    onValueChange = { importoTesto = it },
                    placeholder = { Text("Importo €") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = coloriCampoScuro(),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = descrizione,
                    onValueChange = { descrizione = it },
                    placeholder = { Text("Descrizione") },
                    colors = coloriCampoScuro(),
                    modifier = Modifier.weight(1f)
                )
                ExposedDropdownMenuBox(
                    expanded = espansoCategoria,
                    onExpandedChange = { espansoCategoria = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = espansoCategoria) },
                        colors = coloriCampoScuro(),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = espansoCategoria, onDismissRequest = { espansoCategoria = false }) {
                        CategorieSpesa.LISTA.forEach { nomeCategoria ->
                            DropdownMenuItem(
                                text = { Text(nomeCategoria) },
                                onClick = {
                                    categoria = nomeCategoria
                                    espansoCategoria = false
                                }
                            )
                        }
                    }
                }
            }
            Button(
                onClick = {
                    if (importo != null && importo > 0) {
                        viewModel.aggiungi(data, descrizione.trim().ifBlank { "Spesa" }, categoria, importo)
                        meseVisualizzato = YearMonth.from(data)
                        descrizione = ""
                        importoTesto = ""
                    }
                },
                enabled = importo != null && importo > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Senape),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Aggiungi", fontWeight = FontWeight.Bold)
            }
        }

        SelettoreMeseSemplice(
            mese = meseVisualizzato,
            sottoEtichetta = "${delMese.size} voci · ${formattaEuro(totale)}",
            onCambia = { meseVisualizzato = it },
            modifier = Modifier.padding(top = 20.dp)
        )

        if (delMese.isEmpty()) {
            SchedaBordo(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    "Nessuna spesa in questo mese. Aggiungi la prima voce qui sopra.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grigio,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        } else {
            Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                delMese.forEach { spesa ->
                    RigaSpesa(spesa = spesa, onElimina = { viewModel.elimina(spesa) })
                }
            }
        }
    }

    if (mostraDatePicker) {
        val statoDatePicker = rememberDatePickerState(
            initialSelectedDateMillis = data.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostraDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    statoDatePicker.selectedDateMillis?.let { millis ->
                        data = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    mostraDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { mostraDatePicker = false }) { Text("Annulla") } }
        ) {
            DatePicker(state = statoDatePicker)
        }
    }
}

@Composable
private fun RigaSpesa(spesa: Spesa, onElimina: () -> Unit) {
    SchedaBordo {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Senape.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconaCategoria(spesa.categoria), contentDescription = null, tint = Senape, modifier = Modifier.size(16.dp))
            }
            Text(
                spesa.data.format(DateTimeFormatter.ofPattern("dd/MM")),
                style = MaterialTheme.typography.labelSmall,
                color = Grigio,
                modifier = Modifier.padding(start = 10.dp, end = 8.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(spesa.descrizione, style = MaterialTheme.typography.bodyLarge)
                Text(spesa.categoria, style = MaterialTheme.typography.labelSmall, color = Grigio)
            }
            Text(formattaEuro(spesa.importo), style = MaterialTheme.typography.bodyLarge, color = Petrolio)
            IconButton(onClick = onElimina) {
                Icon(Icons.Filled.Close, contentDescription = "Elimina", tint = Grigio, modifier = Modifier.size(18.dp))
            }
        }
    }
}
