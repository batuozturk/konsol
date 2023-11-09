package com.batuhan.oauth2.presentation

import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.batuhan.core.util.AuthStateManager
import com.batuhan.core.util.Constants
import com.batuhan.core.util.Result
import com.batuhan.oauth2.R
import com.batuhan.oauth2.domain.GetAuthorizationRequestIntent
import com.batuhan.oauth2.domain.GetOauthToken
import com.batuhan.oauth2.domain.GetServiceConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
) : ViewModel(), PurchasesUpdatedListener {

    private val _uiState = MutableStateFlow(AuthScreenUiState())
    val authScreenUiState = _uiState.asStateFlow()

    private val _authScreenEvent = Channel<AuthScreenEvent> { Channel.BUFFERED }
    val authEvent = _authScreenEvent.receiveAsFlow()

    internal var authState: AuthState? = null
    var billingClient: BillingClient? = null

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
                    ensureBillingIsDone(this@apply)
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

    fun initBillingClient(billingClient: BillingClient) {
        this.billingClient = billingClient
    }

    fun endConnection() {
        billingClient?.endConnection()
    }

    fun ensureBillingIsDone(authState: AuthState) {
        billingClient?.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        billingClient?.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder()
                                .setProductType(BillingClient.ProductType.SUBS).build()
                        ) { result, purchases ->
                            purchases.find { !it.isAcknowledged }?.let {
                                acknowledgePurchase(it) {
                                    handleRoutingWithPurchase(authState, purchases.isNotEmpty())
                                }
                            } ?: run {
                                handleRoutingWithPurchase(authState, purchases.isNotEmpty())
                            }
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    retryConnection(authState)
                }
            }
        )
    }

    fun retryConnection(authState: AuthState) {
        val maxTries = 3
        var tries = 1
        var isConnectionEstablished = false
        do {
            runCatching {
                billingClient!!.startConnection(object : BillingClientStateListener {
                    override fun onBillingServiceDisconnected() {
                        throw Exception()
                    }

                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            isConnectionEstablished = true
                            billingClient?.queryPurchasesAsync(
                                QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.SUBS).build()
                            ) { result, purchases ->
                                purchases.find { !it.isAcknowledged }?.let {
                                    acknowledgePurchase(it) {
                                        handleRoutingWithPurchase(authState, purchases.isNotEmpty())
                                    }
                                }
                            }
                        }
                    }
                })
            }.getOrElse {
                tries++
            }
        } while (tries <= maxTries && !isConnectionEstablished)
    }

    fun acknowledgePurchase(
        purchase: Purchase,
        navigateToMain: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = billingClient?.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                    .build()
            )
            when (result?.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    navigateToMain.invoke()
                }
            }
        }
    }

    fun handleRoutingWithPurchase(authState: AuthState, isPurchased: Boolean) {
        endConnection()
        viewModelScope.launch {
            _authScreenEvent.send(AuthScreenEvent.Success(authState, isPurchased))
        }
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        // no-op
    }
}

sealed class BillingScreenEvent {
    object AuthScreen : BillingScreenEvent()

    object ProjectListScreen : BillingScreenEvent()
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
    data class Success(val authState: AuthState, val purchased: Boolean) : AuthScreenEvent()
    data class LaunchIntent(val intent: Intent) : AuthScreenEvent()
}
