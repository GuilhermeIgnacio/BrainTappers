package com.guilherme.braintappers.presentation.screen.profile

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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(navController: NavController) {

    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
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

            //Delete Account Button
            CustomSurface(
                onClick = { TODO("Delete Account") },
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                icon = Icons.Default.DeleteForever,
                iconContentDescription = "Delete Icon",
                text = "Delete Account"
            )

            //Clear History Button
            CustomSurface(
                onClick = { TODO("Clear History") },
                shape = null,
                icon = Icons.Default.History,
                iconContentDescription = "History Icon",
                text = "Clear History"
            )

            //Sign Out Button
            CustomSurface(
                onClick = { TODO("Implement Sign Out Logic") },
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                icon = Icons.AutoMirrored.Filled.Logout,
                iconContentDescription = "Logout Icon",
                text = "Sign Out"
            )
        }
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