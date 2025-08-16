package edu.eati25.kmp.movies.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.eati25.kmp.movies.data.Movie

const val DATABASE_NAME = "movies3_db"
interface DB {
    fun clearAllTables()
}

@Database(entities = [Movie::class], version = 1)
abstract class MoviesDatabase : RoomDatabase(), DB {
    abstract fun moviesDao(): MoviesDao

    override fun clearAllTables() {

    }
}