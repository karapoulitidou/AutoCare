package com.example.autocare

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { DynamicHomeScreen(navController) }
        composable("cars") { DynamicCarsScreen(navController) }
        composable("addCar") { AddCarScreen(navController) }

        composable("details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            CarDetailsScreen(navController, index)
        }

        composable("pdfViewer/{actionIndex}") { backStackEntry ->
            val actionIndex = backStackEntry.arguments?.getString("actionIndex")?.toIntOrNull() ?: 0
            PdfViewerScreen(navController, actionIndex)
        }

        composable("addAction/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            AddActionScreen(navController, index)
        }

        composable("editCar/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            EditCarScreen(navController, index)
        }

        composable("editAction/{actionIndex}") { backStackEntry ->
            val actionIndex = backStackEntry.arguments?.getString("actionIndex")?.toIntOrNull() ?: 0
            EditActionScreen(navController, actionIndex)
        }
    }
}