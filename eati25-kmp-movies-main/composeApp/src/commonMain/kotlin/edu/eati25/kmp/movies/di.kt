package edu.eati25.kmp.movies

import androidx.room.RoomDatabase
import edu.eati25.kmp.movies.data.MoviesRepository
import edu.eati25.kmp.movies.data.MoviesService
import edu.eati25.kmp.movies.data.database.MoviesDao
import edu.eati25.kmp.movies.data.database.MoviesDatabase
import edu.eati25.kmp.movies.ui.screens.detail.DetailViewModel
import edu.eati25.kmp.movies.ui.screens.home.HomeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single<MoviesDao> {
        val dbBuilder = get<RoomDatabase.Builder<MoviesDatabase>>()
        dbBuilder.build().moviesDao()
    }
}

val dataModule = module {
    factoryOf(::MoviesRepository)
    factoryOf(::MoviesService)
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.themoviedb.org"
                    parameters.append("api_key", "d18da1b5da16397619c688b0263cd281")
                }
            }
        }
    }
}

val viewModelsModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
}

expect val nativeModule: Module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule, dataModule, viewModelsModule, nativeModule)
    }
}

