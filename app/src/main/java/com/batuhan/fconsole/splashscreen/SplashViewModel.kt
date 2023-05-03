package com.batuhan.fconsole.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.batuhan.core.util.Result

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
            val result = authStateManager.getAuthState()
            when(result){
                is Result.Success -> {
                    result.data?.let {
                        route.send(SplashRouting.ProjectsScreen)
                    } ?: run {
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
