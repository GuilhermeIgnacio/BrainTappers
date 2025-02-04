package com.guilherme.braintappers.presentation.screen.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun CustomSurface(
    onClick: () -> Unit,
    shape: Shape = RectangleShape,
    icon: ImageVector,
    iconContentDescription: String?,
    text: String,
    color: Color
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shadowElevation = 16.dp,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
                tint = color
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = text, fontFamily = poppinsFamily, color = color)
        }
    }
}

@Composable
fun CustomSurface(
    onClick: () -> Unit,
    shape: Shape = RectangleShape,
    icon: Painter,
    iconContentDescription: String?,
    text: String,
    color: Color
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shadowElevation = 16.dp,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = iconContentDescription,
                tint = color
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = text, fontFamily = poppinsFamily, color = color)
        }
    }
}