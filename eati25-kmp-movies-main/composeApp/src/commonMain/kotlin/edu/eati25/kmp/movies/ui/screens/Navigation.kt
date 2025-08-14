package edu.eati25.kmp.movies.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.eati25.kmp.movies.ui.screens.detail.DetailScreen
import edu.eati25.kmp.movies.ui.screens.home.HomeScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onMovieClick = {
                    navController.navigate("detail/${it.id}")
                },
            )
        }
        composable(
            route = "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backstackEntry ->
            val movieId = backstackEntry.arguments?.getInt("movieId")

            movieId?.let {
                DetailScreen(
                    viewModel = koinViewModel(parameters = { parametersOf(movieId) }),
                    onBack = { navController.popBackStack() })
            }
        }
    }
}