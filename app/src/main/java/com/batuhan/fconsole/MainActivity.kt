package com.batuhan.fconsole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.batuhan.fconsole.splashscreen.SplashScreen
import com.batuhan.fconsole.splashscreen.SplashScreenNavigationKeys
import com.batuhan.oauth2.presentation.AuthScreen
import com.batuhan.theme.FConsoleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FConsoleTheme {
                // A surface container using the 'background' color from the theme
                FConsoleApp()
            }
        }
    }
}

@Composable
fun FConsoleApp(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SplashScreenNavigationKeys.START_DESTINATION
    ) {
        splashScreenGraph(navController)
        // todo other screen graphs
        composable("auth_screen") { _ ->
            AuthScreen(onAuthCompleted = {
                navController.navigate("Home") {
                    popUpTo("Auth") {
                        inclusive = true
                    }
                }
            })
        }
        composable("Home") {
        }
    }
}

fun NavGraphBuilder.splashScreenGraph(navController: NavController){
    composable(SplashScreenNavigationKeys.START_DESTINATION) {
        SplashScreen(navigate = { screen, popUpScreen, popUpInclusive ->
            navController.navigate(screen) {
                popUpScreen?.let {
                    popUpTo(it) {
                        inclusive = popUpInclusive
                    }
                }
            }
        })
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FConsoleTheme {
        FConsoleApp()
    }
}
