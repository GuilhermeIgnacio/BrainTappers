package com.guilherme.braintappers.presentation.screen.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.CarouselState
import androidx.lifecycle.ViewModel
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.model.CarouselItem
import com.guilherme.braintappers.items

class HomeViewModel : ViewModel() {

    @OptIn(ExperimentalMaterial3Api::class)
    val carouselState = CarouselState(itemCount = { items.size })

}