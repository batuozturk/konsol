package com.batuhan.management.presentation.createproject.steps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuhan.management.R
import com.batuhan.core.data.model.management.AnalyticsAccount
import com.batuhan.management.presentation.createproject.StepThreeState
import com.batuhan.management.presentation.createproject.StepTitle
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange

@Composable
fun StepThree(
    stepThreeState: StepThreeState,
    analyticsAccounts: List<AnalyticsAccount>,
    saveThirdStep: (Boolean) -> Unit,
    selectAnalyticsAccount: (analyticsAccountId: String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
    ) {
        StepTitle(
            title = stringResource(id = R.string.step_three_title)
        )
        StepThreeContent(
            stepThreeState = stepThreeState,
            analyticsAccounts = analyticsAccounts,
            selectAnalyticsAccount = selectAnalyticsAccount,
            saveThirdStep = saveThirdStep
        )
    }
}

@Composable
fun StepThreeContent(
    stepThreeState: StepThreeState,
    analyticsAccounts: List<AnalyticsAccount>,
    selectAnalyticsAccount: (analyticsAccountId: String) -> Unit,
    saveThirdStep: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            SwitchText(
                title = stringResource(id = R.string.enable_analytics_title),
                isEnabled = stepThreeState.isGoogleAnalyticsEnabled,
                onAnalyticsEnabled = saveThirdStep
            )
        }
        AnimatedVisibility(stepThreeState.isGoogleAnalyticsEnabled) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(analyticsAccounts.size) {
                    AnalyticsAccountListItem(
                        analyticsAccount = analyticsAccounts[it],
                        selectedAccountId = stepThreeState.googleAnalyticsAccountId ?: "",
                        selectAnalyticsAccount = selectAnalyticsAccount
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsAccountListItem(
    analyticsAccount: AnalyticsAccount,
    selectedAccountId: String,
    selectAnalyticsAccount: (analyticsAccountId: String) -> Unit
) {
    val isSelectedAccount = selectedAccountId == analyticsAccount.id
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                selectAnalyticsAccount(analyticsAccount.id ?: return@clickable)
            }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = analyticsAccount.name ?: "")
        if (isSelectedAccount) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}

@Composable
fun SwitchText(title: String, isEnabled: Boolean, onAnalyticsEnabled: (Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch
            ) {
                onAnalyticsEnabled.invoke(!isEnabled)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            maxLines = 2,
            modifier = Modifier.weight(5f)
        )
        Switch(
            modifier = Modifier.weight(1f),
            checked = isEnabled,
            onCheckedChange = {
                onAnalyticsEnabled.invoke(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Orange,
                checkedBorderColor = Orange,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Orange,
                uncheckedBorderColor = Orange,
                uncheckedIconColor = Orange
            )
        )
    }
}

@Preview
@Composable
fun StepThreePreview() {
    KonsolTheme {
        StepThree(
            stepThreeState = StepThreeState(),
            analyticsAccounts = listOf(),
            saveThirdStep = { },
            selectAnalyticsAccount = {}
        )
    }
}
