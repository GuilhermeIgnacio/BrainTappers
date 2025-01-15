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
import com.guilherme.braintappers.R
import com.guilherme.braintappers.ui.theme.primaryColor
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun Auth(
    title: String,
    onContinueWithEmailClick: () -> Unit,
    onContinueWithGoogleClick: () -> Unit,
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
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontFamily = poppinsFamily
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
                text = stringResource(id = R.string.continue_with_email),
                fontFamily = poppinsFamily
            )
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .height(40.dp),
            onClick = { onContinueWithGoogleClick() },
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
                text = stringResource(id = R.string.continue_with_google),
                fontFamily = poppinsFamily
            )

        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = labelText,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            textAlign = TextAlign.Center,
            fontFamily = poppinsFamily
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
            fontFamily = poppinsFamily
        )

    }

}