package com.guilherme.braintappers.presentation.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.Result
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.domain.model.ApiResponse
import com.guilherme.braintappers.util.poppinsFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val viewModel = koinViewModel<HomeViewModel>()

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
                id = 3,
                imageResId = R.drawable.television,
                contentDescription = "Television"
            ),
            CarouselItem(
                id = 4,
                imageResId = R.drawable.video_games,
                contentDescription = "Video Games"
            ),
            CarouselItem(
                id = 5,
                imageResId = R.drawable.board_games,
                contentDescription = "Board Games"
            ),
            CarouselItem(
                id = 6,
                imageResId = R.drawable.computers,
                contentDescription = "Computers"
            ),
            CarouselItem(
                id = 7,
                imageResId = R.drawable.mathematics,
                contentDescription = "Mathematics"
            ),
            CarouselItem(
                id = 8,
                imageResId = R.drawable.mythology,
                contentDescription = "Mythology"
            ),
            CarouselItem(id = 9, imageResId = R.drawable.sports, contentDescription = "Sports"),
            CarouselItem(
                id = 10,
                imageResId = R.drawable.geography,
                contentDescription = "Geography"
            ),
            CarouselItem(id = 11, imageResId = R.drawable.history, contentDescription = "History"),
            CarouselItem(id = 12, imageResId = R.drawable.art, contentDescription = "Art"),
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
            CarouselItem(id = 18, imageResId = R.drawable.comics, contentDescription = "Comics"),
            CarouselItem(
                id = 19,
                imageResId = R.drawable.anime,
                contentDescription = "Animes & Manga"
            ),
            CarouselItem(
                id = 20,
                imageResId = R.drawable.cartoon,
                contentDescription = "Cartoon & Animations"
            ),
        )

        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { items.count() },
            modifier = Modifier
                .width(412.dp)
                .height(221.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            val item = items[i]

            Card(
                modifier = Modifier.fillMaxSize(),
                onClick = { /*TODO*/ }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .maskClip(MaterialTheme.shapes.extraLarge),
                        painter = painterResource(id = item.imageResId),
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, bottom = 8.dp),
                        text = item.contentDescription,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            shadow = Shadow(color = Color.Black.copy(alpha = 1f), blurRadius = 3f)
                        )
                    )
                }

            }

        }

    }
}

data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    val contentDescription: String
)

class HomeViewModel(
    private val triviaApiService: TriviaApiService
) : ViewModel() {

    init {
        viewModelScope.launch {
            when (val result = triviaApiService.fetchTriviaByCategory("")) {
                is Result.Success -> {
                    println("Result From ViewModel -> " + result.data.results)
                }

                is Result.Error -> {

                }
            }

        }
    }

}