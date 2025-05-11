package br.com.hospitalmaps.presentation.main.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.hospitalmaps.ui.theme.HospitalMapsAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            HospitalMapsAppTheme {
                MainScreen()
            }
        }
    }
}