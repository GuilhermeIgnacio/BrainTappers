package com.guilherme.braintappers.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.guilherme.braintappers.items
import com.guilherme.braintappers.navigation.ProfileScreen
import com.guilherme.braintappers.navigation.TriviaSettingsScreen
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

        IconButton(onClick = { navController.navigate(ProfileScreen) }) {
            Icon(Icons.Filled.Person, contentDescription = "Open profile screen")
        }

        Text(
            text = stringResource(id = R.string.home_categories_display),
            fontSize = MaterialTheme.typography.displayLarge.fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = poppinsFamily
        )

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxSize(),
                    onClick = { navController.navigate(TriviaSettingsScreen(categoryId = item.categoryId.toString())) }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            painter = painterResource(id = item.imageResId),
                            contentDescription = "${item.contentDescription} Image",
                            contentScale = ContentScale.Fit
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
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 1f),
                                    blurRadius = 3f
                                )
                            )
                        )

                    }

                }
            }
        }

    }
}

