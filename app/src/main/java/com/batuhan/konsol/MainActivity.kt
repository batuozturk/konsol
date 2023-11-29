package com.batuhan.konsol

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.batuhan.konsol.billing.BillingScreen
import com.batuhan.konsol.splashscreen.SplashScreenNavigationKeys.AUTH_SCREEN
import com.batuhan.konsol.splashscreen.SplashScreenNavigationKeys.BILLING_SCREEN
import com.batuhan.konsol.splashscreen.SplashScreenNavigationKeys.PROJECT_LIST_SCREEN
import com.batuhan.navigation.*
import com.batuhan.theme.KonsolTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tabIntent = CustomTabsIntent.Builder()
            .build()
        setContent {
            val screen = intent.extras?.getString("screen_name") ?: AUTH_SCREEN
            KonsolTheme {
                // A surface container using the 'background' color from the theme
                KonsolApp(
                    startDestination = screen,
                    launchUrl = { url ->
                        tabIntent.launchUrl(this@MainActivity, Uri.parse(url))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }

                        Firebase.messaging.subscribeToTopic(getString(R.string.topic_subscribe))
                    }
                )
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    Firebase.messaging.subscribeToTopic(getString(R.string.topic_subscribe))
                }
            )
        }
    }
}

@Composable
fun KonsolApp(
    viewModel: MainViewModel = hiltViewModel(),
    startDestination: String,
    launchUrl: (String) -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authScreenGraph(navController, launchUrl)
        projectListScreenGraph(navController)
        projectScreenGraph(navController)
        projectSettingsGraph(navController)
        firestoreScreenGraph(navController)
        createNotificationScreenGraph(navController)
        testLabScreenGraph(navController)
        cloudStorageScreenGraph(navController)
        billingScreenGraph(navController)
        realtimeDatabaseScreenGraph(navController)
        // todo other screen graphs
    }
}

fun NavGraphBuilder.billingScreenGraph(
    navController: NavController
) {
    composable(BILLING_SCREEN) {
        BillingScreen(
            onNavigateToProjectListScreen = {
                navController.navigate(PROJECT_LIST_SCREEN) {
                    popUpTo(BILLING_SCREEN) {
                        inclusive = true
                    }
                }
            },
            logout = {
                navController.navigate(AUTH_SCREEN) {
                    popUpTo(BILLING_SCREEN) {
                        inclusive = true
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KonsolTheme {
        KonsolApp(startDestination = AUTH_SCREEN, launchUrl = {})
    }
}
