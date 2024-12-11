package com.guilherme.braintappers.presentation.screen.trivia

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.guilherme.braintappers.navigation.HomeScreen
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun CloseTriviaDialog(
    navController: NavHostController,
    closeDialog: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Exit?",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = poppinsFamily
            )
        },
        text = {
            Text(
                text = "Are You Sure You Want To Exit? Your Progress Will Be Lost.",
                fontFamily = poppinsFamily
            )
        },
        onDismissRequest = { closeDialog() },
        confirmButton = {
            TextButton(
                onClick = {
                    // Had to do it this way since when using ViewModel the dialog would
                    // survive for about 0.5 seconds at the HomeScreen
                    closeDialog()
                    navController.navigate(HomeScreen)
                }
            ) {
                Text(
                    text = "Close",
                    fontFamily = poppinsFamily,
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { closeDialog() },
            ) {
                Text(text = "Cancel", fontFamily = poppinsFamily, color = Color.Black)
            }
        }
    )
}