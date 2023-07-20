package com.batuhan.management.data.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.batuhan.management.R
import com.batuhan.theme.*

enum class FeatureItem(
    @StringRes val titleResId: Int,
    val icon: ImageVector,
    val route: FeatureItemRoute,
    val tintColor: Color
) {
    FCM(
        R.string.feature_fcm,
        Icons.Default.Send,
        FeatureItemRoute.FCM,
        Orange
    ),
    FIRESTORE(
        R.string.feature_firestore,
        Icons.Default.Description,
        FeatureItemRoute.FIRESTORE,
        Orange
    ),
    AUTH(
        R.string.feature_auth,
        Icons.Default.ManageAccounts,
        FeatureItemRoute.AUTH,
        Orange
    ),
    REALTIME_DATABASE(
        R.string.feature_realtime_database,
        Icons.Outlined.Storage,
        FeatureItemRoute.REALTIME_DATABASE,
        Orange
    ),
    APP_CHECK(
        R.string.feature_app_check,
        Icons.Default.VerifiedUser,
        FeatureItemRoute.APP_CHECK,
        Orange
    ),
    CLOUD_STORAGE(
        R.string.feature_cloud_storage,
        Icons.Default.Folder,
        FeatureItemRoute.CLOUD_STORAGE,
        Orange
    ),
    REMOTE_CONFIG(
        R.string.feature_remote_config,
        Icons.Default.CloudSync,
        FeatureItemRoute.REMOTE_CONFIG,
        Orange
    ),
    HOSTING(
        R.string.feature_hosting,
        Icons.Default.Web,
        FeatureItemRoute.HOSTING,
        Orange
    ),
    DYNAMIC_LINKS(
        R.string.feature_dynamic_links,
        Icons.Default.Link,
        FeatureItemRoute.DYNAMIC_LINKS,
        Orange
    ),
    TEST_LAB(
        R.string.feature_test_lab,
        Icons.Default.DevicesOther,
        FeatureItemRoute.TEST_LAB,
        Orange
    ),
    APP_DISTRIBUTION(
        R.string.feature_app_distribution,
        Icons.Default.InstallMobile,
        FeatureItemRoute.APP_DISTRIBUTION,
        Orange
    )
}

fun generateFeatureList(): List<FeatureItem> {
    val featureList = mutableListOf<FeatureItem>()
    featureList.add(FeatureItem.FCM)
    featureList.add(FeatureItem.FIRESTORE)
    featureList.add(FeatureItem.AUTH)
    featureList.add(FeatureItem.REALTIME_DATABASE)
    featureList.add(FeatureItem.APP_CHECK)
    featureList.add(FeatureItem.CLOUD_STORAGE)
    featureList.add(FeatureItem.REMOTE_CONFIG)
    featureList.add(FeatureItem.HOSTING)
    featureList.add(FeatureItem.DYNAMIC_LINKS)
    featureList.add(FeatureItem.TEST_LAB)
    featureList.add(FeatureItem.APP_DISTRIBUTION)
    return featureList
}

enum class FeatureItemRoute {
    FCM,
    FIRESTORE,
    AUTH,
    REALTIME_DATABASE,
    APP_CHECK,
    CLOUD_STORAGE,
    REMOTE_CONFIG,
    HOSTING,
    DYNAMIC_LINKS,
    TEST_LAB,
    APP_DISTRIBUTION
}
