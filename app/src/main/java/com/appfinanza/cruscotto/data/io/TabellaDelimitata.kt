package com.appfinanza.cruscotto.data.io

import java.time.LocalDate
import java.util.Locale

private val INTESTAZIONI = listOf("tipo", "data", "voce", "dettaglio", "importo")

/**
 * Formato tabellare generico con separatore configurabile: usato sia per il CSV
 * (separatore ";") sia per il TXT (separatore tabulazione). Ogni campo è racchiuso
 * tra virgolette, con le virgolette interne raddoppiate, per un roundtrip sicuro.
 */
object TabellaDelimitata {

    fun scrivi(righe: List<RigaEsportata>, delimitatore: Char): String {
        val sb = StringBuilder()
        sb.append(INTESTAZIONI.joinToString(delimitatore.toString()) { quota(it, delimitatore) }).append('\n')
        righe.forEach { r ->
            val campi = listOf(
                if (r.tipo == TipoRiga.INCASSO) "incasso" else "spesa",
                r.data.toString(),
                r.voce,
                r.dettaglio,
                formattaImporto(r.importo)
            )
            sb.append(campi.joinToString(delimitatore.toString()) { quota(it, delimitatore) }).append('\n')
        }
        return sb.toString()
    }

    fun leggi(testo: String, delimitatore: Char): List<RigaEsportata> {
        val righe = testo.split("\r\n", "\n").filter { it.isNotBlank() }
        if (righe.size < 2) return emptyList()

        val intestazione = analizzaRiga(righe[0], delimitatore).map { it.trim().lowercase(Locale.ITALY) }
        fun indiceDi(nome: String) = intestazione.indexOf(nome)
        val idxTipo = indiceDi("tipo")
        val idxData = indiceDi("data")
        val idxVoce = indiceDi("voce")
        val idxDettaglio = indiceDi("dettaglio")
        val idxImporto = indiceDi("importo")
        if (idxTipo < 0 || idxData < 0 || idxImporto < 0) return emptyList()

        return righe.drop(1).mapNotNull { riga ->
            val campi = analizzaRiga(riga, delimitatore)
            val tipoTesto = campi.getOrNull(idxTipo)?.trim()?.lowercase(Locale.ITALY)
            val tipo = when (tipoTesto) {
                "incasso" -> TipoRiga.INCASSO
                "spesa" -> TipoRiga.SPESA
                else -> null
            } ?: return@mapNotNull null
            val data = campi.getOrNull(idxData)?.trim()?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                ?: return@mapNotNull null
            val importo = campi.getOrNull(idxImporto)?.trim()?.replace(",", ".")?.toDoubleOrNull()
                ?: return@mapNotNull null
            val voce = campi.getOrNull(idxVoce)?.trim().orEmpty()
            val dettaglio = campi.getOrNull(idxDettaglio)?.trim().orEmpty()
            RigaEsportata(tipo, data, voce, dettaglio, importo)
        }
    }

    private fun formattaImporto(valore: Double): String {
        return if (valore == valore.toLong().toDouble()) valore.toLong().toString()
        else valore.toString()
    }

    private fun quota(campo: String, delimitatore: Char): String =
        "\"${campo.replace("\"", "\"\"")}\""

    /** Analizza una riga in stile CSV: campi tra virgolette con eventuale delimitatore custom. */
    private fun analizzaRiga(riga: String, delimitatore: Char): List<String> {
        val campi = mutableListOf<String>()
        val corrente = StringBuilder()
        var dentroVirgolette = false
        var i = 0
        while (i < riga.length) {
            val c = riga[i]
            when {
                dentroVirgolette && c == '"' && i + 1 < riga.length && riga[i + 1] == '"' -> {
                    corrente.append('"')
                    i++
                }
                c == '"' -> dentroVirgolette = !dentroVirgolette
                c == delimitatore && !dentroVirgolette -> {
                    campi.add(corrente.toString())
                    corrente.clear()
                }
                else -> corrente.append(c)
            }
            i++
        }
        campi.add(corrente.toString())
        return campi
    }
}
