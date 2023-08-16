package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.batuhan.management.data.model.FeatureItemRoute
import com.batuhan.management.presentation.project.ProjectScreen

private const val KEY_PROJECT_ID = "projectId"
private const val KEY_PROJECT_NAME = "projectName"

internal const val PROJECT_SCREEN = "project_screen"

fun NavGraphBuilder.projectScreenGraph(navController: NavController) {
    projectScreen(navController)
    /*composable()
    composable()
    composable()
    composable()*/
    // todo features route
}

fun NavGraphBuilder.projectScreen(navController: NavController) = composable(
    route = "$PROJECT_SCREEN/{$KEY_PROJECT_ID}/{$KEY_PROJECT_NAME}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) { type = NavType.StringType },
        navArgument(KEY_PROJECT_NAME) { type = NavType.StringType }
    )
) {
    ProjectScreen(
        projectName = it.arguments?.getString(KEY_PROJECT_NAME) ?: "",
        onBackPressed = { navController.popBackStack() },
        navigate = { route, projectId ->
            val decidedRoute = decideRoute(route, projectId)
            navController.navigate(decidedRoute)
        },
        navigateToProjectSettings = { projectId, projectName ->
            navController.navigate("$PROJECT_SETTINGS_SCREEN/$projectId/$projectName")
        }
    )
}

fun decideRoute(featureItemRoute: FeatureItemRoute, projectId: String): String {
    return when (featureItemRoute) {
        FeatureItemRoute.FIRESTORE -> "$DATABASE_LIST_SCREEN/$projectId"
        else -> ""
    }
}
