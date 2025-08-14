package edu.eati25.kmp.movies.data.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.eati25.kmp.movies.data.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao {

    @Query("SELECT * FROM Movie")
    fun getPopularMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM Movie WHERE id = :id")
    fun getMovieById(id: Int): Flow<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(movies: List<Movie>)

    @Query("DELETE FROM movie")
    suspend fun clearAllMovies()
}