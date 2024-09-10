package com.guilherme.braintappers.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.guilherme.braintappers.R
import com.guilherme.braintappers.presentation.screen.SignInScreen
import com.guilherme.braintappers.presentation.screen.SignUpScreen
import com.guilherme.braintappers.presentation.screen.WelcomeScreen
import com.guilherme.braintappers.ui.theme.primaryColor
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
            SignUpWithEmailScreen(navController)
        }

        composable<SignInWithEmailScreen> {
            SignInWithEmailScreen()
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

@Composable
fun SignUpWithEmailScreen(navController: NavHostController) {

    val modifier = Modifier.padding(start = 8.dp, end = 8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { navController.navigateUp() }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }

            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.sign_up_with_email_screen_title),
                fontWeight = FontWeight.Bold
            )


        }

        //Todo: Regex Email
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = { Text(text = "Email") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
            )
        )

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = { Text(text = "Confirm Email") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
            )
        )
        //Todo: Regex Email

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = { Text(text = "Password") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
            )
        )

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            placeholder = { Text(text = "Confirm Password") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
            )
        )

        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {},
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Next"
            )
        }

    }
}


@Composable
fun SignInWithEmailScreen() {
}
