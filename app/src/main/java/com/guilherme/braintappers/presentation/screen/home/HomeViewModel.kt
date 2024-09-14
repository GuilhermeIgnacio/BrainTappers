package com.guilherme.braintappers.presentation.screen.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.CarouselState
import androidx.lifecycle.ViewModel
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.model.CarouselItem

class HomeViewModel: ViewModel() {

    val items = listOf(
        CarouselItem(
            id = 0,
            imageResId = R.drawable.general_knowledge,
            contentDescription = "General Knowledge"
        ),
        CarouselItem(id = 1, imageResId = R.drawable.book, contentDescription = "Books"),
        CarouselItem(id = 2, imageResId = R.drawable.film, contentDescription = "Films"),
        CarouselItem(id = 3, imageResId = R.drawable.music, contentDescription = "Music"),
        CarouselItem(
            id = 4,
            imageResId = R.drawable.television,
            contentDescription = "Television"
        ),
        CarouselItem(
            id = 5,
            imageResId = R.drawable.video_games,
            contentDescription = "Video Games"
        ),
        CarouselItem(
            id = 6,
            imageResId = R.drawable.board_games,
            contentDescription = "Board Games"
        ),
        CarouselItem(
            id = 7,
            imageResId = R.drawable.computers,
            contentDescription = "Computers"
        ),
        CarouselItem(
            id = 8,
            imageResId = R.drawable.mathematics,
            contentDescription = "Mathematics"
        ),
        CarouselItem(
            id = 9,
            imageResId = R.drawable.mythology,
            contentDescription = "Mythology"
        ),
        CarouselItem(id = 10, imageResId = R.drawable.sports, contentDescription = "Sports"),
        CarouselItem(
            id = 11,
            imageResId = R.drawable.geography,
            contentDescription = "Geography"
        ),
        CarouselItem(id = 12, imageResId = R.drawable.history, contentDescription = "History"),
        CarouselItem(id = 13, imageResId = R.drawable.art, contentDescription = "Art"),
        CarouselItem(
            id = 14,
            imageResId = R.drawable.celebrities,
            contentDescription = "Celebrities"
        ),
        CarouselItem(id = 15, imageResId = R.drawable.animals, contentDescription = "Animals"),
        CarouselItem(
            id = 16,
            imageResId = R.drawable.vehicles,
            contentDescription = "Vehicles"
        ),
        CarouselItem(id = 17, imageResId = R.drawable.comics, contentDescription = "Comics"),
        CarouselItem(
            id = 18,
            imageResId = R.drawable.anime,
            contentDescription = "Animes & Manga"
        ),
        CarouselItem(
            id = 19,
            imageResId = R.drawable.cartoon,
            contentDescription = "Cartoon & Animations"
        ),
    )

    @OptIn(ExperimentalMaterial3Api::class)
    val carouselState = CarouselState(itemCount = { items.size })


}