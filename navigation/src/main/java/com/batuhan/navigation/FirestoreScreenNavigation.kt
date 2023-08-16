package com.batuhan.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.batuhan.firestore.presentation.createdatabase.CreateDatabaseScreen
import com.batuhan.firestore.presentation.database.DatabaseScreen
import com.batuhan.firestore.presentation.database.createitem.CreateItemScreen
import com.batuhan.firestore.presentation.database.delete.DeleteDocumentScreen
import com.batuhan.firestore.presentation.databaselist.DatabaseListScreen

internal const val DATABASE_LIST_SCREEN = "database_list_screen"
private const val DATABASE_SCREEN = "database_screen"
private const val CREATE_DATABASE_SCREEN = "create_database_screen"
private const val SELECT_LOCATION_SCREEN = "select_location_screen"
private const val CREATE_ITEM_SCREEN = "create_item_screen"
private const val DELETE_DOCUMENT_SCREEN = "delete_document_screen"

private const val KEY_PROJECT_ID = "projectId"
private const val KEY_NAME = "name"
private const val KEY_LOCATION_ID = "locationId"
private const val KEY_CREATE_COLLECTION = "createCollection"
private const val KEY_FIRESTORE_PATH = "firestorePath"

fun NavGraphBuilder.firestoreScreenGraph(navController: NavController) {
    databaseListScreen(navController)
    createDatabaseScreen(navController)
    databaseScreen(navController)
    createItemScreen(navController)
    deleteDocumentScreen(navController)
}

fun NavGraphBuilder.databaseListScreen(navController: NavController) = composable(
    route = "$DATABASE_LIST_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    DatabaseListScreen(
        onBackPressed = {
            navController.popBackStack()
        },
        navigateToDatabaseScreen = { name ->
            val encodedName = Uri.encode(name)
            navController.navigate("$DATABASE_SCREEN/$encodedName")
        },
        createDatabase = { projectId ->
            navController.navigate("$CREATE_DATABASE_SCREEN/$projectId")
        },
    )
}

fun NavGraphBuilder.createDatabaseScreen(navController: NavController) = composable(
    route = "$CREATE_DATABASE_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    CreateDatabaseScreen(
        {
            navController.popBackStack()
        },
        { projectId, locationId ->
            navController.navigate("$SELECT_LOCATION_SCREEN/$projectId/$locationId")
        }
    )
}

fun NavGraphBuilder.databaseScreen(navController: NavController) = composable(
    route = "$DATABASE_SCREEN/{$KEY_NAME}",
    arguments = listOf(
        navArgument(KEY_NAME) {
            type = NavType.StringType
        }
    )
) {
    val isSuccess =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("isSuccess") ?: false
    val needsRefresh =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("needsRefresh") ?: false
    DatabaseScreen(
        isDeleted = isSuccess,
        needsRefresh = needsRefresh,
        onBackPressed = {
            navController.popBackStack()
        },
        navigateToCreateDocumentScreen = { path, createCollection ->
            navController.navigate("$CREATE_ITEM_SCREEN/$path/$createCollection")
        },
        navigateToDeleteDocumentScreen = { path, _ ->
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("isSuccess", false)
            navController.navigate("$DELETE_DOCUMENT_SCREEN/$path")
        },
        clearSuccessState = {
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Boolean>("isSuccess")
        },
        clearRefreshState = {
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Boolean>("needsRefresh")
        }
    )
}

fun NavGraphBuilder.createItemScreen(navController: NavController) = composable(
    route = "$CREATE_ITEM_SCREEN/{$KEY_NAME}/{$KEY_CREATE_COLLECTION}",
    arguments = listOf(
        navArgument(KEY_NAME) {
            type = NavType.StringType
        },
        navArgument(KEY_CREATE_COLLECTION) {
            type = NavType.BoolType
        }
    )
) {
    CreateItemScreen { needsRefresh ->
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("needsRefresh", needsRefresh)
        navController.popBackStack()
    }
}

fun NavGraphBuilder.deleteDocumentScreen(navController: NavController) = composable(
    route = "$DELETE_DOCUMENT_SCREEN/{$KEY_FIRESTORE_PATH}",
    arguments = listOf(
        navArgument(KEY_FIRESTORE_PATH) {
            type = NavType.StringType
        }
    )
) {
    DeleteDocumentScreen { isSuccess ->
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("isSuccess", isSuccess)
        navController.popBackStack()
    }
}
