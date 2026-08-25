package com.appfinanza.cruscotto.ui.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.components.NavMese
import com.appfinanza.cruscotto.ui.components.SchedaBordo
import com.appfinanza.cruscotto.ui.theme.Blu
import com.appfinanza.cruscotto.ui.theme.Bordo
import com.appfinanza.cruscotto.ui.theme.Corallo
import com.appfinanza.cruscotto.ui.theme.Crema
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Teal
import kotlin.math.roundToInt

@Composable
fun StudioScreen(viewModel: StudioViewModel, onCambiaPeriodo: (Int, Int) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        NavMese(anno = uiState.anno, mese = uiState.mese, onCambiaPeriodo = onCambiaPeriodo)

        SchedaBordo(modifier = Modifier.padding(top = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("QUANTO È PIENO IL MESE", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.Bottom) {
                    Text(uiState.sedute.toString(), style = MaterialTheme.typography.headlineMedium, color = Petrolio)
                    Text(
                        " sedute su ${uiState.capacita} possibili",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Grigio,
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Crema)
                        .border(1.dp, Bordo, RoundedCornerShape(50))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(uiState.riempimento.coerceIn(0.0, 1.0).toFloat())
                            .fillMaxSize()
                            .clip(RoundedCornerShape(50))
                            .background(if (uiState.riempimento > 0.9) Corallo else Teal)
                    )
                }
                Text(
                    text = when {
                        uiState.sedute == 0 -> "Nessuna seduta registrata in questo mese."
                        uiState.riempimento > 0.9 -> "Agenda quasi satura. Se la richiesta resta alta, la leva non è aggiungere ore ma rivedere la tariffa."
                        else -> "Riempimento al ${(uiState.riempimento * 100).roundToInt()}%. Le sedute libere sono ${(uiState.capacita - uiState.sedute).coerceAtLeast(0)}."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grigio,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        SchedaBordo(modifier = Modifier.padding(top = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("QUANTO VALE DAVVERO UN'ORA", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Crema)
                            .padding(12.dp)
                    ) {
                        Text("Tariffa nominale", style = MaterialTheme.typography.labelSmall, color = Grigio)
                        Text(formattaEuro(uiState.orarioNominale), style = MaterialTheme.typography.titleLarge, color = Grigio, modifier = Modifier.padding(top = 2.dp))
                        Text("per ora di seduta", style = MaterialTheme.typography.labelSmall, color = Grigio, modifier = Modifier.padding(top = 2.dp))
                    }
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Corallo.copy(alpha = 0.08f))
                            .border(1.dp, Corallo.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text("Compenso reale", style = MaterialTheme.typography.labelSmall, color = Grigio)
                        Text(formattaEuro(uiState.orarioReale), style = MaterialTheme.typography.titleLarge, color = Corallo, modifier = Modifier.padding(top = 2.dp))
                        Text("su tutte le ore di lavoro", style = MaterialTheme.typography.labelSmall, color = Grigio, modifier = Modifier.padding(top = 2.dp))
                    }
                }
                Column(Modifier.padding(top = 12.dp)) {
                    Text("Ore in seduta: ${"%.1f".format(uiState.oreFatturate)}", style = MaterialTheme.typography.bodyMedium, color = Grigio)
                    Text(
                        "Ore non fatturabili: ${"%.1f".format(uiState.oreNonFatturabili)} — cartelle, supervisione, formazione, amministrazione",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Grigio,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = if (uiState.orarioReale > 0)
                            "Ogni ora dedicata al lavoro ti rende ${formattaEuro(uiState.orarioReale)} lordi. La differenza con la tariffa è il costo del lavoro che nessuno fattura."
                        else "Registra le sedute del mese per vedere il compenso orario reale.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        SchedaBordo(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("SEDUTE PER TIPO", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Column(Modifier.padding(top = 12.dp)) {
                    if (uiState.seduteTipo.isEmpty()) {
                        Text("Nessuna seduta in questo mese.", style = MaterialTheme.typography.bodyMedium, color = Grigio)
                    } else {
                        uiState.seduteTipo.forEach { voce ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(voce.tipo, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                Row(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(Crema)
                                ) {
                                    val frazione = voce.conteggio.toFloat() / uiState.sedute.coerceAtLeast(1)
                                    Box(
                                        Modifier
                                            .fillMaxWidth(frazione)
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(50))
                                            .background(Blu)
                                    )
                                }
                                Text(
                                    voce.conteggio.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Grigio,
                                    modifier = Modifier.width(26.dp).padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
