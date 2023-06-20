package com.batuhan.fconsole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.batuhan.fconsole.splashscreen.SplashScreenNavigationKeys.AUTH_SCREEN
import com.batuhan.navigation.ProjectsScreenGraph
import com.batuhan.navigation.authScreenGraph
import com.batuhan.theme.FConsoleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val screen = intent.extras?.getString("screen_name") ?: AUTH_SCREEN
            FConsoleTheme {
                // A surface container using the 'background' color from the theme
                FConsoleApp(startDestination = screen)
            }
        }
    }
}

@Composable
fun FConsoleApp(viewModel: MainViewModel = hiltViewModel(), startDestination: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authScreenGraph(navController)
        ProjectsScreenGraph(navController)
        // todo other screen graphs
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FConsoleTheme {
        FConsoleApp(startDestination = AUTH_SCREEN)
    }
}
