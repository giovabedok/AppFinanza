package com.appfinanza.cruscotto.ui.movimenti

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.appfinanza.cruscotto.data.model.Incasso
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.data.model.TipiSeduta
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.components.NavMese
import com.appfinanza.cruscotto.ui.components.SchedaBordo
import com.appfinanza.cruscotto.ui.components.coloriCampoScuro
import com.appfinanza.cruscotto.ui.incassi.IncassiViewModel
import com.appfinanza.cruscotto.ui.navigation.PillTabBar
import com.appfinanza.cruscotto.ui.spese.SpeseViewModel
import com.appfinanza.cruscotto.ui.spese.iconaCategoria
import com.appfinanza.cruscotto.ui.theme.Corallo
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Salvia
import com.appfinanza.cruscotto.ui.theme.Senape
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private enum class TipoMovimento { INCASSI, SPESE }
private val formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimentiScreen(
    incassiViewModel: IncassiViewModel,
    speseViewModel: SpeseViewModel,
    tariffa: Double,
    anno: Int,
    mese: Int,
    onCambiaPeriodo: (Int, Int) -> Unit
) {
    val incassi by incassiViewModel.incassi.collectAsStateWithLifecycle()
    val spese by speseViewModel.spese.collectAsStateWithLifecycle()

    var tipo by rememberSaveable { mutableStateOf(TipoMovimento.INCASSI) }
    var data by remember { mutableStateOf(LocalDate.now()) }
    var campoA by remember { mutableStateOf("") }
    var campoB by remember { mutableStateOf(TipiSeduta.LISTA.first()) }
    var importoTesto by remember { mutableStateOf("") }
    var mostraDatePicker by remember { mutableStateOf(false) }
    var espansoDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(tipo) {
        campoA = ""
        importoTesto = ""
        campoB = if (tipo == TipoMovimento.INCASSI) TipiSeduta.LISTA.first() else CategorieSpesa.LISTA.first()
    }

    val eInc = tipo == TipoMovimento.INCASSI
    val codiceMese = anno * 100 + mese
    val listaIncassi = incassi.filter { it.codiceMese == codiceMese }.sortedByDescending { it.data }
    val listaSpese = spese.filter { it.codiceMese == codiceMese }.sortedByDescending { it.data }
    val numeroVoci = if (eInc) listaIncassi.size else listaSpese.size
    val totale = if (eInc) listaIncassi.sumOf { it.importo } else listaSpese.sumOf { it.importo }
    val importo = importoTesto.replace(",", ".").toDoubleOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        PillTabBar(
            etichette = listOf("Sedute incassate", "Spese di studio"),
            indiceSelezionato = if (eInc) 0 else 1,
            onSeleziona = { indice -> tipo = if (indice == 0) TipoMovimento.INCASSI else TipoMovimento.SPESE }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Petrolio)
                .padding(16.dp)
        ) {
            Text(
                if (eInc) "REGISTRA UNA SEDUTA PAGATA" else "REGISTRA UNA SPESA DI STUDIO",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9DC0C4)
            )
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
                    placeholder = { Text(if (eInc) "Importo (${formattaEuro(tariffa)})" else "Importo €") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = coloriCampoScuro(),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = campoA,
                    onValueChange = { campoA = it },
                    placeholder = { Text(if (eInc) "Paziente o sigla" else "Descrizione") },
                    colors = coloriCampoScuro(),
                    modifier = Modifier.weight(1f)
                )
                ExposedDropdownMenuBox(
                    expanded = espansoDropdown,
                    onExpandedChange = { espansoDropdown = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = campoB,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = espansoDropdown) },
                        colors = coloriCampoScuro(),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = espansoDropdown, onDismissRequest = { espansoDropdown = false }) {
                        (if (eInc) TipiSeduta.LISTA else CategorieSpesa.LISTA).forEach { voce ->
                            DropdownMenuItem(text = { Text(voce) }, onClick = { campoB = voce; espansoDropdown = false })
                        }
                    }
                }
            }
            if (eInc) {
                Text(
                    "Usa la tariffa abituale di ${formattaEuro(tariffa)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9DC0C4),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable { importoTesto = formattaNumeroSemplice(tariffa) }
                )
            }
            Button(
                onClick = {
                    if (importo != null && importo > 0) {
                        if (eInc) {
                            incassiViewModel.aggiungi(data, campoA.trim().ifBlank { "Senza nome" }, campoB, importo)
                        } else {
                            speseViewModel.aggiungi(data, campoA.trim().ifBlank { "Spesa" }, campoB, importo)
                        }
                        if (data.year * 100 + data.monthValue != codiceMese) onCambiaPeriodo(data.year, data.monthValue)
                        campoA = ""
                        importoTesto = ""
                    }
                },
                enabled = importo != null && importo > 0,
                colors = ButtonDefaults.buttonColors(containerColor = if (eInc) Corallo else Senape),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Aggiungi", fontWeight = FontWeight.Bold)
            }
        }

        NavMese(
            anno = anno,
            mese = mese,
            onCambiaPeriodo = onCambiaPeriodo,
            sottoEtichetta = "$numeroVoci voci · ${formattaEuro(totale)}",
            modifier = Modifier.padding(top = 20.dp)
        )

        if (numeroVoci == 0) {
            SchedaBordo(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    if (eInc) "Nessuna seduta in questo mese. Aggiungi la prima voce qui sopra."
                    else "Nessuna spesa in questo mese. Aggiungi la prima voce qui sopra.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grigio,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        } else if (eInc) {
            Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listaIncassi.forEach { incasso -> RigaIncasso(incasso) { incassiViewModel.elimina(incasso) } }
            }
        } else {
            Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listaSpese.forEach { spesa -> RigaSpesa(spesa) { speseViewModel.elimina(spesa) } }
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
private fun RigaIncasso(incasso: Incasso, onElimina: () -> Unit) {
    SchedaBordo {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).background(Salvia.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Payments, contentDescription = null, tint = Salvia, modifier = Modifier.size(16.dp))
            }
            Text(
                incasso.data.format(DateTimeFormatter.ofPattern("dd/MM")),
                style = MaterialTheme.typography.labelSmall,
                color = Grigio,
                modifier = Modifier.padding(start = 10.dp, end = 8.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(incasso.cliente, style = MaterialTheme.typography.bodyLarge)
                Text(incasso.prestazione, style = MaterialTheme.typography.labelSmall, color = Grigio)
            }
            Text(formattaEuro(incasso.importo), style = MaterialTheme.typography.bodyLarge, color = Petrolio)
            IconButton(onClick = onElimina) {
                Icon(Icons.Filled.Close, contentDescription = "Elimina", tint = Grigio, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun RigaSpesa(spesa: Spesa, onElimina: () -> Unit) {
    SchedaBordo {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).background(Senape.copy(alpha = 0.16f), CircleShape),
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

private fun formattaNumeroSemplice(numero: Double): String =
    if (numero == numero.toLong().toDouble()) numero.toLong().toString() else numero.toString()
