package com.appfinanza.cruscotto.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.appfinanza.cruscotto.data.repository.FinanzaRepository

/** Factory generica: le ViewModel dell'app hanno tutte un solo costruttore con il repository. */
class ViewModelFactory(private val repository: FinanzaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return modelClass.getConstructor(FinanzaRepository::class.java).newInstance(repository)
    }
}
