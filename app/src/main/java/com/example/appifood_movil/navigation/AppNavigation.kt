package com.example.appifood_movil.navigation

import com.example.appifood_movil.ui.screens.HomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.appifood_movil.ui.screens.SplashLoginScreen
import com.example.appifood_movil.ui.screens.AuthScreen
import com.example.appifood_movil.ui.screens.ProductDetailScreen

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
            // Pasamos la función que ejecuta el cambio de pantalla
            AuthScreen(onLoginNavigation = {
                navController.navigate("home") {
                    // Esto evita que el usuario regrese al login al presionar 'atrás'
                    popUpTo("auth") { inclusive = true }
                }
            })
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("productDetail") {
            ProductDetailScreen(navController)
        }
    }
}