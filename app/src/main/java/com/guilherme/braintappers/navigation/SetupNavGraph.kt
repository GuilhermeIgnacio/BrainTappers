package com.guilherme.braintappers.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.guilherme.braintappers.presentation.screen.home.HomeScreen
import com.guilherme.braintappers.presentation.screen.profile.ProfileScreen
import com.guilherme.braintappers.presentation.screen.quizzesplayed.QuizzesPlayedScreen
import com.guilherme.braintappers.presentation.screen.triviasettings.TriviaSettingsScreen
import com.guilherme.braintappers.presentation.screen.signin.SignInScreen
import com.guilherme.braintappers.presentation.screen.signin.signinwithemail.SignInWithEmailScreen
import com.guilherme.braintappers.presentation.screen.signup.SignUpScreen
import com.guilherme.braintappers.presentation.screen.signup.signupwithemail.SignUpWithEmailScreen
import com.guilherme.braintappers.presentation.screen.trivia.TriviaMainScreen
import com.guilherme.braintappers.presentation.screen.welcome.WelcomeScreen
import kotlinx.serialization.Serializable

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: Any
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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

        composable<HomeScreen> {
            HomeScreen(navController = navController)
        }

        composable<ProfileScreen> {
            ProfileScreen(navController = navController)
        }

        composable<QuizzesPlayedScreen> {
            QuizzesPlayedScreen(navController = navController)
        }

        composable<TriviaSettingsScreen> {
            val args = it.toRoute<TriviaSettingsScreen>()

            TriviaSettingsScreen(
                navController = navController,
                categoryId = args.categoryId
            )
        }

        composable<TriviaScreen> {
            val args = it.toRoute<TriviaScreen>()

            TriviaMainScreen(
                navController = navController,
                categoryId = args.categoryId,
                numberOfQuestions = args.numberOfQuestions,
                difficulty = args.difficulty,
                type = args.type
            )

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

@Serializable
object HomeScreen

@Serializable
object ProfileScreen

@Serializable
object QuizzesPlayedScreen

@Serializable
data class TriviaSettingsScreen(
    val categoryId: String
)

@Serializable
data class TriviaScreen(
    val categoryId: String,
    val numberOfQuestions: String,
    val difficulty: String,
    val type: String
)

