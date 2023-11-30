package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.batuhan.oauth2.presentation.AuthScreen

internal const val AUTH_SCREEN = "auth_screen"
private const val BILLING_SCREEN = "billing_screen"

fun NavGraphBuilder.authScreenGraph(navController: NavController, launchUrl: (String) -> Unit, langCode: String, selectLang: (String) -> Unit) {
    composable(AUTH_SCREEN) {
        AuthScreen(
            navigateToProjectListScreen = {
                navController.navigate(PROJECT_LIST_SCREEN) {
                    popUpTo(AUTH_SCREEN) {
                        inclusive = true
                    }
                }
            },
            navigateToBillingScreen = {
                navController.navigate(BILLING_SCREEN) {
                    popUpTo(AUTH_SCREEN) {
                        inclusive = true
                    }
                }
            },
            launchUrl = launchUrl,
            langCode = langCode,
            selectLang = selectLang
        )
    }
}
