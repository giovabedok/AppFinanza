package com.appfinanza.cruscotto.ui.ripartizione

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.theme.RossoAvviso
import com.appfinanza.cruscotto.ui.theme.VerdeAccento

@Composable
fun RipartizioneScreen(viewModel: RipartizioneViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var testoTasse by remember { mutableStateOf("35") }
    var testoSpese by remember { mutableStateOf("10") }
    var testoStipendio by remember { mutableStateOf("40") }
    var testoFondo by remember { mutableStateOf("10") }
    var testoFuturo by remember { mutableStateOf("5") }
    var testoFondoAccumulato by remember { mutableStateOf("0") }
    var testoSpeseMedie by remember { mutableStateOf("1800") }
    var inizializzato by remember { mutableStateOf(false) }

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
            inizializzato = true
        }
    }

    val totalePercentuali = (testoTasse.toDoubleOrNull() ?: 0.0) +
        (testoSpese.toDoubleOrNull() ?: 0.0) +
        (testoStipendio.toDoubleOrNull() ?: 0.0) +
        (testoFondo.toDoubleOrNull() ?: 0.0) +
        (testoFuturo.toDoubleOrNull() ?: 0.0)
    val percentualiCorrette = kotlin.math.abs(totalePercentuali - 100.0) < 0.01

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ripartizione") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "Come divido l'incasso",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "Decidi come si divide ogni euro che entra. La somma deve fare 100%.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    CampoPercentuale("Tasse e contributi", testoTasse) { testoTasse = it }
                    CampoPercentuale("Spese professionali", testoSpese) { testoSpese = it }
                    CampoPercentuale("Stipendio personale", testoStipendio) { testoStipendio = it }
                    CampoPercentuale("Fondo sicurezza", testoFondo) { testoFondo = it }
                    CampoPercentuale("Futuro e investimenti", testoFuturo) { testoFuturo = it }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Totale", fontWeight = FontWeight.Bold)
                        Text(
                            "${"%.1f".format(totalePercentuali)}%",
                            fontWeight = FontWeight.Bold,
                            color = if (percentualiCorrette) VerdeAccento else RossoAvviso
                        )
                    }
                    Text(
                        text = if (percentualiCorrette) "Percentuali corrette: totale 100%." else "Attenzione: le percentuali non fanno 100%.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (percentualiCorrette) VerdeAccento else RossoAvviso,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Button(
                        enabled = percentualiCorrette,
                        modifier = Modifier.padding(top = 12.dp),
                        onClick = {
                            viewModel.aggiornaPercentuali(
                                pctTasse = (testoTasse.toDoubleOrNull() ?: 0.0) / 100.0,
                                pctSpeseProfessionali = (testoSpese.toDoubleOrNull() ?: 0.0) / 100.0,
                                pctStipendio = (testoStipendio.toDoubleOrNull() ?: 0.0) / 100.0,
                                pctFondoSicurezza = (testoFondo.toDoubleOrNull() ?: 0.0) / 100.0,
                                pctFuturo = (testoFuturo.toDoubleOrNull() ?: 0.0) / 100.0
                            )
                        }
                    ) { Text("Salva percentuali") }
                }
            }

            Text(
                "I tuoi dati personali",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = testoFondoAccumulato,
                        onValueChange = { testoFondoAccumulato = it },
                        label = { Text("Fondo sicurezza accumulato (€)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = testoSpeseMedie,
                        onValueChange = { testoSpeseMedie = it },
                        label = { Text("Spese personali medie al mese (€)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        modifier = Modifier.padding(top = 12.dp),
                        onClick = {
                            viewModel.aggiornaDatiPersonali(
                                fondoSicurezzaAccumulato = testoFondoAccumulato.replace(",", ".").toDoubleOrNull() ?: 0.0,
                                spesePersonaliMedie = testoSpeseMedie.replace(",", ".").toDoubleOrNull() ?: 0.0
                            )
                        }
                    ) { Text("Salva dati personali") }
                }
            }

            Text(
                "Le tre medie da guardare",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    RigaMedia("Media mensile dell'anno", uiState.mediaMensileAnno, "Il tuo reddito normale.")
                    RigaMedia("Mese più basso dell'anno", uiState.meseBassoAnno, "Il livello prudenziale: taraci le spese fisse.")
                    RigaMedia("Mese più alto dell'anno", uiState.meseAltoAnno, "Non usarlo mai per decidere il tuo tenore di vita.")
                }
            }

            Text(
                "Le percentuali qui sopra sono un punto di partenza da confermare con il commercialista.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun CampoPercentuale(etichetta: String, valore: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etichetta, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(
            value = valore,
            onValueChange = onValueChange,
            suffix = { Text("%") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
        Text(sottotitolo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formattaPercentoModificabile(frazione: Double): String {
    val percento = frazione * 100.0
    return if (percento == percento.toLong().toDouble()) percento.toLong().toString() else percento.toString()
}

private fun formattaNumeroModificabile(numero: Double): String {
    return if (numero == numero.toLong().toDouble()) numero.toLong().toString() else numero.toString()
}
