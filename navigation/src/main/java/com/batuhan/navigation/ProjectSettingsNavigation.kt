package com.batuhan.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.batuhan.management.data.model.SettingsItemRoute
import com.batuhan.management.presentation.project.settings.ProjectSettingsScreen
import com.batuhan.management.presentation.project.settings.actions.analytics.AnalyticsInfo
import com.batuhan.management.presentation.project.settings.actions.analytics.add.AddAnalyticsAccount
import com.batuhan.management.presentation.project.settings.actions.androidapps.AndroidApps
import com.batuhan.management.presentation.project.settings.actions.billinginfo.BillingInfo
import com.batuhan.management.presentation.project.settings.actions.billinginfo.add.AddBillingInfo
import com.batuhan.management.presentation.project.settings.actions.createapp.AppType
import com.batuhan.management.presentation.project.settings.actions.createapp.CreateApp
import com.batuhan.management.presentation.project.settings.actions.deleteproject.DeleteProject
import com.batuhan.management.presentation.project.settings.actions.iosapps.IosApps
import com.batuhan.management.presentation.project.settings.actions.projectdetail.ProjectDetail
import com.batuhan.management.presentation.project.settings.actions.projectdetail.addlocation.SelectLocation
import com.batuhan.management.presentation.project.settings.actions.webapps.WebApps
import com.batuhan.management.presentation.projectlist.ProjectsScreenNavigationKeys

private const val KEY_PROJECT_ID = "projectId"
private const val KEY_PROJECT_NAME = "projectName"
private const val KEY_APP_TYPE = "appType"

internal const val PROJECT_SETTINGS_SCREEN = "project_settings_screen"
private const val CREATE_APP_SCREEN = "create_app_screen"
private const val IOS_APPS_SCREEN = "ios_apps_screen"
private const val ANDROID_APPS_SCREEN = "android_apps_screen"
private const val WEB_APPS_SCREEN = "web_apps_screen"
private const val UPDATE_PROJECT_SCREEN = "update_project"
private const val DELETE_PROJECT_SCREEN = "delete_project"
private const val ANALYTICS_SCREEN = "analytics_info_screen"
private const val UPDATE_BILLING_INFO_SCREEN = "update_billing_info"
private const val ADD_BILLING_INFO_SCREEN = "add_billing_info"
private const val ADD_ANALYTICS_ACCOUNT_SCREEN = "add_analytics_account"
private const val SELECT_LOCATION_SCREEN = "select_location"

fun NavGraphBuilder.projectSettingsGraph(navController: NavController) {
    projectSettingsScreen(navController)
    createAppScreen(navController)
    iosAppsScreen(navController)
    webAppsScreen(navController)
    androidAppsScreen(navController)
    updateBillingInfoScreen(navController)
    updateProjectScreen(navController)
    analyticsScreen(navController)
    deleteProjectScreen(navController)
    addBillingInfoScreen(navController)
    addAnalyticsAccountScreen(navController)
    selectLocationScreen(navController)
}

fun NavGraphBuilder.projectSettingsScreen(navController: NavController) = composable(
    route = "$PROJECT_SETTINGS_SCREEN/{$KEY_PROJECT_ID}/{$KEY_PROJECT_NAME}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) { type = NavType.StringType },
        navArgument(KEY_PROJECT_NAME) { type = NavType.StringType }
    )
) {
    val projectId = it.arguments?.getString(KEY_PROJECT_ID) ?: ""
    ProjectSettingsScreen(
        projectId = it.arguments?.getString(KEY_PROJECT_ID) ?: "",
        projectName = it.arguments?.getString(KEY_PROJECT_NAME) ?: "",
        navigate = { route ->
            val screenRoute = decideRoute(route, projectId)
            navController.navigate(screenRoute)
        },
        onBackPressed = { navController.popBackStack() }
    )
}

fun NavGraphBuilder.createAppScreen(navController: NavController) = composable(
    route = "$CREATE_APP_SCREEN/{$KEY_PROJECT_ID}/{$KEY_APP_TYPE}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) { type = NavType.StringType },
        navArgument(KEY_APP_TYPE) { type = NavType.StringType }
    )
) {
    CreateApp(
        appType = it.arguments?.getString(KEY_APP_TYPE) ?: "",
        onBackPressed = { navController.popBackStack() }
    )
}

fun NavGraphBuilder.iosAppsScreen(navController: NavController) = composable(
    route = "$IOS_APPS_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    IosApps(
        onBackPressed = { navController.popBackStack() },
        createIosApp = { projectId ->
            navController.navigate("$CREATE_APP_SCREEN/$projectId/${AppType.IOS.name}")
        }
    )
}

fun NavGraphBuilder.webAppsScreen(navController: NavController) = composable(
    route = "$WEB_APPS_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    WebApps(
        onBackPressed = { navController.popBackStack() },
        createWebApp = { projectId ->
            navController.navigate("$CREATE_APP_SCREEN/$projectId/${AppType.WEB.name}")
        }
    )
}

fun NavGraphBuilder.androidAppsScreen(navController: NavController) = composable(
    route = "$ANDROID_APPS_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    AndroidApps(
        onBackPressed = { navController.popBackStack() },
        createAndroidApp = { projectId ->
            navController.navigate("$CREATE_APP_SCREEN/$projectId/${AppType.ANDROID.name}")
        }
    )
}

fun NavGraphBuilder.updateBillingInfoScreen(navController: NavController) = composable(
    route = "$UPDATE_BILLING_INFO_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    BillingInfo(
        onBackPressed = {
            navController.popBackStack()
        },
        navigateToAddBillingInfo = { projectId ->
            navController.navigate("$ADD_BILLING_INFO_SCREEN/$projectId")
        }
    )
}

fun NavGraphBuilder.updateProjectScreen(navController: NavController) = composable(
    route = "$UPDATE_PROJECT_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    ProjectDetail(
        onBackPressed = {
            navController.popBackStack()
        },
        navigateToSelectLocation = { projectId ->
            navController.navigate("$SELECT_LOCATION_SCREEN/$projectId")
        }
    )
}

fun NavGraphBuilder.analyticsScreen(navController: NavController) = composable(
    route = "$ANALYTICS_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    AnalyticsInfo(
        onBackPressed = {
            navController.popBackStack()
        },
        navigateToAddAnalyticsAccount = { projectId ->
            navController.navigate("$ADD_ANALYTICS_ACCOUNT_SCREEN/$projectId")
        }
    )
}

fun NavGraphBuilder.deleteProjectScreen(navController: NavController) = composable(
    route = "$DELETE_PROJECT_SCREEN/{$KEY_PROJECT_ID}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    DeleteProject(
        onBackPressed = {
            navController.popBackStack()
        },
        onStartDestination = {
            navController.navigate(
                ProjectsScreenNavigationKeys.START_DESTINATION,
                navOptions = navOptions {
                    popUpTo(ProjectsScreenNavigationKeys.START_DESTINATION) {
                        inclusive = true
                    }
                }
            )
        }
    )
}

fun NavGraphBuilder.addBillingInfoScreen(navController: NavController) = composable(
    route = "$ADD_BILLING_INFO_SCREEN/{projectId}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    AddBillingInfo {
        navController.popBackStack()
    }
}

fun NavGraphBuilder.addAnalyticsAccountScreen(navController: NavController) = composable(
    route = "$ADD_ANALYTICS_ACCOUNT_SCREEN/{projectId}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    AddAnalyticsAccount {
        navController.popBackStack()
    }
}

fun NavGraphBuilder.selectLocationScreen(navController: NavController) = composable(
    route = "$SELECT_LOCATION_SCREEN/{projectId}",
    arguments = listOf(
        navArgument(KEY_PROJECT_ID) {
            type = NavType.StringType
        }
    )
) {
    SelectLocation {
        navController.popBackStack()
    }
}

fun decideRoute(route: SettingsItemRoute, projectId: String) = when (route) {
    SettingsItemRoute.ANALYTICS -> "$ANALYTICS_SCREEN/$projectId"
    SettingsItemRoute.UPDATE_PROJECT -> "$UPDATE_PROJECT_SCREEN/$projectId"
    SettingsItemRoute.UPDATE_BILLING_INFO -> "$UPDATE_BILLING_INFO_SCREEN/$projectId"
    SettingsItemRoute.DELETE_PROJECT -> "$DELETE_PROJECT_SCREEN/$projectId"
    SettingsItemRoute.ANDROID_APPS -> "$ANDROID_APPS_SCREEN/$projectId"
    SettingsItemRoute.IOS_APPS -> "$IOS_APPS_SCREEN/$projectId"
    SettingsItemRoute.WEB_APPS -> "$WEB_APPS_SCREEN/$projectId"
}
