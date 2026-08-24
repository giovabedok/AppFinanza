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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.appfinanza.cruscotto.ui.theme.Blu
import com.appfinanza.cruscotto.ui.theme.Corallo
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.RossoErrore
import com.appfinanza.cruscotto.ui.theme.Salvia
import com.appfinanza.cruscotto.ui.theme.Senape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CruscottoScreen(viewModel: CruscottoViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.Filled.Explore, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Bussola")
                    }
                },
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
                    colors = CardDefaults.cardColors(containerColor = RossoErrore.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = RossoErrore)
                        Text(
                            text = "Attenzione: le percentuali nella scheda Ripartizione non sommano al 100%.",
                            modifier = Modifier.padding(start = 8.dp),
                            color = RossoErrore
                        )
                    }
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
                KpiCard("Incassi del mese", uiState.incassiDelMese, Icons.Filled.Payments, coloreAccento = Salvia)
                KpiCard("Spese professionali", uiState.speseProfessionaliDelMese, Icons.Filled.Receipt, coloreAccento = Senape)
                KpiCard("Da accantonare per tasse", uiState.daAccantonareTasse, Icons.Filled.AccountBalance, coloreAccento = Petrolio)
                KpiCard("Stipendio personale", uiState.stipendioPersonale, Icons.Filled.Savings, coloreAccento = Corallo)
                KpiCard("Fondo sicurezza", uiState.daFondoSicurezza, Icons.Filled.Shield, coloreAccento = Blu)
                KpiCard("Futuro e investimenti", uiState.daFuturo, Icons.AutoMirrored.Filled.TrendingUp, coloreAccento = Salvia)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Incassi totali dell'anno",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = formattaEuro(uiState.incassiTotaliAnno),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
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
                        Icons.Filled.Shield,
                        "Mesi di autonomia",
                        String.format("%.1f mesi", uiState.mesiAutonomia),
                        "Obiettivo: 4-6 mesi."
                    )
                    RigaSicurezza(
                        Icons.Filled.Savings,
                        "Fondo sicurezza accumulato",
                        formattaEuro(uiState.fondoSicurezzaAccumulato),
                        "Modificabile nella scheda Ripartizione."
                    )
                    RigaSicurezza(
                        Icons.Filled.Receipt,
                        "Spese personali medie al mese",
                        formattaEuro(uiState.spesePersonaliMedie),
                        null
                    )
                }
            }

            Text(
                text = "Le percentuali di questa app sono indicazioni organizzative, non consulenza fiscale: confermale con il commercialista.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun RigaSicurezza(icona: androidx.compose.ui.graphics.vector.ImageVector, titolo: String, valore: String, sottotitolo: String?) {
    Row(Modifier.padding(vertical = 8.dp), verticalAlignment = androidx.compose.ui.Alignment.Top) {
        Icon(icona, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 2.dp))
        Column(Modifier.padding(start = 12.dp).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(titolo, style = MaterialTheme.typography.bodyLarge)
                Text(valore, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
            if (sottotitolo != null) {
                Text(sottotitolo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
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
                    leadingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
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
