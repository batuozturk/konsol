package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.batuhan.oauth2.presentation.AuthScreen

internal const val AUTH_SCREEN = "auth_screen"

fun NavGraphBuilder.authScreenGraph(navController: NavController, launchUrl: (String) -> Unit) {
    composable(AUTH_SCREEN) {
        AuthScreen(
            navigateToProjectListScreen = {
                navController.navigate(PROJECT_LIST_SCREEN) {
                    popUpTo(AUTH_SCREEN) {
                        inclusive = true
                    }
                }
            },
            launchUrl = launchUrl
        )
    }
}
