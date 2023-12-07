package com.batuhan.konsol.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.batuhan.core.util.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(private val authStateManager: AuthStateManager) :
    ViewModel(), PurchasesUpdatedListener {

    private val _routing: Channel<BillingScreenEvent> = Channel { Channel.BUFFERED }
    val routing = _routing.receiveAsFlow()

    val productDetails = MutableStateFlow(listOf<ProductDetails>())
    var billingClient: BillingClient? = null

    fun initBillingClient(billingClient: BillingClient) {
        this.billingClient = billingClient
    }

    fun endConnection() {
        billingClient?.endConnection()
    }

    fun createPurchase(productId: String, launchFlow: (BillingFlowParams) -> Unit) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    )
                )
                .build()
        billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { result, products ->
            val productDetailList = products.map {
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(it)
                    .setOfferToken(it.subscriptionOfferDetails?.get(0)?.offerToken ?: "")
                    .build()
            }

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailList)
                .build()

            launchFlow.invoke(billingFlowParams)
        }
    }

    fun startConnection() {
        billingClient?.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        billingClient?.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder()
                                .setProductType(BillingClient.ProductType.SUBS).build()
                        ) { result, purchases ->
                            if (purchases.isNotEmpty()) {
                                purchases.find { !it.isAcknowledged }?.let {
                                    acknowledgePurchase(it) {
                                        onNavigateToMain()
                                    }
                                }
                            }
                        }
                        val queryProductDetailsParams =
                            QueryProductDetailsParams.newBuilder()
                                .setProductList(
                                    listOf(
                                        QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId("konsol_annual")
                                            .setProductType(BillingClient.ProductType.SUBS)
                                            .build(),
                                        QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId("konsol_6_month")
                                            .setProductType(BillingClient.ProductType.SUBS)
                                            .build(),
                                        QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId("konsol_monthly")
                                            .setProductType(BillingClient.ProductType.SUBS)
                                            .build()
                                    )
                                )
                                .build()
                        billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { result, products ->
                            productDetails.value = products
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    retryConnection()
                }
            }
        )
    }

    fun retryConnection() {
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
                                if (purchases.isNotEmpty()) {
                                    purchases.find { !it.isAcknowledged }?.let {
                                        acknowledgePurchase(it) {
                                            onNavigateToMain()
                                        }
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

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.find { !it.isAcknowledged }?.let {
                acknowledgePurchase(it) {
                    onNavigateToMain()
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authStateManager.deleteAuthState()
            _routing.send(BillingScreenEvent.AuthScreen)
        }
    }

    fun onNavigateToMain() {
        viewModelScope.launch {
            _routing.send(BillingScreenEvent.ProjectListScreen)
        }
    }
}

sealed class BillingScreenEvent {
    object AuthScreen : BillingScreenEvent()

    object ProjectListScreen : BillingScreenEvent()
}
