package com.appfinanza.cruscotto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.appfinanza.cruscotto.ui.common.ViewModelFactory
import com.appfinanza.cruscotto.ui.navigation.AppFinanzaApp
import com.appfinanza.cruscotto.ui.theme.AppFinanzaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as AppFinanzaApplication).repository
        val factory = ViewModelFactory(repository)

        setContent {
            AppFinanzaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppFinanzaApp(factory)
                }
            }
        }
    }
}
