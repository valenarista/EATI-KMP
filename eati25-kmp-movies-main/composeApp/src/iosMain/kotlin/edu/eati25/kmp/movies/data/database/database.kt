package edu.eati25.kmp.movies.data.database


import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<MoviesDatabase> {
    val dbFilePath = NSHomeDirectory() + "/$DATABASE_NAME"
    return Room.databaseBuilder<MoviesDatabase>(
        name = dbFilePath,
        factory = {
            MoviesDatabase::class.instantiateImpl()
        }
    ).setDriver(BundledSQLiteDriver())
}