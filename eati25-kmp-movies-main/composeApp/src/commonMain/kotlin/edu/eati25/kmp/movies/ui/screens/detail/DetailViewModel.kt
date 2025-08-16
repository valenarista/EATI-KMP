package edu.eati25.kmp.movies.ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.eati25.kmp.movies.data.DataSource
import edu.eati25.kmp.movies.data.Movie
import edu.eati25.kmp.movies.data.MoviesRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val id: Int,
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    var state by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            state = UiState(isLoading = true)
            val movieWithSource = moviesRepository.getMovieById(id)
            state = UiState(
                isLoading = false,
                movie = movieWithSource.movie,
                dataSource = movieWithSource.dataSource
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
        val dataSource: DataSource? = null
    )
    fun onFavoriteClick() {
        val current = state.movie ?: return
        val updated = current.copy(isFavorite = !current.isFavorite)
        state = state.copy(movie = updated)

        viewModelScope.launch {
            moviesRepository.toggleFavorite(current)
        }
    }
}