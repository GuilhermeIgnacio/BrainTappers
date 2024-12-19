package com.guilherme.braintappers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.guilherme.braintappers.di.koinConfiguration
import com.guilherme.braintappers.navigation.SetupNavGraph
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        setContent {

            KoinApplication(::koinConfiguration) {
                val viewModel = koinViewModel<MainViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                splashScreen.setKeepOnScreenCondition { state.isLoading }
                MaterialTheme {
                    SetupNavGraph(
                        navController = rememberNavController(),
                        startDestination = state.startDestination!!
                    )
                }
            }
        }
    }
}