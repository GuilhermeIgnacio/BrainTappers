package com.guilherme.braintappers.domain.model

import androidx.annotation.DrawableRes

data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    val contentDescription: String
)