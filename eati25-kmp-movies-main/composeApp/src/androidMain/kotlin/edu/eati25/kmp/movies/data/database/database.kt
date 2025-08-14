package edu.eati25.kmp.movies.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


fun getDataBaseBuilder(context: Context): RoomDatabase.Builder <MoviesDatabase>{
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder(
        context = appContext,
        name = dbFile.absolutePath
    )
}