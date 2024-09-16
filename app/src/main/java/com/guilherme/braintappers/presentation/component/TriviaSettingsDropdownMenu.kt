package com.guilherme.braintappers.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.model.DropdownItem
import com.guilherme.braintappers.util.poppinsFamily

@Composable
fun TriviaSettingsDropdownMenu(
    text: String,
    onClick: () -> Unit,
    isDropdownMenuOpen: Boolean,
    dropdownItems: List<DropdownItem>,
    dismissDropdownMenu: () -> Unit
) {

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontFamily = poppinsFamily)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onClick) {

                val icon by animateFloatAsState(targetValue = if (isDropdownMenuOpen) 180f else 0f)

                Icon(
                    modifier = Modifier.graphicsLayer(rotationZ = icon),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isDropdownMenuOpen) stringResource(id = R.string.close_menu) else stringResource(
                        id = R.string.open_menu
                    )
                )

            }
        }

        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = isDropdownMenuOpen,
            onDismissRequest = dismissDropdownMenu
        ) {
            dropdownItems.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.text.asString(),
                            fontFamily = poppinsFamily
                        )
                    },
                    onClick = it.onClick
                )
            }
        }
    }

    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}