package edu.eati25.kmp.movies.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.eati25.kmp.movies.data.Movie
import edu.eati25.kmp.movies.data.MoviesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val moviesRepository: MoviesRepository
): ViewModel() {
    var state by mutableStateOf(UiState())
        private set
    var showArgMovies by mutableStateOf(false)
        private set
    var currentFilter by mutableStateOf("")
        private set

    private var lastClearTime: Instant?= null
    private val AUTO_CLEAR_INTERVAL_MS = 30*60*1000L // 30 minutes
    private var hasInitiallyLoaded = false

    init {
        if (!hasInitiallyLoaded) {
            viewModelScope.launch {
                snapshotFlow { showArgMovies to currentFilter }
                    .flatMapLatest { (showArg, filter) ->
                        state = state.copy(isLoading = true)
                        clearIfNeeded()

                        val moviesFlow = if (showArg) {
                            moviesRepository.getArgMovies()
                        } else {
                            moviesRepository.getPopularMovies()
                        }

                        moviesFlow.map { movies ->
                            if (filter.isEmpty()) {
                                movies
                            } else {
                                movies.filter { movie ->
                                    movie.title.contains(filter, ignoreCase = true)
                                }
                            }
                        }
                    }
                    .collect { filteredMovies ->
                        state = UiState(
                            isLoading = false,
                            movies = filteredMovies
                        )
                    }
            }
            hasInitiallyLoaded = true
        }
    }

    fun toggleShowArgMovies(checked: Boolean) {
        showArgMovies = checked
    }
    fun loadFilteredMovies(query: String) {
        currentFilter = query
    }

    private fun shouldClearCache(): Boolean {
        val currentTime = Clock.System.now()
        return lastClearTime?.let { lastTime ->
            val timeDifference = (currentTime - lastTime).inWholeMilliseconds
            timeDifference >= AUTO_CLEAR_INTERVAL_MS
        } ?: true
    }

    private suspend fun clearIfNeeded() {
        if (shouldClearCache()) {
            val currentTime = Clock.System.now()
            val minutesPassed = lastClearTime?.let {
                ((currentTime - it).inWholeMilliseconds / 1000 / 60).toInt()
            } ?: 0

            println("🕐 Auto-limpieza activada - Han pasado $minutesPassed minutos")
            moviesRepository.clearDatabase()
            lastClearTime = currentTime

        } else {
            val currentTime = Clock.System.now()
            val timeLeft = AUTO_CLEAR_INTERVAL_MS - (lastClearTime?.let {
                (currentTime - it).inWholeMilliseconds
            } ?: 0)
            val minutesLeft = (timeLeft / 1000 / 60).toInt()
            println("Cache valido - Faltan $minutesLeft minutos para auto-limpieza")

        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
    )
}