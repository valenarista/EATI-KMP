package edu.eati25.kmp.movies.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.eati25.kmp.movies.data.Movie
import edu.eati25.kmp.movies.data.MoviesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(
    private val moviesRepository: MoviesRepository
): ViewModel() {
    var state by mutableStateOf(UiState())
        private set
    var showArgMovies by mutableStateOf(false)
        private set

    private var hasInitiallyLoaded = false

    init {
        if(!hasInitiallyLoaded) {
            loadPopularMovies()
            hasInitiallyLoaded = true
        }
    }

    fun toggleShowArgMovies(checked: Boolean) {
        showArgMovies = checked
        if (checked) {
            loadArgMovies()
        } else {
            loadPopularMovies()
        }
    }
    fun loadPopularMovies() {
        viewModelScope.launch {
            state = UiState(isLoading = true)
            moviesRepository.clearDatabase()
            delay(1000)
            state =
                UiState(
                    isLoading = false,
                    movies = moviesRepository.getPopularMovies()
                )
        }
    }

    fun loadArgMovies() {
        viewModelScope.launch {
            state = UiState(isLoading = true)
            moviesRepository.clearDatabase()
            delay(1000)
            state =
                UiState(
                    isLoading = false,
                    movies = moviesRepository.getArgMovies()
                )
        }
    }
    fun loadFilteredMovies(query: String) {
        viewModelScope.launch {
            state = UiState(isLoading = true)
            val moviesFromDb = moviesRepository.getPopularMovies().filter { it.title.contains(query, ignoreCase = true) }
            if (moviesFromDb.isNotEmpty()) {
                state = UiState(isLoading = false, movies = moviesFromDb)
            } else {
                state = UiState(isLoading = false, movies = emptyList())
            }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
    )
}