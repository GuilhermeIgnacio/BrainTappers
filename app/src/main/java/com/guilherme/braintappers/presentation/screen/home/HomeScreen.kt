package com.guilherme.braintappers.presentation.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.guilherme.braintappers.R
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {

        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.Person, contentDescription = "")
        }

        Text(
            text = stringResource(id = R.string.home_categories_display),
            fontSize = MaterialTheme.typography.displayLarge.fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = poppinsFamily
        )


    }
}