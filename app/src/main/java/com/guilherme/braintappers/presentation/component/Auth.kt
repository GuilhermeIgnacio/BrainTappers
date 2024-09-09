package com.guilherme.braintappers.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.guilherme.braintappers.R
import com.guilherme.braintappers.ui.theme.primaryColor
import kotlinx.coroutines.launch

@Composable
fun Auth(
    title: String,
    onContinueWithEmailClick: () -> Unit,
    onContinueWithGoogleClick: (Credential) -> Unit,
    labelText: String,
    actionText: String,
    onTextClick: () -> Unit,
) {

//    <a target="_blank" href="https://icons8.com/icon/17949/google-logo">Google Logo</a> ícone por <a target="_blank" href="https://icons8.com">Icons8</a>

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = title,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            onClick = onContinueWithEmailClick,
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = stringResource(id = R.string.continue_with_email)
            )
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .height(40.dp),
            onClick = {

                val credentialManager = CredentialManager.create(context)

                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                coroutineScope.launch {
                    try {
                        val result = credentialManager.getCredential(
                            request = request,
                            context = context,
                        )
                        onContinueWithGoogleClick(result.credential)
                    } catch (e: GetCredentialException) {
                        e.stackTrace
                    }
                }

            },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black,
            ),
            shape = RoundedCornerShape(20)
        ) {

            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(R.drawable.google_logo),
                contentDescription = "Google Logo"
            )

            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = stringResource(id = R.string.continue_with_google)
            )

        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = labelText,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTextClick
                ),
            text = actionText,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

    }

}