package com.batuhan.management.data.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.batuhan.management.R

enum class SettingsItem(
    @StringRes val titleResId: Int,
    val icon: ImageVector,
    val route: SettingsItemRoute
) {
    ADD_OR_REMOVE_GOOGLE_ANALYTICS(
        R.string.add_remove_google_analytics,
        Icons.Default.Analytics,
        SettingsItemRoute.ANALYTICS
    ),
    ANDROID_APPS(
        R.string.android_apps,
        Icons.Default.List,
        SettingsItemRoute.ANDROID_APPS
    ),
    IOS_APPS(
        R.string.ios_apps,
        Icons.Default.List,
        SettingsItemRoute.IOS_APPS
    ),
    WEB_APPS(
        R.string.web_apps,
        Icons.Default.List,
        SettingsItemRoute.WEB_APPS
    ),
    UPDATE_PROJECT(
        R.string.update_project,
        Icons.Default.Edit,
        SettingsItemRoute.UPDATE_PROJECT
    ),
    UPDATE_BILLING_INFO(
        R.string.update_billing_info,
        Icons.Default.Payment,
        SettingsItemRoute.UPDATE_BILLING_INFO
    ),
    DELETE_PROJECT(
        R.string.delete_project,
        Icons.Default.Delete,
        SettingsItemRoute.DELETE_PROJECT
    )
}

enum class SettingsItemRoute {
    ANALYTICS,
    UPDATE_PROJECT,
    UPDATE_BILLING_INFO,
    DELETE_PROJECT,
    ANDROID_APPS,
    IOS_APPS,
    WEB_APPS,
}

fun generateSettingsList(): List<SettingsItem> {
    val settingsList = mutableListOf<SettingsItem>()
    settingsList.add(SettingsItem.ADD_OR_REMOVE_GOOGLE_ANALYTICS)
    settingsList.add(SettingsItem.ANDROID_APPS)
    settingsList.add(SettingsItem.IOS_APPS)
    settingsList.add(SettingsItem.WEB_APPS)
    settingsList.add(SettingsItem.UPDATE_PROJECT)
    settingsList.add(SettingsItem.UPDATE_BILLING_INFO)
    settingsList.add(SettingsItem.DELETE_PROJECT)
    return settingsList
}
