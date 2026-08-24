package com.appfinanza.cruscotto.ui.common

import java.text.NumberFormat
import java.util.Locale

private val formatoEuro: NumberFormat = NumberFormat.getCurrencyInstance(Locale.ITALY)
private val formatoPercentuale: NumberFormat = NumberFormat.getPercentInstance(Locale.ITALY).apply {
    maximumFractionDigits = 1
}

fun formattaEuro(valore: Double): String = formatoEuro.format(valore)

fun formattaPercentuale(valore: Double): String = formatoPercentuale.format(valore)
