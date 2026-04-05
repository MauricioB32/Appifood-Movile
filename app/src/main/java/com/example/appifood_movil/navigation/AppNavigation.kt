package com.example.appifood_movil.navigation

import com.example.appifood_movil.ui.screens.HomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.appifood_movil.ui.screens.SplashLoginScreen
import com.example.appifood_movil.ui.screens.AuthScreen
import com.example.appifood_movil.ui.screens.CartScreen
import com.example.appifood_movil.ui.screens.ProductDetailScreen
import com.example.appifood_movil.ui.screens.ProfileScreen
import com.example.appifood_movil.ui.screens.RestaurantDetailScreen
import com.example.appifood_movil.ui.screens.OrderHistoryScreen
import com.example.appifood_movil.ui.screens.FavoritesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashLoginScreen(
                onLoginClick = { navController.navigate("auth") },
                onSignUpClick = { navController.navigate("auth") }
            )
        }

        composable("auth") {
            AuthScreen(onLoginNavigation = {
                navController.navigate("home") {
                    popUpTo("auth") { inclusive = true }
                }
            })
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("restaurantDetail/{nombre}") { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")
            RestaurantDetailScreen(navController, nombre ?: "")
        }
        composable("cart") { CartScreen(navController) }

        composable(
            route = "productDetail/{nombre}/{precio}/{imagen}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("precio") { type = NavType.StringType },
                navArgument("imagen") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val precio = backStackEntry.arguments?.getString("precio") ?: ""
            val imagen = backStackEntry.arguments?.getInt("imagen") ?: 0
            ProductDetailScreen(navController, nombre, precio, imagen)
        }

        composable("profile") { ProfileScreen(navController) }

        composable("orderHistory") {
            OrderHistoryScreen(navController)


        }
        composable("favorites") {
            FavoritesScreen(navController)
        }
    }
}