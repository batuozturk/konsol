package com.batuhan.konsol.billing

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.android.billingclient.api.BillingClient
import com.batuhan.konsol.R
import com.batuhan.theme.Orange
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    onNavigateToProjectListScreen: () -> Unit,
    logout: () -> Unit
) {
    val viewModel = hiltViewModel<BillingViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.select_plan))
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.logout()
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
                .fillMaxSize()
                .padding(it)
        ) {
            PurchaseItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                title = stringResource(R.string.one_month_title),
                description = stringResource(R.string.one_month_desc),
                price = stringResource(R.string.one_month_price),
                isSelected = selectedId == "konsol_monthly",
                launchFlow = {
                    selectedId = "konsol_monthly"
                }
            )
            PurchaseItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                title = stringResource(R.string.six_month_title),
                price = stringResource(R.string.six_month_price),
                description = stringResource(R.string.six_month_desc),
                isSelected = selectedId == "konsol_6_month",
                launchFlow = {
                    selectedId = "konsol_6_month"
                }
            )
            PurchaseItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                title = stringResource(R.string.annual_title),
                price = stringResource(R.string.annual_price),
                description = stringResource(R.string.annual_desc),
                isSelected = selectedId == "konsol_annual",
                launchFlow = {
                    selectedId = "konsol_annual"
                }
            )
            Button(
                onClick = {
                    selectedId.takeIf { it.isNotEmpty() }
                        ?.let {
                            val activity = context.findActivity() ?: return@Button
                            viewModel.createPurchase(it) { params ->
                                val result =
                                    viewModel.billingClient?.launchBillingFlow(activity, params)
                                Firebase.analytics.logEvent(
                                    "billing_result",
                                    Bundle().apply {
                                        putString(
                                            "billing_result_code",
                                            result?.responseCode.toString()
                                        )
                                        putString("billing_result_message", result?.debugMessage)
                                    }
                                )
                            }
                        }
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .weight(0.2f),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text(text = stringResource(R.string.continue_text))
            }
        }
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
            .padding(10.dp),
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
