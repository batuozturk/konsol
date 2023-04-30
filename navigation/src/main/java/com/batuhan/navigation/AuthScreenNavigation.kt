package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.batuhan.oauth2.presentation.AuthScreen
import com.batuhan.oauth2.presentation.AuthScreenNavigationKeys.START_DESTINATION

fun NavGraphBuilder.authScreenGraph(navController: NavController) {
    composable(START_DESTINATION) {
        AuthScreen(navigate = { screen, popUpScreen, popUpInclusive ->
            navController.navigate(screen) {
                popUpScreen?.let {
                    popUpTo(it) {
                        inclusive = popUpInclusive
                    }
                }
            }
        })
    }
}
