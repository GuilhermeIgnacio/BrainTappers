package com.guilherme.braintappers.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun CustomTopAppBar(
    title: String?,
    onReturnClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onReturnClick
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title ?: "",
            fontWeight = FontWeight.Bold,
            fontFamily = poppinsFamily
        )

    }
}