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
            when (val result = authStateManager.getAuthState()) {
                is Result.Success -> {
                    result.data?.let {
                        authStateManager.setAuthState(it)
                        delay(3000L)
                        route.send(SplashRouting.ProjectListScreen)
                    } ?: run {
                        authStateManager.clearAuthState()
                        route.send(SplashRouting.AuthScreen)
                    }
                }
                is Result.Error -> {
                    authStateManager.clearAuthState()
                    route.send(SplashRouting.AuthScreen)
                }
            }
        }
    }
}

sealed class SplashRouting {
    object ProjectListScreen : SplashRouting()
    object AuthScreen : SplashRouting()
}
