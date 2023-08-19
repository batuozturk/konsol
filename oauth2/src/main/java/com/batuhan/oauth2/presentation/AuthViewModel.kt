package com.batuhan.oauth2.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.AuthStateManager
import com.batuhan.core.util.Constants
import com.batuhan.core.util.Result
import com.batuhan.core.util.UiState
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
) : ViewModel(), TextFieldEventHandler {

    private val _authScreenState = MutableStateFlow(AuthScreenState())
    val authScreenState = _authScreenState.asStateFlow()

    // todo channel ile ayarla intent ve authstate'i

    private val _authEvent = Channel<AuthEvent> { Channel.BUFFERED }
    val authEvent = _authEvent.receiveAsFlow()

    var email: String? = null
    internal var authState: AuthState? = null

    fun sendAuthRequest(authorizationService: AuthorizationService) {
        email ?: return

        getServiceConfiguration { authorizationServiceConfig ->
            viewModelScope.launch {
                authState = AuthState(authorizationServiceConfig)
                val result = getAuthorizationRequestIntent.invoke(
                    GetAuthorizationRequestIntent.Params(
                        serviceConfiguration = authorizationServiceConfig,
                        clientId = Constants.OAUTH_CLIENT_ID,
                        email = email!!,
                        redirectUri = "com.batuhan.konsol:/auth",
                        scope = Constants.OAUTH_SCOPE,
                        authorizationService = authorizationService
                    )
                )
                when (result) {
                    is Result.Success -> {
                        val intent = result.data
                        _authEvent.send(AuthEvent.LaunchIntent(intent))
                    }

                    is Result.Error -> {
                        // to-do error handling
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
                    // to-do error handling
                }
            }
        }
    }

    override fun onValueChanged(text: String) {
        email = text
        _authScreenState.update {
            it.copy(email = text)
        }
    }

//    fun clearIntent() {
//        _authScreenState.update {
//            it.copy(intent = null)
//        }
//    }

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
                    // to-do error handling
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
                    _authEvent.send(AuthEvent.Success(this@apply))
                }
            }
        }
    }

    fun clearAuthState() {
        authState = null
    }
}

data class AuthScreenState(
    override val isError: Boolean = false,
    override val isLoading: Boolean = false,
    internal val routing: String? = null,
    internal val email: String? = null
) : UiState()

interface TextFieldEventHandler {

    fun onValueChanged(text: String)
}

sealed class AuthEvent {
    data class Success(val authState: AuthState) : AuthEvent()
    data class LaunchIntent(val intent: Intent) : AuthEvent()
    object Error : AuthEvent()
}
