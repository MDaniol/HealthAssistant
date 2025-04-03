package com.example.healthassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.healthassistant.consent.ui.ConsentScreen
import com.example.healthassistant.login.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Consent : Screen("consent/{consentId}") {
        fun createRoute(consentId: String): String {
            return "consent/$consentId"
        }
    }
    object Upload : Screen("upload")
}
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onConsentRequired = { consentId ->
                    navController.navigate(Screen.Consent.createRoute(consentId))
                },
                onReadyForUpload = {
                    navController.navigate(Screen.Upload.route)
                }
            )
        }
        composable(
            route = Screen.Consent.route,
            arguments = listOf(navArgument("consentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val consentId = backStackEntry.arguments?.getString("consentId") ?: ""
            ConsentScreen(consentId, {
            //TODO()
            })
        }

        composable("upload") {
            //TODO: FileUploadScreen()
        }
    }
}