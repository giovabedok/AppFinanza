package com.appfinanza.cruscotto.ui.impostazioni

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.data.io.FormatoFile
import com.appfinanza.cruscotto.data.io.formatoDaEstensione
import com.appfinanza.cruscotto.ui.theme.Blu
import com.appfinanza.cruscotto.ui.theme.Corallo
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Salvia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ImpostazioniScreen(viewModel: ImportExportViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val risultatoImportazione by viewModel.risultatoImportazione.collectAsStateWithLifecycle()

    LaunchedEffect(risultatoImportazione) {
        val risultato = risultatoImportazione ?: return@LaunchedEffect
        val messaggio = if (risultato.vuoto) {
            "Nessuna riga valida trovata nel file selezionato."
        } else {
            "Importati ${risultato.incassiImportati} incassi e ${risultato.speseImportate} spese."
        }
        snackbarHostState.showSnackbar(messaggio)
        viewModel.azzeraRisultato()
    }

    fun mostraMessaggio(testo: String) {
        scope.launch { snackbarHostState.showSnackbar(testo) }
    }

    val esportaCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(FormatoFile.CSV.mimeType)) { uri ->
        uri?.let { esporta(context, it, FormatoFile.CSV, scope, viewModel, ::mostraMessaggio) }
    }
    val esportaXlsx = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(FormatoFile.XLSX.mimeType)) { uri ->
        uri?.let { esporta(context, it, FormatoFile.XLSX, scope, viewModel, ::mostraMessaggio) }
    }
    val esportaTxt = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(FormatoFile.TXT.mimeType)) { uri ->
        uri?.let { esporta(context, it, FormatoFile.TXT, scope, viewModel, ::mostraMessaggio) }
    }
    val importaFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { importa(context, it, scope, viewModel, ::mostraMessaggio) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Impostazioni")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SezioneTitolo(Icons.Filled.FileDownload, "Esporta i tuoi dati")
            Text(
                "Scarica tutti gli incassi e le spese registrati, nel formato che preferisci.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    RigaFormato(Icons.Filled.TableChart, "CSV", "Compatibile con Excel, Fogli Google e software gestionali.", Salvia) {
                        esportaCsv.launch("bussola_export.csv")
                    }
                    RigaFormato(Icons.Filled.GridOn, "Excel (.xlsx)", "Foglio di calcolo pronto da aprire.", Petrolio) {
                        esportaXlsx.launch("bussola_export.xlsx")
                    }
                    RigaFormato(Icons.Filled.Description, "TXT", "Testo semplice, leggibile ovunque.", Corallo) {
                        esportaTxt.launch("bussola_export.txt")
                    }
                }
            }

            SezioneTitolo(Icons.Filled.FileUpload, "Importa dati")
            Text(
                "Seleziona un file CSV, Excel o TXT esportato in precedenza: le voci verranno aggiunte a quelle già presenti.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedButton(
                        onClick = { importaFile.launch(arrayOf("*/*")) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Scegli un file da importare")
                    }
                    Text(
                        "Formati supportati: .csv, .xlsx, .txt",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            SezioneTitolo(Icons.Filled.Info, "Informazioni")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Explore, contentDescription = null, tint = Color.White)
                        }
                        Column(Modifier.padding(start = 12.dp)) {
                            Text("Bussola", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Versione 1.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(
                        "Il cruscotto finanziario per chi lavora in proprio: ogni euro che arriva sa già dove andare.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        "Sviluppato da Giovanni Bedocchi",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:giovanni@bedocchi.it")
                                }
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
        }
    }
}

@Composable
private fun SezioneTitolo(icona: ImageVector, titolo: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)) {
        Icon(icona, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(end = 8.dp))
        Text(titolo, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun RigaFormato(icona: ImageVector, titolo: String, descrizione: String, colore: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colore.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icona, contentDescription = null, tint = colore, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text(titolo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(descrizione, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Filled.FileDownload, contentDescription = "Esporta in $titolo", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun esporta(
    context: Context,
    uri: Uri,
    formato: FormatoFile,
    scope: kotlinx.coroutines.CoroutineScope,
    viewModel: ImportExportViewModel,
    mostraMessaggio: (String) -> Unit
) {
    scope.launch {
        val riuscito = runCatching {
            withContext(Dispatchers.IO) {
                if (formato == FormatoFile.XLSX) {
                    val contenuto = viewModel.generaContenutoXlsx()
                    context.contentResolver.openOutputStream(uri)?.use { it.write(contenuto) }
                } else {
                    val contenuto = viewModel.generaContenutoDelimitato(formato)
                    context.contentResolver.openOutputStream(uri)?.use { it.write(contenuto.toByteArray(Charsets.UTF_8)) }
                }
            }
        }.isSuccess
        mostraMessaggio(if (riuscito) "Esportazione ${formato.etichetta} completata." else "Esportazione non riuscita.")
    }
}

private fun importa(
    context: Context,
    uri: Uri,
    scope: kotlinx.coroutines.CoroutineScope,
    viewModel: ImportExportViewModel,
    mostraMessaggio: (String) -> Unit
) {
    scope.launch {
        val nomeFile = nomeVisualizzato(context, uri)
        val formato = formatoDaEstensione(nomeFile)
        if (formato == null) {
            mostraMessaggio("Formato non riconosciuto: usa un file .csv, .xlsx o .txt.")
            return@launch
        }
        val esito = runCatching {
            withContext(Dispatchers.IO) {
                if (formato == FormatoFile.XLSX) {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    bytes?.let { viewModel.importaXlsx(it) }
                } else {
                    val testo = context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                    testo?.let { viewModel.importaTesto(it, formato) }
                }
            }
        }
        if (esito.isFailure) {
            mostraMessaggio("Importazione non riuscita: controlla il file selezionato.")
        }
    }
}

private fun nomeVisualizzato(context: Context, uri: Uri): String {
    var nome = uri.lastPathSegment.orEmpty()
    runCatching {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val indice = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (indice >= 0 && cursor.moveToFirst()) {
                nome = cursor.getString(indice)
            }
        }
    }
    return nome
}
