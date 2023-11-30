package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.batuhan.realtimedatabase.presentation.RealtimeDatabaseScreen
import com.batuhan.realtimedatabase.presentation.create.CreateDatabaseScreen
import com.batuhan.realtimedatabase.presentation.databaseitem.DatabaseItemScreen

internal const val REALTIME_DATABASE_SCREEN = "realtime_database_screen"
internal const val CREATE_REALTIME_DATABASE_SCREEN = "create_realtime_database_screen"
internal const val DATABASE_ITEM_SCREEN = "database_item_screen"

fun NavGraphBuilder.realtimeDatabaseScreenGraph(navController: NavController) {
    realtimeDatabaseScreen(navController)
    createRealtimeDatabaseScreen(navController)
    databaseItemScreen(navController)
}

fun NavGraphBuilder.realtimeDatabaseScreen(navController: NavController) =
    composable(
        "$REALTIME_DATABASE_SCREEN/{projectId}",
        arguments = listOf(
            navArgument("projectId") {
                type = NavType.StringType
            }
        )
    ) {
        RealtimeDatabaseScreen(
            onBackPressed = {
                navController.popBackStack()
            },
            navigateToCreateDatabaseScreen = { isFirst, projectId ->
                navController.navigate("$CREATE_REALTIME_DATABASE_SCREEN/$projectId/$isFirst")
            },
            navigateToDatabaseScreen = {
                navController.navigate("$DATABASE_ITEM_SCREEN/$it")
            }
        )
    }

fun NavGraphBuilder.createRealtimeDatabaseScreen(navController: NavController) =
    composable(
        "$CREATE_REALTIME_DATABASE_SCREEN/{projectId}/{isFirst}",
        arguments = listOf(
            navArgument("projectId") {
                type = NavType.StringType
            },
            navArgument("isFirst") {
                type = NavType.BoolType
            }
        )
    ) {
        CreateDatabaseScreen(
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }

fun NavGraphBuilder.databaseItemScreen(navController: NavController) =
    composable(
        "$DATABASE_ITEM_SCREEN/{databaseUrl}",
        arguments = listOf(
            navArgument("databaseUrl") {
                type = NavType.StringType
            }
        )
    ) {
        DatabaseItemScreen {
            navController.popBackStack()
        }
    }
