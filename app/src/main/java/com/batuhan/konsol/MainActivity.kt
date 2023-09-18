package com.batuhan.konsol

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.batuhan.konsol.splashscreen.SplashScreenNavigationKeys.AUTH_SCREEN
import com.batuhan.navigation.*
import com.batuhan.theme.KonsolTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
        // todo other screen graphs
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KonsolTheme {
        KonsolApp(startDestination = AUTH_SCREEN, launchUrl = {})
    }
}
