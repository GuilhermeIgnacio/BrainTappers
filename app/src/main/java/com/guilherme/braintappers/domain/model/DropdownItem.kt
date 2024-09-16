package com.guilherme.braintappers.domain.model

import com.guilherme.braintappers.presentation.UiText

data class DropdownItem(
    val text: UiText.StringResource,
    val onClick: () -> Unit
)