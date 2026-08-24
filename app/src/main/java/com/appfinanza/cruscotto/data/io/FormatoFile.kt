package com.appfinanza.cruscotto.data.io

enum class FormatoFile(val estensione: String, val mimeType: String, val etichetta: String) {
    CSV("csv", "text/csv", "CSV"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Excel"),
    TXT("txt", "text/plain", "Testo")
}

fun formatoDaEstensione(nomeFile: String): FormatoFile? {
    val nome = nomeFile.lowercase()
    return when {
        nome.endsWith(".csv") -> FormatoFile.CSV
        nome.endsWith(".xlsx") -> FormatoFile.XLSX
        nome.endsWith(".txt") -> FormatoFile.TXT
        else -> null
    }
}
