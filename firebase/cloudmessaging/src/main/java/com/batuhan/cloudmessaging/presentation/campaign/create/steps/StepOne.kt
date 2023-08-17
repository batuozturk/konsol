package com.batuhan.cloudmessaging.presentation.campaign.create.steps

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.cloudmessaging.R
import com.batuhan.cloudmessaging.presentation.campaign.create.CampaignType
import com.batuhan.cloudmessaging.presentation.campaign.create.StepOneState
import com.batuhan.theme.Orange

@Composable
fun StepOne(
    stepOneState: StepOneState,
    updateStepOne: (StepOneState) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StepOneItem(
            CampaignType.TOPIC,
            stepOneState.campaignType == CampaignType.TOPIC,
            R.string.step_one_topic_title,
            R.string.step_one_topic_desc,
            Icons.Default.List
        ) {
            updateStepOne.invoke(stepOneState.copy(campaignType = it))
        }
        StepOneItem(
            CampaignType.TOKEN,
            stepOneState.campaignType == CampaignType.TOKEN,
            R.string.step_one_token_title,
            R.string.step_one_token_desc,
            Icons.Default.PhoneAndroid
        ) {
            updateStepOne.invoke(stepOneState.copy(campaignType = it))
        }
    }
}

@Composable
fun StepOneItem(
    campaignType: CampaignType,
    isSelected: Boolean,
    @StringRes titleResId: Int,
    @StringRes descriptionResId: Int,
    imageVector: ImageVector,
    setCampaignType: (CampaignType) -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp).fillMaxWidth().clickable {
            setCampaignType(campaignType)
        }.border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = {
                setCampaignType.invoke(campaignType)
            },
            colors = RadioButtonDefaults.colors(selectedColor = Orange)
        )
        StepOneContent(titleResId, descriptionResId, imageVector)
    }
}

@Composable
fun StepOneContent(
    @StringRes titleResId: Int,
    @StringRes descriptionResId: Int,
    imageVector: ImageVector
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = Orange,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(stringResource(titleResId))
        Spacer(modifier = Modifier.height(20.dp))
        Text(stringResource(descriptionResId))
    }
}
