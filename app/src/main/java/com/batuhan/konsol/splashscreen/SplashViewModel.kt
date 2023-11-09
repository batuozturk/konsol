package com.batuhan.konsol.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.batuhan.core.util.AuthStateManager
import com.batuhan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authStateManager: AuthStateManager) :
    ViewModel() {

    private var billingClient: BillingClient? = null

    fun initBillingClient(billingClient: BillingClient) {
        this.billingClient = billingClient
    }

    fun endConnection() {
        billingClient?.endConnection()
    }

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
                        ensureBillingIsDone(true)
                    } ?: run {
                        authStateManager.clearAuthState()
                        ensureBillingIsDone(false)
                    }
                }

                is Result.Error -> {
                    authStateManager.clearAuthState()
                    ensureBillingIsDone(false)
                }
            }
        }
    }

    fun ensureBillingIsDone(authenticated: Boolean) {
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
                                    handleRoutingWithPurchase(purchases.isNotEmpty(), authenticated)
                                }
                            } ?: run {
                                handleRoutingWithPurchase(purchases.isNotEmpty(), authenticated)
                            }
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    viewModelScope.launch {
                        authStateManager.clearAuthState()
                        route.send(SplashRouting.AuthScreen)
                    }
                }
            }
        )
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

    fun handleRoutingWithPurchase(purchaseMade: Boolean, authenticated: Boolean) {
        viewModelScope.launch {
            billingClient?.endConnection()
            if (purchaseMade && authenticated) {
                route.send(SplashRouting.ProjectListScreen)
            } else if (authenticated) {
                route.send(SplashRouting.BillingScreen)
            } else {
                route.send(SplashRouting.AuthScreen)
            }
        }
    }
}

sealed class SplashRouting {
    object ProjectListScreen : SplashRouting()
    object BillingScreen : SplashRouting()
    object AuthScreen : SplashRouting()
}
