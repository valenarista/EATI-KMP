package edu.eati25.kmp.movies.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MoviesService(
    private val client: HttpClient
) {

    suspend fun getPopularMovies(): RemoteResult {
        return client.get("/3/discover/movie?sort_by=popularity.desc")
            .body()
    }
    suspend fun getArgMovies(): RemoteResult {
        return client.get("/3/discover/movie?sort_by=popularity.desc&with_origin_country=AR")
            .body()
    }
    suspend fun getMovieDetails(id: Int): RemoteMovie {
        return client.get("/3/movie/$id")
            .body()
    }
}