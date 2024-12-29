package com.guilherme.braintappers.presentation.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.guilherme.braintappers.R
import com.guilherme.braintappers.presentation.component.CustomCircularProgressIndicator
import com.guilherme.braintappers.presentation.component.EmailOutlinedTextField
import com.guilherme.braintappers.presentation.component.PasswordOutlinedTextField
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KFunction1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    var isSignOutDialogVisible by remember { mutableStateOf(false) }
    var isDeleteAccountDialogVisible by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = state.snackbarHostState) }
    ) { _ ->

        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(color = primaryColor.copy(alpha = 0.5f))
                    .statusBarsPadding()
            ) {

                AsyncImage(
                    modifier = Modifier
                        .size(128.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                        .clip(CircleShape),
                    model = state.user?.photoUrl,
                    contentDescription = "Profile Picture",
                    placeholder = painterResource(R.drawable.profile_avatar_placeholder),
                    error = painterResource(R.drawable.profile_avatar_placeholder)
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "0\nQuizzes",
                        textAlign = TextAlign.Center,
                        fontFamily = poppinsFamily,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Light
                    )

                    VerticalDivider(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .height(30.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(20)),
                        thickness = 1.dp,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )

                    Text(
                        text = "0\nCorrect Answers",
                        textAlign = TextAlign.Center,
                        fontFamily = poppinsFamily,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Light
                    )

                    VerticalDivider(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .height(30.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(20)),
                        thickness = 1.dp,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )

                    Text(
                        text = "0\nRank",
                        textAlign = TextAlign.Center,
                        fontFamily = poppinsFamily,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Light
                    )

                }

                Spacer(modifier = Modifier.height(16.dp))

            }

            Column(modifier = Modifier.padding(16.dp)) {
                /*Surface(
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    shadowElevation = 16.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = ""
                        )
                        Text("Lorem Ipsum Dolor Sit Amet")
                    }
                }*/

                //Clear History Button
                CustomSurface(
                    onClick = { TODO("Open Clear History Dialog") },
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    icon = Icons.Default.History,
                    iconContentDescription = "History Icon",
                    text = "Clear History"
                )

                //Delete Account Button
                CustomSurface(
                    onClick = { isDeleteAccountDialogVisible = true },
                    shape = null,
                    icon = Icons.Default.DeleteForever,
                    iconContentDescription = "Delete Icon",
                    text = "Delete Account"
                )

                //Sign Out Button
                CustomSurface(
                    onClick = { isSignOutDialogVisible = true },
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    iconContentDescription = "Logout Icon",
                    text = "Sign Out"
                )
            }
        }

        ReauthenticateModalBottomSheet(state, onEvent, navController)

    }

    //Delete Account Dialog
    ProfileCustomDialog(
        visibility = isDeleteAccountDialogVisible,
        onDismissRequest = { isDeleteAccountDialogVisible = false },
        icon = Icons.Default.DeleteForever,
        iconContentDescription = "Delete Icon",
        title = "Delete Account",
        text = "Are you sure you want to delete your account?\nThis action is permanent and cannot be undone. All your data will be erased.",
        onConfirmClick = {
            isDeleteAccountDialogVisible = false
            onEvent(ProfileEvents.OnConfirmAccountDeletion(navController))
        },
        confirmButtonText = "Delete Account"

    )

    //Sign Out Dialog
    ProfileCustomDialog(
        visibility = isSignOutDialogVisible,
        onDismissRequest = { isSignOutDialogVisible = false },
        icon = Icons.AutoMirrored.Filled.Logout,
        iconContentDescription = "Logout Icon",
        title = "Signing Out",
        text = "Are You Sure You Want to Sign Out?",
        onConfirmClick = {
            isSignOutDialogVisible = false
            onEvent(ProfileEvents.OnConfirmSignOut(navController))
        },
        confirmButtonText = "Sign Out"
    )

    CustomCircularProgressIndicator(state.isLoading)

}

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

@Composable
fun ProfileCustomDialog(
    visibility: Boolean,
    onDismissRequest: () -> Unit,
    icon: ImageVector,
    iconContentDescription: String,
    title: String,
    text: String,
    onConfirmClick: () -> Unit,
    confirmButtonText: String
) {
    if (visibility) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    tint = Color.Red
                )
            },
            title = {
                Text(text = title, fontFamily = poppinsFamily, color = Color.Red)
            },
            text = {
                Text(text = text, fontFamily = poppinsFamily, textAlign = TextAlign.Center)
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirmClick
                ) {
                    Text(text = confirmButtonText, fontFamily = poppinsFamily, color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = "Dismiss", fontFamily = poppinsFamily)
                }
            }
        )
    }
}

@Composable
fun CustomSurface(
    onClick: () -> Unit,
    shape: Shape?,
    icon: ImageVector,
    iconContentDescription: String?,
    text: String
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shadowElevation = 16.dp,
        shape = shape ?: RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
                tint = Color.Red
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = text, fontFamily = poppinsFamily, color = Color.Red)
        }
    }
}