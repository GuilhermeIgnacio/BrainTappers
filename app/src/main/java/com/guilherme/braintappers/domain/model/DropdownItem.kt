package com.guilherme.braintappers.domain.model

import com.guilherme.braintappers.presentation.UiText

data class DropdownItem(
    val apiParameter: String,
    val text: UiText.StringResource,
    val onClick: () -> Unit
)