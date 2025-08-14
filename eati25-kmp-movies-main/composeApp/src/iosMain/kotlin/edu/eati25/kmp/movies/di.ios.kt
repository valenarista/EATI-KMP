package edu.eati25.kmp.movies

import edu.eati25.kmp.movies.data.database.getDatabaseBuilder
import org.koin.dsl.module

actual val nativeModule = module {
    single { getDatabaseBuilder() }
}