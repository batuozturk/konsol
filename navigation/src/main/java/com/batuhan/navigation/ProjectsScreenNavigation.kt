package com.batuhan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.batuhan.management.presentation.createproject.CreateProjectScreen
import com.batuhan.management.presentation.projectlist.ProjectsScreen
import com.batuhan.management.presentation.projectlist.ProjectsScreenNavigationKeys.CREATE_PROJECT_SCREEN
import com.batuhan.management.presentation.projectlist.ProjectsScreenNavigationKeys.START_DESTINATION

fun NavGraphBuilder.ProjectsScreenGraph(navController: NavController) {
    composable(START_DESTINATION) {
        ProjectsScreen(
            navigate = { screen, popUpScreen, popUpInclusive ->
                navController.navigate(screen) {
                    popUpScreen?.let {
                        popUpTo(it) {
                            inclusive = popUpInclusive
                        }
                    }
                }
            }
        )
    }
    composable(CREATE_PROJECT_SCREEN) {
        CreateProjectScreen(
            onDismiss = { navController.popBackStack() }
        )
    }
}
