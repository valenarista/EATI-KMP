package edu.eati25.kmp.movies

import edu.eati25.kmp.movies.data.database.getDataBaseBuilder
import org.koin.dsl.module

actual val nativeModule = module {
    single { getDataBaseBuilder(get()) }
}