package com.batuhan.oauth2.presentation

import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.AuthStateManager
import com.batuhan.core.util.Constants
import com.batuhan.core.util.Result
import com.batuhan.oauth2.R
import com.batuhan.oauth2.domain.GetAuthorizationRequestIntent
import com.batuhan.oauth2.domain.GetOauthToken
import com.batuhan.oauth2.domain.GetServiceConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.TokenResponse
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthorizationRequestIntent: GetAuthorizationRequestIntent,
    private val getServiceConfiguration: GetServiceConfiguration,
    private val getOauthToken: GetOauthToken,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthScreenUiState())
    val authScreenUiState = _uiState.asStateFlow()

    private val _authScreenEvent = Channel<AuthScreenEvent> { Channel.BUFFERED }
    val authEvent = _authScreenEvent.receiveAsFlow()

    internal var authState: AuthState? = null

    fun sendAuthRequest(authorizationService: AuthorizationService) {
        clearErrorState()

        getServiceConfiguration { authorizationServiceConfig ->
            viewModelScope.launch {
                authState = AuthState(authorizationServiceConfig)
                val result = getAuthorizationRequestIntent.invoke(
                    GetAuthorizationRequestIntent.Params(
                        serviceConfiguration = authorizationServiceConfig,
                        clientId = Constants.OAUTH_CLIENT_ID,
                        redirectUri = "com.batuhan.konsol:/auth",
                        scope = Constants.OAUTH_SCOPE,
                        authorizationService = authorizationService
                    )
                )
                when (result) {
                    is Result.Success -> {
                        val intent = result.data
                        _authScreenEvent.send(AuthScreenEvent.LaunchIntent(intent))
                    }

                    is Result.Error -> {
                        setErrorState(AuthScreenErrorState.APPAUTH_ERROR)
                    }
                }
            }
        }
    }

    private fun getServiceConfiguration(onSuccess: (config: AuthorizationServiceConfiguration) -> Unit) {
        viewModelScope.launch {
            when (val result = getServiceConfiguration.invoke()) {
                is Result.Success -> {
                    result.data?.let {
                        onSuccess(it)
                    }
                }

                is Result.Error -> {
                    setErrorState(AuthScreenErrorState.APPAUTH_ERROR)
                }
            }
        }
    }

    fun getOauth2Token(
        authorizationResponse: AuthorizationResponse,
        authorizationService: AuthorizationService
    ) {
        viewModelScope.launch {
            val result = getOauthToken.invoke(
                GetOauthToken.Params(
                    authorizationResponse,
                    authorizationService
                )
            )
            when (result) {
                is Result.Success -> {
                    updateAuthState(result.data)
                }

                is Result.Error -> {
                    setErrorState(AuthScreenErrorState.APPAUTH_ERROR)
                }
            }
        }
    }

    fun updateAuthState(response: AuthorizationResponse?, exception: AuthorizationException?) {
        authState?.apply {
            update(response, exception)
        }
    }

    fun updateAuthState(pair: Pair<TokenResponse?, AuthorizationException?>?) {
        val response = pair?.first
        val exception = pair?.second
        response?.accessToken?.let { token ->
            authState?.apply {
                update(response, exception)
                viewModelScope.launch {
                    authStateManager.addAuthState(authState = this@apply)
                    handleRouting(this@apply)
                }
            }
        }
    }

    fun setErrorState(errorState: AuthScreenErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun retryOperation(errorState: AuthScreenErrorState) {
        // todo decide retry operation
    }

    fun handleRouting(authState: AuthState) {
        viewModelScope.launch {
            _authScreenEvent.send(AuthScreenEvent.Success(authState))
        }
    }
}

data class AuthScreenUiState(
    val errorState: AuthScreenErrorState? = null,
    val isLoading: Boolean = false,
    val routing: String? = null
)

enum class AuthScreenErrorState(@StringRes val titleResId: Int, @StringRes val actionResId: Int?) {
    APPAUTH_ERROR(R.string.error_occurred, null)
}

sealed class AuthScreenEvent {
    data class Success(val authState: AuthState) : AuthScreenEvent()
    data class LaunchIntent(val intent: Intent) : AuthScreenEvent()
}
