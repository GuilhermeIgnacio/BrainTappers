package com.guilherme.braintappers.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guilherme.braintappers.presentation.screen.signin.SignInScreen
import com.guilherme.braintappers.presentation.screen.signin.signinwithemail.SignInWithEmailScreen
import com.guilherme.braintappers.presentation.screen.signup.SignUpScreen
import com.guilherme.braintappers.presentation.screen.signup.signupwithemail.SignUpWithEmailScreen
import com.guilherme.braintappers.presentation.screen.welcome.WelcomeScreen
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

        composable<SignUpWithEmailScreen> {
            SignUpWithEmailScreen(navController = navController)
        }

        composable<SignInWithEmailScreen> {
            SignInWithEmailScreen(navController = navController)
        }

    }
}

@Serializable
object WelcomeScreen

@Serializable
object SignUpScreen

@Serializable
object SignInScreen

@Serializable
object SignUpWithEmailScreen

@Serializable
object SignInWithEmailScreen