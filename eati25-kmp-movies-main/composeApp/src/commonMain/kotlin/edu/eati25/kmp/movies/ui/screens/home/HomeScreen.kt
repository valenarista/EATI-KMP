package edu.eati25.kmp.movies.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import edu.eati25.kmp.movies.data.Movie
import edu.eati25.kmp.movies.ui.screens.common.LoadingIndicator
import kmpmovies.composeapp.generated.resources.Res
import kmpmovies.composeapp.generated.resources.app_name
import kmpmovies.composeapp.generated.resources.flag_argentina
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun HomeScreen(
    onMovieClick: (Movie) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {

    MaterialTheme {
        Surface {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            val showArgMovies = viewModel.showArgMovies
            var filter by remember { mutableStateOf(viewModel.currentFilter) }
            Scaffold(
                topBar = {
                    TopAppBar(
                        { Text(stringResource(Res.string.app_name)) },
                        actions = {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                TextField(
                                    value = filter,
                                    placeholder = { Text("Search") },
                                    onValueChange = {
                                        filter = it
                                        viewModel.loadFilteredMovies( it )
                                    },
                                    readOnly = false,
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .padding(end = 8.dp)
                                    ,
                                    shape = MaterialTheme.shapes.small,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = Color(0x4fa1ed),
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                )

                                Text("AR",style = MaterialTheme.typography.bodySmall)
                                Image(
                                    painter = painterResource(Res.drawable.flag_argentina) ,
                                    contentDescription = "Arg",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .fillMaxWidth(0.05f)
                                        .aspectRatio(1f)
                                        .clip(MaterialTheme.shapes.small)
                                )
                                Switch(
                                    checked = showArgMovies,
                                    onCheckedChange = { checked ->
                                        viewModel.toggleShowArgMovies(checked)
                                    }
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) { padding ->


                val state = viewModel.state
                LoadingIndicator(state.isLoading)

                if(!state.isLoading){
                    if( viewModel.state.movies.isEmpty() && filter.isNotEmpty() )
                        ShowNoResults()
                    else if( viewModel.state.movies.isEmpty() && !showArgMovies )
                        ShowNoResults()
                    else if( viewModel.state.movies.isEmpty() && showArgMovies )
                        ShowNoResults()
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(120.dp),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(padding)
                ) {
                    items(state.movies, key = { it.id }) { movie ->
                    MovieItem(movie) { onMovieClick(movie) }
                    }
                }
            }

        }
    }
}
@Composable
private fun ShowNoResults() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No results found", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() }
    ) {
        Box {
            AsyncImage(
                model = movie.poster,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2 / 3f)
                    .clip(MaterialTheme.shapes.small)
            )
            if (movie.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
        }
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier.padding(8.dp)
        )
    }

}