package com.appfinanza.cruscotto.ui.cruscotto

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.components.BarChartEntrateUscite
import com.appfinanza.cruscotto.ui.components.KpiCard
import com.appfinanza.cruscotto.ui.components.PieChartRipartizione
import com.appfinanza.cruscotto.ui.theme.RossoAvviso
import com.appfinanza.cruscotto.ui.theme.VerdeAccento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CruscottoScreen(viewModel: CruscottoViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cruscotto finanziario") },
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
            SelettorePeriodo(
                anno = uiState.anno,
                mese = uiState.mese,
                onCambiaPeriodo = viewModel::cambiaPeriodo
            )

            if (!uiState.percentualiCorrette) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = RossoAvviso.copy(alpha = 0.12f))
                ) {
                    Text(
                        text = "Attenzione: le percentuali nel foglio Ripartizione non sommano al 100%.",
                        modifier = Modifier.padding(12.dp),
                        color = RossoAvviso
                    )
                }
            }

            Text(
                text = "I numeri del mese",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard("Incassi del mese", uiState.incassiDelMese, coloreValore = VerdeAccento)
                KpiCard("Spese professionali", uiState.speseProfessionaliDelMese, coloreValore = RossoAvviso)
                KpiCard("Da accantonare per tasse", uiState.daAccantonareTasse)
                KpiCard("Stipendio personale", uiState.stipendioPersonale)
                KpiCard("Fondo sicurezza", uiState.daFondoSicurezza)
                KpiCard("Futuro e investimenti", uiState.daFuturo)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Incassi totali dell'anno", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = formattaEuro(uiState.incassiTotaliAnno),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Entrate e uscite nell'anno",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                BarChartEntrateUscite(dati = uiState.datiMensili, modifier = Modifier.padding(16.dp))
            }

            Text(
                text = "Ripartizione del mese",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                PieChartRipartizione(voci = uiState.ripartizioneMese, modifier = Modifier.padding(16.dp))
            }

            Text(
                text = "La tua sicurezza",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    RigaSicurezza(
                        "Mesi di autonomia",
                        String.format("%.1f mesi", uiState.mesiAutonomia),
                        "Obiettivo: 4-6 mesi."
                    )
                    RigaSicurezza(
                        "Fondo sicurezza accumulato",
                        formattaEuro(uiState.fondoSicurezzaAccumulato),
                        "Modificabile nel foglio Ripartizione."
                    )
                    RigaSicurezza(
                        "Spese personali medie al mese",
                        formattaEuro(uiState.spesePersonaliMedie),
                        null
                    )
                }
            }

            Text(
                text = "Le percentuali di questo file sono indicazioni organizzative, non consulenza fiscale: confermale con il commercialista.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun RigaSicurezza(titolo: String, valore: String, sottotitolo: String?) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(titolo, style = MaterialTheme.typography.bodyLarge)
            Text(valore, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
        if (sottotitolo != null) {
            Text(sottotitolo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelettorePeriodo(anno: Int, mese: Int, onCambiaPeriodo: (Int, Int) -> Unit) {
    var espansoMenuMese by remember { mutableStateOf(false) }
    val nomiMesi = CruscottoUiState.NOMI_MESI

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                val nuovoMese = if (mese == 1) 12 else mese - 1
                val nuovoAnno = if (mese == 1) anno - 1 else anno
                onCambiaPeriodo(nuovoAnno, nuovoMese)
            }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Mese precedente")
            }

            ExposedDropdownMenuBox(
                expanded = espansoMenuMese,
                onExpandedChange = { espansoMenuMese = it },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                TextField(
                    value = "${nomiMesi[mese - 1]} $anno",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Periodo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = espansoMenuMese) },
                    modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = espansoMenuMese,
                    onDismissRequest = { espansoMenuMese = false }
                ) {
                    nomiMesi.forEachIndexed { indice, nome ->
                        DropdownMenuItem(
                            text = { Text("$nome $anno") },
                            onClick = {
                                onCambiaPeriodo(anno, indice + 1)
                                espansoMenuMese = false
                            }
                        )
                    }
                }
            }

            IconButton(onClick = {
                val nuovoMese = if (mese == 12) 1 else mese + 1
                val nuovoAnno = if (mese == 12) anno + 1 else anno
                onCambiaPeriodo(nuovoAnno, nuovoMese)
            }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Mese successivo")
            }
        }
    }
}
