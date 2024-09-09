package com.guilherme.braintappers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.guilherme.braintappers.di.koinConfiguration
import com.guilherme.braintappers.navigation.SetupNavGraph
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KoinApplication(::koinConfiguration) {
                MaterialTheme {
                    SetupNavGraph(navController = rememberNavController())
                }
            }
        }
    }
}