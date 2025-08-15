package edu.eati25.kmp.movies.data

import edu.eati25.kmp.movies.data.database.MoviesDao
import kotlinx.coroutines.flow.first

class MoviesRepository(
    private val moviesService: MoviesService,
    private val moviesDao: MoviesDao
) {

    private val movieDataSourceMap = mutableMapOf<Int, DataSource>()

    private val POPULAR_ID_OFFSET = 0
    private val ARGENTINA_ID_OFFSET = 10000000 // 10 millones

    suspend fun getPopularMovies(): List<Movie> {

        val allMoviesFromDb = moviesDao.getPopularMovies().first()
        val popularMoviesFromDb = allMoviesFromDb.filter { it.id < ARGENTINA_ID_OFFSET }


        return if(popularMoviesFromDb.isEmpty()) {
            val moviesFromApi = moviesService.getPopularMovies().results.map {
                it.toDomainMovie()
            }

            moviesFromApi.forEach { movie ->
                movieDataSourceMap[movie.id] = DataSource.API
            }

            moviesDao.save(moviesFromApi)
            moviesFromApi
        } else{
            popularMoviesFromDb.forEach { movie ->
                movieDataSourceMap[movie.id] = DataSource.DATABASE
            }
            popularMoviesFromDb
        }
    }

    suspend fun getArgMovies(): List<Movie> {
        val allMoviesFromDb = moviesDao.getPopularMovies().first()
        val argMoviesFromDb = allMoviesFromDb.filter { it.id >= ARGENTINA_ID_OFFSET }

        return if (argMoviesFromDb.isEmpty()) {
            val moviesFromApi = moviesService.getArgMovies().results.map { remoteMovie ->
                remoteMovie.toDomainMovie().copy(
                    id = remoteMovie.id + ARGENTINA_ID_OFFSET
                )
            }

            moviesFromApi.forEach { movie ->
                movieDataSourceMap[movie.id] = DataSource.API
            }

            moviesDao.save(moviesFromApi)
            moviesFromApi
        } else {
            argMoviesFromDb.forEach { movie ->
                movieDataSourceMap[movie.id] = DataSource.DATABASE
            }
            argMoviesFromDb
        }
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