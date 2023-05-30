package com.batuhan.fconsole.splashscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.batuhan.theme.FConsoleTheme
import com.batuhan.theme.Orange
import com.batuhan.theme.R

internal object SplashScreenNavigationKeys {
    const val START_DESTINATION = "splash_screen"
    const val PROJECTS_SCREEN = "projects_screen"
    const val AUTH_SCREEN = "auth_screen"
}

@Composable
fun SplashScreen(
    navigate: (key: String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.routeFlow.collect {
            when (it) {
                is SplashRouting.AuthScreen -> navigate(
                    SplashScreenNavigationKeys.AUTH_SCREEN
                )
                is SplashRouting.ProjectsScreen -> navigate(
                    SplashScreenNavigationKeys.PROJECTS_SCREEN
                )
            }
        }
    }
    SplashScreenContent()
}

@Composable
fun SplashScreenContent() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo_24),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).aspectRatio(1f),
                    contentScale = ContentScale.FillBounds
                )
                Spacer(modifier = Modifier.height(20.dp))
                LinearProgressIndicator(color = Orange)
            }
        }
    }
}

@Preview(showSystemUi = false)
@Composable
fun SplashScreenPreview() {
    FConsoleTheme {
        SplashScreenContent()
    }
}
