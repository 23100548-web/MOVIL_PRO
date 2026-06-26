package com.exchangepro.moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.exchangepro.moviles.presentation.navigation.ExchangeProNavGraph
import com.exchangepro.moviles.ui.theme.ExchangeProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExchangeProTheme {
                ExchangeProNavGraph()
            }
        }
    }
}
