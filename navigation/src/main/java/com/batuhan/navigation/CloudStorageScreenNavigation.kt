package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.batuhan.cloudstorage.presentation.bucket.BucketScreen
import com.batuhan.cloudstorage.presentation.bucket.create.CreateBucketScreen
import com.batuhan.cloudstorage.presentation.objectlist.ObjectListScreen
import com.batuhan.cloudstorage.presentation.objectlist.createfolder.CreateFolderScreen

internal const val BUCKET_SCREEN = "bucket_screen"
private const val CREATE_BUCKET_SCREEN = "create_bucket_screen"
private const val OBJECT_LIST_SCREEN = "object_list_screen"
private const val CREATE_FOLDER_SCREEN = "create_folder_screen"

private const val KEY_PROJECT_ID = "projectId"
private const val KEY_BUCKET_NAME = "bucketName"
private const val KEY_PREFIX = "prefix"

fun NavGraphBuilder.cloudStorageScreenGraph(navController: NavController) {
    bucketScreen(navController)
    createBucketScreen(navController)
    objectListScreen(navController)
    createFolderScreen(navController)
}

fun NavGraphBuilder.bucketScreen(navController: NavController) = composable(
    route = "$BUCKET_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    BucketScreen(
        onBackPressed = { navController.popBackStack() },
        navigateToCreateBucketScreen = { projectId ->
            navController.navigate("$CREATE_BUCKET_SCREEN/$projectId")
        },
        navigateToObjectListScreen = { bucketName ->
            navController.navigate("$OBJECT_LIST_SCREEN/$bucketName")
        }
    )
}

fun NavGraphBuilder.createBucketScreen(navController: NavController) = composable(
    route = "$CREATE_BUCKET_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    CreateBucketScreen(
        onBackPressed = { navController.popBackStack() }
    )
}

fun NavGraphBuilder.objectListScreen(navController: NavController) = composable(
    route = "$OBJECT_LIST_SCREEN/{$KEY_BUCKET_NAME}",
    arguments = listOf(
        navArgument(KEY_BUCKET_NAME) {
            type = NavType.StringType
        }
    )
) {
    ObjectListScreen(
        onBackPressed = { navController.popBackStack() },
        navigateToCreateFolderScreen = { bucketName,prefix ->
            navController.navigate("$CREATE_FOLDER_SCREEN/$bucketName/$prefix")
        }
    )
}

fun NavGraphBuilder.createFolderScreen(navController: NavController) = composable(
    route = "$CREATE_FOLDER_SCREEN/{$KEY_BUCKET_NAME}/{$KEY_PREFIX}",
    arguments = listOf(
        navArgument(KEY_BUCKET_NAME) {
            type = NavType.StringType
        },
        navArgument(KEY_PREFIX) {
            type = NavType.StringType
        }
    )
) {
    CreateFolderScreen {
        navController.popBackStack()
    }
}
