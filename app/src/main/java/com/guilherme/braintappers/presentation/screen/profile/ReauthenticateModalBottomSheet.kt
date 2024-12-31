package com.guilherme.braintappers.presentation.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guilherme.braintappers.presentation.component.EmailOutlinedTextField
import com.guilherme.braintappers.presentation.component.PasswordOutlinedTextField
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ReauthenticateModalBottomSheet(
    state: ProfileState,
    onEvent: (ProfileEvents) -> Unit,
    navController: NavController
) {
    if (state.modalBottomSheetVisibility) {
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(ProfileEvents.DismissModalBottomSheet)
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                EmailOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.emailTextField,
                    onValueChange = { onEvent(ProfileEvents.OnEmailTextFieldValueChanged(it)) },
                    placeholder = "Email",
                    isEnabled = !state.isLoading,
                    isError = state.isReauthenticateWithEmailAndPasswordError,
                    errorSupportingText = ""
                )

                PasswordOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.passwordTextField,
                    onValueChange = { onEvent(ProfileEvents.OnPasswordChanged(it)) },
                    placeholder = "Password",
                    isEnabled = !state.isLoading,
                    isError = state.isReauthenticateWithEmailAndPasswordError,
                    errorSupportingText = state.errorSupportingText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {

                    Button(
                        modifier = Modifier,
                        onClick = { onEvent(ProfileEvents.DismissModalBottomSheet) },
                        shape = RoundedCornerShape(20),
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black
                        ),
                    ) {
                        Text(
                            text = "Close",
                            fontFamily = poppinsFamily
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        modifier = Modifier,
                        onClick = {
                            onEvent(
                                ProfileEvents.ReauthenticateWithEmailAndPassword(
                                    navController
                                )
                            )
                        },
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = Color.Black
                        ),
                        enabled = state.emailTextField.isNotEmpty() && state.passwordTextField.isNotEmpty() && !state.isLoading
                    ) {
                        Text(
                            text = "Re-authenticate",
                            fontFamily = poppinsFamily
                        )
                    }
                }

            }
        }
    }
}