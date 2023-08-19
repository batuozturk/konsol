package com.batuhan.konsol.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.AuthStateManager
import com.batuhan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authStateManager: AuthStateManager) :
    ViewModel() {

    private val route = Channel<SplashRouting> { Channel.BUFFERED }
    val routeFlow = route.receiveAsFlow()

    init {
        getAuthenticatedUser()
    }

    private fun getAuthenticatedUser() {
        viewModelScope.launch {
            delay(3000L)
            when (val result = authStateManager.getAuthState()) {
                is Result.Success -> {
                    result.data?.let {
                        authStateManager.setAuthState(it)
                        route.send(SplashRouting.ProjectsScreen)
                    } ?: run {
                        authStateManager.clearAuthState()
                        route.send(SplashRouting.AuthScreen)
                    }
                }
                is Result.Error -> {
                    // todo error handling
                }
            }
        }
    }
}

sealed class SplashRouting {
    object ProjectsScreen : SplashRouting()
    object AuthScreen : SplashRouting()
}
