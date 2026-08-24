package com.appfinanza.cruscotto.ui.spese

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.data.model.Spesa
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.theme.Senape
import java.time.format.DateTimeFormatter

private val formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun SpeseScreen(viewModel: SpeseViewModel) {
    val spese by viewModel.spese.collectAsStateWithLifecycle()
    var mostraDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Receipt, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Spese professionali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostraDialogo = true },
                containerColor = Senape,
                contentColor = androidx.compose.ui.graphics.Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Aggiungi spesa")
            }
        }
    ) { padding ->
        if (spese.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Inbox,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Text(
                    "Nessuna spesa registrata. Tocca + per aggiungere la prima spesa professionale.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(spese, key = { it.id }) { spesa ->
                    RigaSpesa(spesa = spesa, onElimina = { viewModel.elimina(spesa) })
                }
            }
        }
    }

    if (mostraDialogo) {
        AddSpesaDialog(
            onConferma = { data, descrizione, categoria, importo ->
                viewModel.aggiungi(data, descrizione, categoria, importo)
                mostraDialogo = false
            },
            onAnnulla = { mostraDialogo = false }
        )
    }
}

@Composable
private fun RigaSpesa(spesa: Spesa, onElimina: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Senape.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(iconaCategoria(spesa.categoria), contentDescription = null, tint = Senape, modifier = Modifier.size(20.dp))
                }
                Column(Modifier.padding(start = 12.dp)) {
                    Text(spesa.descrizione, style = MaterialTheme.typography.titleMedium)
                    AssistChip(
                        onClick = {},
                        label = { Text(spesa.categoria) },
                        leadingIcon = { Icon(iconaCategoria(spesa.categoria), contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        spesa.data.format(formatoData),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formattaEuro(spesa.importo),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Senape
                )
                IconButton(onClick = onElimina) {
                    Icon(Icons.Filled.Delete, contentDescription = "Elimina", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
