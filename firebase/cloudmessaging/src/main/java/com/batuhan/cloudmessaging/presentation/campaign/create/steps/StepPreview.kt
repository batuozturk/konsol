package com.batuhan.cloudmessaging.presentation.campaign.create.steps

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.cloudmessaging.R
import com.batuhan.cloudmessaging.presentation.campaign.create.CampaignType
import com.batuhan.cloudmessaging.presentation.campaign.create.StepOneState
import com.batuhan.cloudmessaging.presentation.campaign.create.StepThreeState
import com.batuhan.cloudmessaging.presentation.campaign.create.StepTwoState
import com.batuhan.theme.Orange

@Composable
fun StepPreview(
    stepOneState: StepOneState,
    stepTwoState: StepTwoState,
    stepThreeState: StepThreeState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                stringResource(id = R.string.notification_preview),
                modifier = Modifier.padding(8.dp)
            )
        }
        item {
            NotificationPreview(stepThreeState)
        }
        item {
            TypeInfoPreview(stepOneState, stepTwoState)
        }
    }
}

@Composable
fun NotificationPreview(stepThreeState: StepThreeState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        PreviewItem(R.string.step_three_notification_title, stepThreeState.title ?: "")
        PreviewItem(R.string.step_three_notification_body, stepThreeState.body ?: "")
        PreviewItem(
            R.string.step_three_notification_image_url,
            stepThreeState.imageUrl.takeIf { it?.isNotEmpty() == true } ?: "undefined"
        )
    }
}

@Composable
fun TypePreview(stepOneState: StepOneState) {
    val value =
        if (stepOneState.campaignType == CampaignType.TOPIC) {
            stringResource(id = R.string.step_preview_topic)
        } else {
            stringResource(id = R.string.step_preview_token)
        }
    Column(modifier = Modifier.fillMaxWidth()) {
        PreviewItem(R.string.notification_type, value)
    }
}

@Composable
fun TypeInfoPreview(stepOneState: StepOneState, stepTwoState: StepTwoState) {
    val title = if (stepOneState.campaignType == CampaignType.TOPIC) {
        R.string.step_two_topic
    } else {
        R.string.step_two_token
    }
    val value =
        if (stepOneState.campaignType == CampaignType.TOPIC) {
            (stepTwoState as? StepTwoState.Topic)?.topicName
        } else {
            (stepTwoState as? StepTwoState.Token)?.deviceToken
        }
    val infoValue =
        if (stepOneState.campaignType == CampaignType.TOPIC) {
            stringResource(id = R.string.step_preview_topic)
        } else {
            stringResource(id = R.string.step_preview_token)
        }
    Column(modifier = Modifier.fillMaxWidth()) {
        PreviewItem(R.string.notification_type, infoValue)
        PreviewItem(title, value ?: "undefined")
    }
}

@Composable
fun PreviewItem(@StringRes title: Int, value: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(stringResource(id = title))
        Text(value)
    }
}
