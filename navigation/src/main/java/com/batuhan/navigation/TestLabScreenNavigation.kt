package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.batuhan.testlab.presentation.creatematrix.CreateMatrixScreen
import com.batuhan.testlab.presentation.execution.ExecutionScreen
import com.batuhan.testlab.presentation.execution.environment.EnvironmentScreen
import com.batuhan.testlab.presentation.resultlist.ResultListScreen
import com.batuhan.testlab.presentation.selectfile.SelectFileScreen

internal const val RESULT_LIST_SCREEN = "result_list_screen"
private const val CREATE_MATRIX_SCREEN = "create_matrix_screen"
private const val EXECUTION_DETAIL_SCREEN = "execution_detail_screen"
private const val ENVIRONMENT_SCREEN = "execution_screen"
private const val SELECT_FILE_SCREEN = "select_file_screen"

private const val KEY_PROJECT_ID = "projectId"
private const val KEY_EXECUTION_ID = "executionId"
private const val KEY_HISTORY_ID = "historyId"
private const val KEY_ENVIRONMENT_ID = "environmentId"
private const val KEY_BUCKET_NAME = "bucketName"

fun NavGraphBuilder.testLabScreenGraph(navController: NavController) {
    resultListScreen(navController)
    createMatrixScreen(navController)
    executionScreen(navController)
    environmentScreen(navController)
    selectFileScreen(navController)
}

fun NavGraphBuilder.resultListScreen(navController: NavController) = composable(
    route = "$RESULT_LIST_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    ResultListScreen(
        onBackPressed = {
            navController.popBackStack()
        },
        navigateToCreateMatrixScreen = { projectId ->
            navController.navigate("$CREATE_MATRIX_SCREEN/$projectId")
        },
        navigateToExecutionDetailScreen = { projectId, historyId, executionId ->
            navController.navigate("$EXECUTION_DETAIL_SCREEN/$projectId/$historyId/$executionId")
        }
    )
}

fun NavGraphBuilder.createMatrixScreen(navController: NavController) = composable(
    route = "$CREATE_MATRIX_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    val gcsPath = it.savedStateHandle.get<String>("gcsPath")
    CreateMatrixScreen(
        gcsPath,
        onBackPressed = {
            navController.popBackStack()
        },
        navigateToSelectFileScreen = { bucketName ->
            navController.navigate("$SELECT_FILE_SCREEN/$bucketName")
        },
        clearGcsPath = {
            it.savedStateHandle.remove<String>("gcsPath")
        }
    )
}

fun NavGraphBuilder.executionScreen(navController: NavController) = composable(
    route = "$EXECUTION_DETAIL_SCREEN/{$KEY_PROJECT_ID}/{$KEY_HISTORY_ID}/{$KEY_EXECUTION_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        },
        navArgument(KEY_EXECUTION_ID) {
            type = NavType.StringType
        },
        navArgument(KEY_HISTORY_ID) {
            type = NavType.StringType
        }
    )
) {
    ExecutionScreen(
        onBackPressed = {
            navController.popBackStack()
        },
        navigateToEnvironmentScreen = { projectId, historyId, executionId, environmentId ->
            navController.navigate("$ENVIRONMENT_SCREEN/$projectId/$historyId/$executionId/$environmentId")
        }
    )
}

fun NavGraphBuilder.environmentScreen(navController: NavController) = composable(
    route = "$ENVIRONMENT_SCREEN/{$KEY_PROJECT_ID}/{$KEY_HISTORY_ID}/{$KEY_EXECUTION_ID}/{$KEY_ENVIRONMENT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        },
        navArgument(KEY_EXECUTION_ID) {
            type = NavType.StringType
        },
        navArgument(KEY_HISTORY_ID) {
            type = NavType.StringType
        },
        navArgument(KEY_ENVIRONMENT_ID) {
            type = NavType.StringType
        }
    )
) {
    EnvironmentScreen(
        onBackPressed = {
            navController.popBackStack()
        }
    )
}

fun NavGraphBuilder.selectFileScreen(navController: NavController) = composable(
    route = "$SELECT_FILE_SCREEN/{$KEY_BUCKET_NAME}",
    arguments = listOf(
        navArgument(KEY_BUCKET_NAME) {
            type = NavType.StringType
        }
    )
) {
    SelectFileScreen(
        onBackPressed = { navController.popBackStack() },
        onSuccess = { gcsPath ->
            navController.previousBackStackEntry?.savedStateHandle?.set("gcsPath", gcsPath)
            navController.popBackStack()
        }
    )
}
