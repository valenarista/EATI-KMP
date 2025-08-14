package edu.eati25.kmp.movies.data

import edu.eati25.kmp.movies.data.database.MoviesDao
import kotlinx.coroutines.flow.first


class MoviesRepository(
    private val moviesService: MoviesService,
    private val moviesDao: MoviesDao
) {

    private val moviesLoadedFromApiInSession = mutableSetOf<Int>()

    suspend fun getPopularMovies(): List<Movie> {

        val moviesFromDb = moviesDao.getPopularMovies().first()

        return moviesFromDb.ifEmpty {
            val moviesFromApi = moviesService.getPopularMovies().results.map {
                it.toDomainMovie()
            }
            moviesFromApi.forEach { movie ->
                moviesLoadedFromApiInSession.add(movie.id)
            }
            moviesDao.save(moviesFromApi)
            moviesFromApi
        }
    }

    suspend fun getArgMovies(): List<Movie> {
        val moviesFromDb = moviesDao.getPopularMovies().first()

        return moviesFromDb.ifEmpty {
            val moviesFromApi = moviesService.getArgMovies().results.map {
                it.toDomainMovie()
            }
            moviesFromApi.forEach { movie ->
                moviesLoadedFromApiInSession.add(movie.id)
            }
            moviesDao.save(moviesFromApi)
            moviesFromApi
        }
    }


    suspend fun getMovieById(id: Int): MoviesWithSource {
        val movieFromDb = moviesDao.getMovieById(id)
        val dataSource = if (moviesLoadedFromApiInSession.contains(id)) {
            println("entre a api")
            DataSource.API
        } else {
            println("entre a database")
            DataSource.DATABASE
        }

        return MoviesWithSource(
            movie = movieFromDb.first(),
            dataSource = dataSource
        )
    }

    suspend fun clearDatabase() {
        moviesDao.clearAllMovies()
        moviesLoadedFromApiInSession.clear()
        println("🧹 API session set cleared!")
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
            voteAverage = voteAverage
        )

    }
}