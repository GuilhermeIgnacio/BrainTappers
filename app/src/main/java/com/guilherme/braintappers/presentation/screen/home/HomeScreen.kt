package com.guilherme.braintappers.presentation.screen.home

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
import androidx.navigation.NavController
import com.guilherme.braintappers.R
import com.guilherme.braintappers.util.poppinsFamily
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

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

        HorizontalMultiBrowseCarousel(
            state = viewModel.carouselState,
            modifier = Modifier
                .width(412.dp)
                .height(221.dp),
            preferredItemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            val item = viewModel.items[i]

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