package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.batuhan.management.presentation.createproject.CreateProjectScreen
import com.batuhan.management.presentation.projectlist.ProjectListEvent
import com.batuhan.management.presentation.projectlist.ProjectListScreen

internal const val PROJECT_LIST_SCREEN = "project_list_screen"
private const val CREATE_PROJECT_SCREEN = "create_project_screen"

fun NavGraphBuilder.projectListScreenGraph(navController: NavController) {
    composable(PROJECT_LIST_SCREEN) {
        ProjectListScreen(
            navigate = { event ->
                navController.navigate(event)
            }
        )
    }
    composable(CREATE_PROJECT_SCREEN) {
        CreateProjectScreen(
            onBackPressed = { navController.popBackStack() }
        )
    }
}

fun NavController.navigate(event: ProjectListEvent) {
    when (event) {
        ProjectListEvent.Logout -> {
            navigate(AUTH_SCREEN) {
                popUpTo(PROJECT_LIST_SCREEN) {
                    inclusive = true
                }
            }
        }
        ProjectListEvent.CreateProject -> {
            navigate(CREATE_PROJECT_SCREEN)
        }
        is ProjectListEvent.Project -> {
            navigate("$PROJECT_SCREEN/${event.projectId ?: ""}/${event.displayName ?: ""}")
        }
    }
}
