package com.guilherme.braintappers.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guilherme.braintappers.presentation.screen.SignInScreen
import com.guilherme.braintappers.presentation.screen.SignUpScreen
import com.guilherme.braintappers.presentation.screen.WelcomeScreen
import kotlinx.serialization.Serializable

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = WelcomeScreen
    ) {
        composable<WelcomeScreen> {
            WelcomeScreen(navController = navController)
        }

        composable<SignUpScreen> {
            SignUpScreen(navController = navController)
        }

        composable<SignInScreen> {
            SignInScreen(navController = navController)
        }

    }
}

@Serializable
object WelcomeScreen

@Serializable
object SignUpScreen

@Serializable
object SignInScreen
