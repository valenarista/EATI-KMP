package edu.eati25.kmp.movies.data.database


import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun getDatabaseBuilder() = Room.databaseBuilder<MoviesDatabase>(
    name = File(System.getProperty("java.io.tmpdir"), DATABASE_NAME).absolutePath
).setDriver(BundledSQLiteDriver())