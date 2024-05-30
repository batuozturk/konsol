package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.batuhan.cloudmessaging.presentation.campaign.create.CreateNotificationScreen

internal const val CREATE_NOTIFICATION_SCREEN = "create_notification_screen"

private const val KEY_PROJECT_ID = "projectId"

fun NavGraphBuilder.createNotificationScreenGraph(navController: NavController) {
    composable(
        route = "$CREATE_NOTIFICATION_SCREEN/{$KEY_PROJECT_ID}",
        arguments = listOf(
            navArgument(KEY_PROJECT_ID) {
                type = NavType.StringType
            }
        )
    ) {
        CreateNotificationScreen {
            navController.popBackStack()
        }
    }
}
