package com.appfinanza.cruscotto.data.io

import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory

private const val SPREADSHEET_NS = "http://schemas.openxmlformats.org/spreadsheetml/2006/main"
private val INTESTAZIONI = listOf("tipo", "data", "voce", "dettaglio", "importo")

/**
 * Lettore/scrittore XLSX minimale, senza dipendenze esterne: un file .xlsx è
 * semplicemente uno zip di parti XML (OOXML). Scriviamo un unico foglio con
 * celle a stringa inline (senza sharedStrings.xml) e leggiamo qualunque .xlsx
 * a foglio singolo con intestazione tipo/data/voce/dettaglio/importo.
 */
object XlsxFormato {

    fun scrivi(righe: List<RigaEsportata>): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            scriviVoce(zip, "[Content_Types].xml", CONTENT_TYPES)
            scriviVoce(zip, "_rels/.rels", RELS_ROOT)
            scriviVoce(zip, "xl/workbook.xml", WORKBOOK)
            scriviVoce(zip, "xl/_rels/workbook.xml.rels", WORKBOOK_RELS)
            scriviVoce(zip, "xl/worksheets/sheet1.xml", foglioXml(righe))
        }
        return out.toByteArray()
    }

    fun leggi(input: InputStream): List<RigaEsportata> {
        val voci = mutableMapOf<String, ByteArray>()
        ZipInputStream(input).use { zip ->
            var voce = zip.nextEntry
            while (voce != null) {
                if (!voce.isDirectory) voci[voce.name] = zip.readBytes()
                zip.closeEntry()
                voce = zip.nextEntry
            }
        }
        val sharedStrings = voci["xl/sharedStrings.xml"]?.let { analizzaSharedStrings(it) } ?: emptyList()
        val nomeFoglio = voci.keys
            .filter { it.startsWith("xl/worksheets/") && it.endsWith(".xml") }
            .minOrNull() ?: return emptyList()
        val bytesFoglio = voci[nomeFoglio] ?: return emptyList()
        return analizzaFoglio(bytesFoglio, sharedStrings)
    }

    private fun scriviVoce(zip: ZipOutputStream, nome: String, contenuto: String) {
        zip.putNextEntry(ZipEntry(nome))
        zip.write(contenuto.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    private fun foglioXml(righe: List<RigaEsportata>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        sb.append("<worksheet xmlns=\"$SPREADSHEET_NS\"><sheetData>")
        sb.append(rigaIntestazione())
        righe.forEachIndexed { indice, r ->
            val n = indice + 2
            sb.append("<row r=\"$n\">")
            sb.append(cellaTesto("A$n", nomeTipo(r.tipo)))
            sb.append(cellaTesto("B$n", r.data?.toString().orEmpty()))
            sb.append(cellaTesto("C$n", r.voce))
            sb.append(cellaTesto("D$n", r.dettaglio))
            sb.append(cellaNumero("E$n", r.importo))
            sb.append("</row>")
        }
        sb.append("</sheetData></worksheet>")
        return sb.toString()
    }

    private fun rigaIntestazione(): String {
        val sb = StringBuilder("<row r=\"1\">")
        INTESTAZIONI.forEachIndexed { indice, testo ->
            val colonna = ('A' + indice)
            sb.append(cellaTesto("$colonna" + "1", testo))
        }
        sb.append("</row>")
        return sb.toString()
    }

    private fun cellaTesto(riferimento: String, testo: String): String =
        "<c r=\"$riferimento\" t=\"inlineStr\"><is><t xml:space=\"preserve\">${escapaXml(testo)}</t></is></c>"

    private fun cellaNumero(riferimento: String, numero: Double): String =
        "<c r=\"$riferimento\"><v>${if (numero == numero.toLong().toDouble()) numero.toLong().toString() else numero.toString()}</v></c>"

    private fun escapaXml(testo: String): String = testo
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")

    private fun costruisciDocumento(bytes: ByteArray): Document {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        runCatching { factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true) }
        return factory.newDocumentBuilder().parse(ByteArrayInputStream(bytes))
    }

    private fun analizzaSharedStrings(bytes: ByteArray): List<String> {
        val doc = costruisciDocumento(bytes)
        val nodiSi = doc.getElementsByTagNameNS(SPREADSHEET_NS, "si")
        return (0 until nodiSi.length).map { i ->
            val si = nodiSi.item(i) as Element
            val nodiT = si.getElementsByTagNameNS(SPREADSHEET_NS, "t")
            (0 until nodiT.length).joinToString("") { nodiT.item(it).textContent }
        }
    }

    private fun analizzaFoglio(bytes: ByteArray, sharedStrings: List<String>): List<RigaEsportata> {
        val doc = costruisciDocumento(bytes)
        val righeXml = doc.getElementsByTagNameNS(SPREADSHEET_NS, "row")
        if (righeXml.length == 0) return emptyList()

        fun colonnaDiRiferimento(rif: String): String = rif.takeWhile { it.isLetter() }

        fun leggiCellaTesto(cella: Element): String {
            return when (cella.getAttribute("t")) {
                "s" -> {
                    val indice = cella.getElementsByTagNameNS(SPREADSHEET_NS, "v")
                        .item(0)?.textContent?.toIntOrNull()
                    indice?.let { sharedStrings.getOrNull(it) } ?: ""
                }
                "inlineStr" -> {
                    val nodiT = cella.getElementsByTagNameNS(SPREADSHEET_NS, "t")
                    (0 until nodiT.length).joinToString("") { nodiT.item(it).textContent }
                }
                else -> cella.getElementsByTagNameNS(SPREADSHEET_NS, "v").item(0)?.textContent ?: ""
            }
        }

        val intestazione = mutableMapOf<String, String>()
        val risultato = mutableListOf<RigaEsportata>()

        for (i in 0 until righeXml.length) {
            val riga = righeXml.item(i) as Element
            val celleXml = riga.getElementsByTagNameNS(SPREADSHEET_NS, "c")
            val valoriPerColonna = mutableMapOf<String, String>()
            for (j in 0 until celleXml.length) {
                val cella = celleXml.item(j) as Element
                val rif = cella.getAttribute("r")
                if (rif.isNullOrBlank()) continue
                valoriPerColonna[colonnaDiRiferimento(rif)] = leggiCellaTesto(cella)
            }
            if (i == 0) {
                valoriPerColonna.forEach { (colonna, testo) -> intestazione[testo.trim().lowercase()] = colonna }
                continue
            }
            fun campo(nome: String): String? = intestazione[nome]?.let { valoriPerColonna[it] }

            val tipo = tipoDaNome(campo("tipo")?.trim()) ?: continue
            val importo = campo("importo")?.trim()?.replace(",", ".")?.toDoubleOrNull() ?: continue
            val dataTesto = campo("data")?.trim().orEmpty()
            val data = if (dataTesto.isBlank()) null else analizzaData(dataTesto)
            if (tipo != TipoRiga.IMPOSTAZIONE && data == null) continue
            val voce = campo("voce")?.trim().orEmpty()
            val dettaglio = campo("dettaglio")?.trim().orEmpty()

            risultato.add(RigaEsportata(tipo, data, voce, dettaglio, importo))
        }
        return risultato
    }

    private fun nomeTipo(tipo: TipoRiga): String = when (tipo) {
        TipoRiga.INCASSO -> "incasso"
        TipoRiga.SPESA -> "spesa"
        TipoRiga.SCADENZA -> "scadenza"
        TipoRiga.IMPOSTAZIONE -> "impostazione"
    }

    private fun tipoDaNome(nome: String?): TipoRiga? = when (nome?.lowercase()) {
        "incasso" -> TipoRiga.INCASSO
        "spesa" -> TipoRiga.SPESA
        "scadenza" -> TipoRiga.SCADENZA
        "impostazione" -> TipoRiga.IMPOSTAZIONE
        else -> null
    }

    /** Accetta sia la data in testo ISO (nostro export) sia il numero seriale delle date Excel. */
    private fun analizzaData(testo: String): LocalDate? {
        runCatching { return LocalDate.parse(testo) }
        val seriale = testo.toDoubleOrNull() ?: return null
        return runCatching { LocalDate.of(1899, 12, 30).plusDays(seriale.toLong()) }.getOrNull()
    }

    private val CONTENT_TYPES = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
<Default Extension="xml" ContentType="application/xml"/>
<Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
<Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
</Types>"""

    private val RELS_ROOT = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>"""

    private val WORKBOOK = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
<sheets><sheet name="Dati" sheetId="1" r:id="rId1"/></sheets>
</workbook>"""

    private val WORKBOOK_RELS = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
</Relationships>"""
}
