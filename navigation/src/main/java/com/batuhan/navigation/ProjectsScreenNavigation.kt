package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.batuhan.management.presentation.createproject.CreateProjectScreen
import com.batuhan.management.presentation.projectlist.ProjectsScreen
import com.batuhan.management.presentation.projectlist.ProjectsScreenEvent

internal const val PROJECT_LIST_SCREEN = "projects_screen"
private const val CREATE_PROJECT_SCREEN = "create_project_screen"

fun NavGraphBuilder.projectListScreenGraph(navController: NavController) {
    composable(PROJECT_LIST_SCREEN) {
        ProjectsScreen(
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

fun NavController.navigate(event: ProjectsScreenEvent) {
    when (event) {
        ProjectsScreenEvent.Logout -> {
            navigate(AUTH_SCREEN) {
                popUpTo(PROJECT_LIST_SCREEN) {
                    inclusive = true
                }
            }
        }
        ProjectsScreenEvent.CreateProject -> {
            navigate(CREATE_PROJECT_SCREEN)
        }
        is ProjectsScreenEvent.Project -> {
            navigate("$PROJECT_SCREEN/${event.project.projectId ?: ""}/${event.project.displayName ?: ""}")
        }
    }
}
