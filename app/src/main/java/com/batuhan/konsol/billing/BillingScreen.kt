package com.batuhan.konsol.billing

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.batuhan.konsol.R
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange

@Composable
fun BillingScreen(
    onNavigateToProjectListScreen: () -> Unit,
    logout: () -> Unit
) {
    val viewModel = hiltViewModel<BillingViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val productDetails by viewModel.productDetails.collectAsStateWithLifecycle()
    val context = LocalContext.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val billingClient = BillingClient.newBuilder(context)
                    .setListener(viewModel)
                    .enablePendingPurchases()
                    .build()
                viewModel.initBillingClient(billingClient)
                viewModel.startConnection()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.endConnection()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.routing.collect {
            when (it) {
                BillingScreenEvent.AuthScreen -> logout.invoke()
                BillingScreenEvent.ProjectListScreen -> onNavigateToProjectListScreen.invoke()
            }
        }
    }
    var selectedId by remember {
        mutableStateOf("")
    }

    val isEmpty by remember(productDetails) {
        derivedStateOf { productDetails.isEmpty() }
    }
    val isLoading by remember {
        mutableStateOf(true)
    }
    if (!isEmpty) {
        BillingScreenContent(
            products = productDetails,
            selectedId = selectedId,
            setSelectedId = { selectedId = it },
            logout = viewModel::logout,
            createPurchase = {
                selectedId.takeIf { it.isNotEmpty() }
                    ?.let {
                        val activity = context.findActivity() ?: return@let
                        viewModel.createPurchase(it) { params ->
                            viewModel.billingClient?.launchBillingFlow(activity, params)
                        }
                    }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreenContent(
    products: List<ProductDetails>,
    selectedId: String,
    setSelectedId: (String) -> Unit,
    logout: () -> Unit,
    createPurchase: () -> Unit
) {
    val productList = listOf(
        "Firebase Project Management",
        "Firestore",
        "Realtime Database",
        "FCM",
        "Cloud Storage",
        "Test Lab"
    )
    val monthly by remember(products) {
        derivedStateOf { products.find { it.productId == "konsol_monthly" } }
    }
    val sixMonth by remember(products) {
        derivedStateOf { products.find { it.productId == "konsol_6_month" } }
    }
    val annual by remember(products) {
        derivedStateOf { products.find { it.productId == "konsol_annual" } }
    }
    val then = stringResource(id = R.string.price_then)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.select_plan))
                },
                actions = {
                    IconButton(
                        onClick = {
                            logout.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it).padding(horizontal = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(9f)
            ) {
                item {
                    Text(
                        stringResource(R.string.billing_desc),
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }
                items(productList.size) {
                    ProductItem(text = productList[it])
                }
                monthly?.let {
                    val priceDetails =
                        it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.filter { it.formattedPrice.any { it.isDigit() } }
                            ?.get(0)
                    val priceText =
                        then + " " + priceDetails?.formattedPrice
                    item {
                        PurchaseItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            title = stringResource(R.string.one_month_title),
                            description = stringResource(R.string.one_month_desc),
                            price = priceText + stringResource(id = R.string.one_month_price),
                            isSelected = selectedId == "konsol_monthly",
                            launchFlow = {
                                setSelectedId("konsol_monthly")
                            }
                        )
                    }
                }
                sixMonth?.let {
                    val priceDetails =
                        it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.filter { it.formattedPrice.any { it.isDigit() } }
                            ?.get(0)
                    val priceText =
                        then + " " + priceDetails?.formattedPrice
                    item {
                        PurchaseItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            title = stringResource(R.string.six_month_title),
                            description = stringResource(R.string.six_month_desc),
                            price = priceText + stringResource(id = R.string.six_month_price),
                            isSelected = selectedId == "konsol_6_month",
                            launchFlow = {
                                setSelectedId("konsol_6_month")
                            }
                        )
                    }
                }
                annual?.let {
                    val priceDetails =
                        it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.filter { it.formattedPrice.any { it.isDigit() } }
                            ?.get(0)
                    val priceText =
                        then + " " + priceDetails?.formattedPrice
                    item {
                        PurchaseItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            title = stringResource(R.string.annual_title),
                            description = stringResource(R.string.annual_desc),
                            price = priceText + stringResource(id = R.string.annual_price),
                            isSelected = selectedId == "konsol_annual",
                            launchFlow = {
                                setSelectedId("konsol_annual")
                            }
                        )
                    }
                }
            }
            Button(
                onClick = {
                    createPurchase.invoke()
                },
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text(text = stringResource(R.string.continue_text))
            }
        }
    }
}

@Composable
fun ProductItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text)
        Icon(imageVector = Icons.Default.Done, tint = Orange, contentDescription = null)
    }
}

@Composable
fun PurchaseItem(
    modifier: Modifier,
    title: String,
    isSelected: Boolean,
    description: String,
    price: String,
    launchFlow: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                launchFlow.invoke()
            }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = isSelected,
            onClick = {
                launchFlow.invoke()
            },
            colors = RadioButtonDefaults.colors(selectedColor = Orange)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    launchFlow.invoke()
                }
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            Text(description)
            Text(price)
        }
    }
}

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun getPrice(productDetail: ProductDetails) {
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO)
fun BillingPreview() {
    KonsolTheme {
        BillingScreenContent(
            products = listOf(),
            selectedId = "null",
            setSelectedId = {},
            logout = { /*TODO*/ }
        ) {
        }
    }
}
