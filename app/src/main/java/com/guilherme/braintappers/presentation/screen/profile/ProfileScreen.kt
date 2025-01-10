package com.guilherme.braintappers.presentation.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.guilherme.braintappers.R
import com.guilherme.braintappers.navigation.HomeScreen
import com.guilherme.braintappers.navigation.QuizzesPlayedScreen
import com.guilherme.braintappers.presentation.component.CustomCircularProgressIndicator
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(navController: NavController) {

    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    var isSignOutDialogVisible by remember { mutableStateOf(false) }
    var isDeleteAccountDialogVisible by remember { mutableStateOf(false) }
    var isClearHistoryDialogVisible by remember { mutableStateOf(false) }

    var isDropdownMenuOpen by remember { mutableStateOf(false) }

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

                Row {
                    IconButton(
                        onClick = { navController.navigate(HomeScreen) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return to home screen",
                        )
                    }

                    if (state.isAnonymousUser) {

                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            IconButton(
                                onClick = {
                                    isDropdownMenuOpen = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Open link account dropdown menu",
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(end = 4.dp)
                            ) {
                                DropdownMenu(
                                    expanded = isDropdownMenuOpen,
                                    onDismissRequest = {
                                        isDropdownMenuOpen = !isDropdownMenuOpen
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    containerColor = Color.Black
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Link Account With Google",
                                                fontFamily = poppinsFamily,
                                                color = Color.White
                                            )
                                        },
                                        onClick = {},
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .align(Alignment.CenterHorizontally),
                                                painter = painterResource(R.drawable.google_logo),
                                                contentDescription = "Google logo",
                                                tint = Color.White
                                            )
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Link Account With Email",
                                                fontFamily = poppinsFamily,
                                                color = Color.White
                                            )
                                        },
                                        onClick = { onEvent(ProfileEvents.OpenLinkAccountWithEmailModalBottomSheet) },
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .align(Alignment.CenterHorizontally),
                                                imageVector = Icons.Default.Email,
                                                contentDescription = "Google logo",
                                                tint = Color.White
                                            )
                                        }
                                    )
                                }
                            }
                        }

                    }
                }

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

                Spacer(modifier = Modifier.height(16.dp))

            }

            Column(modifier = Modifier.padding(16.dp)) {

                CustomSurface(
                    onClick = {
                        navController.navigate(QuizzesPlayedScreen)
                    },
                    shape = RoundedCornerShape(16.dp),
                    icon = painterResource(R.drawable.rounded_cognition_2_24),
                    iconContentDescription = "",
                    text = "Played Quizzes",
                    color = primaryColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                //Clear History Button
                CustomSurface(
                    onClick = { isClearHistoryDialogVisible = true },
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    icon = Icons.Default.History,
                    iconContentDescription = "History Icon",
                    text = "Clear History",
                    color = Color.Red
                )

                //Delete Account Button
                CustomSurface(
                    onClick = { isDeleteAccountDialogVisible = true },
                    icon = Icons.Default.DeleteForever,
                    iconContentDescription = "Delete Icon",
                    text = "Delete Account",
                    color = Color.Red
                )

                //Sign Out Button
                CustomSurface(
                    onClick = { isSignOutDialogVisible = true },
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    iconContentDescription = "Logout Icon",
                    text = "Sign Out",
                    color = Color.Red
                )
            }
        }

        ProfileModalBottomSheet(state, onEvent, navController)

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

    //Clear History Dialog
    ProfileCustomDialog(
        visibility = isClearHistoryDialogVisible,
        onDismissRequest = { isClearHistoryDialogVisible = false },
        icon = Icons.Default.History,
        iconContentDescription = "History Icon",
        title = "Clear History?",
        text = "Are you sure you want to clear all your history? All quizzes data will be lost. This action cannot be undone.",
        onConfirmClick = {
            isClearHistoryDialogVisible = false
            onEvent(ProfileEvents.OnConfirmClearHistory)
        },
        confirmButtonText = "Clear History"
    )

    CustomCircularProgressIndicator(state.isLoading)

}

