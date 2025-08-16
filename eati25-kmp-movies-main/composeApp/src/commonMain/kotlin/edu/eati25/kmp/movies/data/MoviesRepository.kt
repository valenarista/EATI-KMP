package edu.eati25.kmp.movies.data

import edu.eati25.kmp.movies.data.database.MoviesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MoviesRepository(
    private val moviesService: MoviesService,
    private val moviesDao: MoviesDao
) {

    private val movieDataSourceMap = mutableMapOf<Int, DataSource>()

    private val ARGENTINA_ID_OFFSET = 10000000

    fun getPopularMovies(): Flow<List<Movie>> = flow {
        var firstLoadFromApi = false

        val allMoviesFromDb = moviesDao.getPopularMovies().first()
        val popularMoviesFromDb = allMoviesFromDb.filter { it.id < ARGENTINA_ID_OFFSET }

        if (popularMoviesFromDb.isEmpty()) {
            val moviesFromApi = moviesService.getPopularMovies().results.map {
                it.toDomainMovie()
            }

            moviesFromApi.forEach { movie ->
                movieDataSourceMap[movie.id] = DataSource.API
            }

            emit(moviesFromApi)
            firstLoadFromApi = true

            moviesDao.save(moviesFromApi)
        }

        emitAll(
            moviesDao.getPopularMovies().map { dbMovies ->
                val filtered = dbMovies.filter { it.id < ARGENTINA_ID_OFFSET }

                if (firstLoadFromApi && filtered.isNotEmpty()) {
                    firstLoadFromApi = false
                    return@map filtered
                }

                filtered.forEach { movie ->
                    movieDataSourceMap[movie.id] = DataSource.DATABASE
                }
                filtered
            }
        )
    }
    fun getArgMovies(): Flow<List<Movie>> = flow {
        var firstLoadFromApi = false

        val allMoviesFromDb = moviesDao.getPopularMovies().first()
        val argMoviesFromDb = allMoviesFromDb.filter { it.id >= ARGENTINA_ID_OFFSET }

        if (argMoviesFromDb.isEmpty()) {
            val moviesFromApi = moviesService.getArgMovies().results.map { remoteMovie ->
                remoteMovie.toDomainMovie().copy(
                    id = remoteMovie.id + ARGENTINA_ID_OFFSET
                )
            }

            moviesFromApi.forEach { movie ->
                movieDataSourceMap[movie.id] = DataSource.API
            }

            emit(moviesFromApi)
            firstLoadFromApi = true

            moviesDao.save(moviesFromApi)
        }

        emitAll(
            moviesDao.getPopularMovies().map { dbMovies ->
                val filtered = dbMovies.filter { it.id >= ARGENTINA_ID_OFFSET }

                if (firstLoadFromApi && filtered.isNotEmpty()) {
                    firstLoadFromApi = false
                    return@map filtered
                }

                filtered.forEach { movie ->
                    movieDataSourceMap[movie.id] = DataSource.DATABASE
                }
                filtered
            }
        )
    }



    suspend fun getMovieById(id: Int): MoviesWithSource {
        val movieFromDb = moviesDao.getMovieById(id)
        val dataSource = movieDataSourceMap[id] ?: DataSource.DATABASE

        when(dataSource){
            DataSource.API -> println("✅ Movie ID $id loaded from API")
            DataSource.DATABASE -> println("💾 Movie ID $id loaded from DATABASE")
        }

        return MoviesWithSource(
            movie = movieFromDb.first(),
            dataSource = dataSource
        )
    }


    suspend fun clearDatabase() {
        moviesDao.clearAllMovies()
        movieDataSourceMap.clear()
        println("🧹 Database and DataSource map cleared!")
    }

    suspend fun toggleFavorite(movie:Movie){
        moviesDao.save(listOf(movie.copy(isFavorite = !movie.isFavorite)))
    }

    private fun RemoteMovie.toDomainMovie(): Movie {
        return Movie(
            id=id,
            title=title,
            overview=overview,
            releaseDate = releaseDate,
            poster = "https://image.tmdb.org/t/p/w185$posterPath",
            backdrop = backdropPath.let {"https://image.tmdb.org/t/p/w780$it" },
            originalTitle = originalTitle,
            originalLanguage = originalLanguage,
            popularity = popularity,
            voteAverage = voteAverage,
            isFavorite = false
        )
    }
}