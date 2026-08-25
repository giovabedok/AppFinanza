package com.appfinanza.cruscotto.ui.ripartizione

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.components.PulsanteSecondario
import com.appfinanza.cruscotto.ui.components.SchedaBordo
import com.appfinanza.cruscotto.ui.impostazioni.ImportExportViewModel
import com.appfinanza.cruscotto.ui.impostazioni.SezioneDatiEImpostazioni
import com.appfinanza.cruscotto.ui.theme.ColoriRipartizione
import com.appfinanza.cruscotto.ui.theme.Corallo
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.RossoErrore
import com.appfinanza.cruscotto.ui.theme.Salvia
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun RipartizioneScreen(
    viewModel: RipartizioneViewModel,
    importExportViewModel: ImportExportViewModel,
    mostraMessaggio: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var testoTasse by remember { mutableStateOf("35") }
    var testoSpese by remember { mutableStateOf("10") }
    var testoStipendio by remember { mutableStateOf("40") }
    var testoFondo by remember { mutableStateOf("10") }
    var testoFuturo by remember { mutableStateOf("5") }
    var testoFondoAccumulato by remember { mutableStateOf("0") }
    var testoSpeseMedie by remember { mutableStateOf("1800") }
    var testoTariffa by remember { mutableStateOf("70") }
    var testoSeduteSettimana by remember { mutableStateOf("20") }
    var testoOreNonFatturabili by remember { mutableStateOf("8") }
    var inizializzato by remember { mutableStateOf(false) }

    var titoloNuovaScadenza by remember { mutableStateOf("") }
    var dataNuovaScadenza by remember { mutableStateOf<LocalDate?>(null) }
    var mostraDatePickerScadenza by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.caricato) {
        if (uiState.caricato && !inizializzato) {
            val s = uiState.settings
            testoTasse = formattaPercentoModificabile(s.pctTasse)
            testoSpese = formattaPercentoModificabile(s.pctSpeseProfessionali)
            testoStipendio = formattaPercentoModificabile(s.pctStipendio)
            testoFondo = formattaPercentoModificabile(s.pctFondoSicurezza)
            testoFuturo = formattaPercentoModificabile(s.pctFuturo)
            testoFondoAccumulato = formattaNumeroModificabile(s.fondoSicurezzaAccumulato)
            testoSpeseMedie = formattaNumeroModificabile(s.spesePersonaliMedie)
            testoTariffa = formattaNumeroModificabile(uiState.studio.tariffa)
            testoSeduteSettimana = formattaNumeroModificabile(uiState.studio.seduteSettimana)
            testoOreNonFatturabili = formattaNumeroModificabile(uiState.studio.oreNonFatturabiliSett)
            inizializzato = true
        }
    }

    val totalePercentuali = (testoTasse.toDoubleOrNull() ?: 0.0) +
        (testoSpese.toDoubleOrNull() ?: 0.0) +
        (testoStipendio.toDoubleOrNull() ?: 0.0) +
        (testoFondo.toDoubleOrNull() ?: 0.0) +
        (testoFuturo.toDoubleOrNull() ?: 0.0)
    val percentualiCorrette = kotlin.math.abs(totalePercentuali - 100.0) < 0.01

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
            Icon(Icons.Filled.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(end = 8.dp))
            Text("Come divido l'incasso", style = MaterialTheme.typography.titleLarge)
        }
        Text(
            "Decidi come si divide ogni euro che entra. La somma deve fare 100%.",
            style = MaterialTheme.typography.bodyMedium,
            color = Grigio,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        SchedaBordo {
            Column(Modifier.padding(16.dp)) {
                CampoPercentuale("Tasse e contributi", testoTasse, ColoriRipartizione[0]) { testoTasse = it }
                CampoPercentuale("Spese professionali", testoSpese, ColoriRipartizione[1]) { testoSpese = it }
                CampoPercentuale("Stipendio personale", testoStipendio, ColoriRipartizione[2]) { testoStipendio = it }
                CampoPercentuale("Fondo sicurezza", testoFondo, ColoriRipartizione[3]) { testoFondo = it }
                CampoPercentuale("Futuro e investimenti", testoFuturo, ColoriRipartizione[4]) { testoFuturo = it }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Totale", fontWeight = FontWeight.Bold)
                    Text(
                        "${"%.1f".format(totalePercentuali)}%",
                        fontWeight = FontWeight.Bold,
                        color = if (percentualiCorrette) Salvia else RossoErrore
                    )
                }
                Text(
                    text = if (percentualiCorrette) "Percentuali corrette: totale 100%." else "Attenzione: le percentuali non fanno 100%.",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (percentualiCorrette) Salvia else RossoErrore,
                    modifier = Modifier.padding(top = 4.dp)
                )

                PulsanteSecondario(
                    testo = "Salva percentuali",
                    abilitato = percentualiCorrette,
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = {
                        viewModel.aggiornaPercentuali(
                            pctTasse = (testoTasse.toDoubleOrNull() ?: 0.0) / 100.0,
                            pctSpeseProfessionali = (testoSpese.toDoubleOrNull() ?: 0.0) / 100.0,
                            pctStipendio = (testoStipendio.toDoubleOrNull() ?: 0.0) / 100.0,
                            pctFondoSicurezza = (testoFondo.toDoubleOrNull() ?: 0.0) / 100.0,
                            pctFuturo = (testoFuturo.toDoubleOrNull() ?: 0.0) / 100.0
                        )
                        mostraMessaggio("Percentuali salvate.")
                    }
                )
            }
        }

        TitoloSezione(Icons.Filled.MedicalServices, "Il tuo studio")
        SchedaBordo {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = testoTariffa,
                    onValueChange = { testoTariffa = it },
                    label = { Text("Tariffa a seduta (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = testoSeduteSettimana,
                    onValueChange = { testoSeduteSettimana = it },
                    label = { Text("Sedute a settimana (max)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = testoOreNonFatturabili,
                    onValueChange = { testoOreNonFatturabili = it },
                    label = { Text("Ore non fatturabili a settimana") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Nelle ore non fatturabili conta cartelle, supervisione, formazione, contabilità, telefonate e primi contatti.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Grigio,
                    modifier = Modifier.padding(top = 8.dp)
                )
                PulsanteSecondario(
                    testo = "Salva parametri studio",
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = {
                        viewModel.aggiornaStudio(
                            tariffa = testoTariffa.replace(",", ".").toDoubleOrNull() ?: 0.0,
                            seduteSettimana = testoSeduteSettimana.replace(",", ".").toDoubleOrNull() ?: 0.0,
                            oreNonFatturabiliSett = testoOreNonFatturabili.replace(",", ".").toDoubleOrNull() ?: 0.0
                        )
                        mostraMessaggio("Parametri dello studio salvati.")
                    }
                )
            }
        }

        TitoloSezione(Icons.Filled.Person, "I tuoi dati personali")
        SchedaBordo {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = testoFondoAccumulato,
                    onValueChange = { testoFondoAccumulato = it },
                    label = { Text("Fondo sicurezza accumulato (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = testoSpeseMedie,
                    onValueChange = { testoSpeseMedie = it },
                    label = { Text("Spese personali medie al mese (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                PulsanteSecondario(
                    testo = "Salva dati personali",
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = {
                        viewModel.aggiornaDatiPersonali(
                            fondoSicurezzaAccumulato = testoFondoAccumulato.replace(",", ".").toDoubleOrNull() ?: 0.0,
                            spesePersonaliMedie = testoSpeseMedie.replace(",", ".").toDoubleOrNull() ?: 0.0
                        )
                        mostraMessaggio("Dati personali salvati.")
                    }
                )
            }
        }

        TitoloSezione(Icons.Filled.Event, "Scadenze")
        SchedaBordo {
            Column(Modifier.padding(16.dp)) {
                if (uiState.scadenze.isEmpty()) {
                    Text("Nessuna scadenza registrata.", style = MaterialTheme.typography.bodyMedium, color = Grigio)
                } else {
                    uiState.scadenze.forEach { scadenza ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(scadenza.titolo, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            Text(
                                scadenza.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                style = MaterialTheme.typography.labelSmall,
                                color = Grigio
                            )
                            IconButton(onClick = { viewModel.eliminaScadenza(scadenza) }) {
                                Icon(Icons.Filled.Close, contentDescription = "Elimina scadenza", tint = Grigio, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titoloNuovaScadenza,
                        onValueChange = { titoloNuovaScadenza = it },
                        placeholder = { Text("Cosa scade") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dataNuovaScadenza?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Data") },
                        trailingIcon = {
                            IconButton(onClick = { mostraDatePickerScadenza = true }) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = "Scegli data")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                PulsanteSecondario(
                    testo = "Aggiungi scadenza",
                    abilitato = titoloNuovaScadenza.isNotBlank() && dataNuovaScadenza != null,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    onClick = {
                        val data = dataNuovaScadenza
                        if (data != null) {
                            viewModel.aggiungiScadenza(titoloNuovaScadenza.trim(), data)
                            titoloNuovaScadenza = ""
                            dataNuovaScadenza = null
                        }
                    }
                )
            }
        }

        TitoloSezione(Icons.Filled.Insights, "Le tre medie da guardare")
        SchedaBordo {
            Column(Modifier.padding(16.dp)) {
                RigaMedia("Media mensile dell'anno", uiState.mediaMensileAnno, "Il tuo reddito normale.")
                RigaMedia("Mese più basso dell'anno", uiState.meseBassoAnno, "Il livello prudenziale: taraci le spese fisse.")
                RigaMedia("Mese più alto dell'anno", uiState.meseAltoAnno, "Non usarlo mai per decidere il tuo tenore di vita.")
            }
        }

        TitoloSezione(Icons.Filled.PieChart, "I tuoi dati")
        SezioneDatiEImpostazioni(viewModel = importExportViewModel, mostraMessaggio = mostraMessaggio)

        TitoloSezione(Icons.Filled.Explore, "Informazioni")
        SchedaBordo {
            Column(Modifier.padding(16.dp)) {
                Text("Il mio studio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Petrolio)
                Text("Versione 1.0", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Text(
                    "Sviluppata da Giovanni Bedocchi",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse("mailto:giovanni@bedocchi.it") }
                            runCatching { context.startActivity(intent) }
                        }
                ) {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                    Text(
                        "giovanni@bedocchi.it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }

        Text(
            "Le percentuali qui sopra sono un punto di partenza da confermare con il commercialista.",
            style = MaterialTheme.typography.labelSmall,
            color = Grigio,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
    }

    if (mostraDatePickerScadenza) {
        val statoDatePicker = rememberDatePickerState(
            initialSelectedDateMillis = (dataNuovaScadenza ?: LocalDate.now())
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostraDatePickerScadenza = false },
            confirmButton = {
                TextButton(onClick = {
                    statoDatePicker.selectedDateMillis?.let { millis ->
                        dataNuovaScadenza = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    mostraDatePickerScadenza = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { mostraDatePickerScadenza = false }) { Text("Annulla") } }
        ) {
            DatePicker(state = statoDatePicker)
        }
    }
}

@Composable
private fun TitoloSezione(icona: ImageVector, titolo: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
    ) {
        Icon(icona, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(end = 8.dp))
        Text(titolo, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun CampoPercentuale(etichetta: String, valore: String, colore: Color, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(10.dp).background(colore, CircleShape))
            Text(etichetta, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 10.dp))
        }
        OutlinedTextField(
            value = valore,
            onValueChange = onValueChange,
            suffix = { Text("%") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.padding(start = 8.dp).widthIn(min = 96.dp)
        )
    }
}

@Composable
private fun RigaMedia(titolo: String, valore: Double, sottotitolo: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(titolo, style = MaterialTheme.typography.bodyLarge)
            Text(formattaEuro(valore), fontWeight = FontWeight.SemiBold)
        }
        Text(sottotitolo, style = MaterialTheme.typography.labelSmall, color = Grigio)
    }
}

private fun formattaPercentoModificabile(frazione: Double): String {
    val percento = frazione * 100.0
    return if (percento == percento.toLong().toDouble()) percento.toLong().toString() else percento.toString()
}

private fun formattaNumeroModificabile(numero: Double): String {
    return if (numero == numero.toLong().toDouble()) numero.toLong().toString() else numero.toString()
}
