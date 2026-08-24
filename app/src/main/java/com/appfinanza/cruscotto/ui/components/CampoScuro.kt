package com.appfinanza.cruscotto.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.appfinanza.cruscotto.ui.theme.PetrolioBordoCampo
import com.appfinanza.cruscotto.ui.theme.PetrolioCampo

/** Colori dei campi di testo scuri usati dentro ai form (card in Petrolio) di Incassi e Spese. */
@Composable
fun coloriCampoScuro(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = PetrolioCampo,
    unfocusedContainerColor = PetrolioCampo,
    disabledContainerColor = PetrolioCampo,
    focusedBorderColor = PetrolioBordoCampo,
    unfocusedBorderColor = PetrolioBordoCampo,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.8f),
    cursorColor = Color.White,
    focusedPlaceholderColor = Color.White.copy(alpha = 0.55f),
    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.55f),
    focusedTrailingIconColor = Color.White,
    unfocusedTrailingIconColor = Color.White,
    focusedLeadingIconColor = Color.White,
    unfocusedLeadingIconColor = Color.White
)
