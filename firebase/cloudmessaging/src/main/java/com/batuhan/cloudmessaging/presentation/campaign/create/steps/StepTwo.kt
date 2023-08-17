package com.batuhan.cloudmessaging.presentation.campaign.create.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.cloudmessaging.R
import com.batuhan.cloudmessaging.presentation.campaign.create.StepTwoState
import com.batuhan.theme.Orange

@Composable
fun StepTwo(stepTwoState: StepTwoState, updateStepTwo: (StepTwoState) -> Unit) {
    when (stepTwoState) {
        is StepTwoState.Topic -> StepTwoTopic(stepTwoState, updateStepTwo)
        is StepTwoState.Token -> StepTwoToken(stepTwoState, updateStepTwo)
        else -> {
        }
    }
}

@Composable
fun StepTwoTopic(stepTwoState: StepTwoState.Topic, updateSecondStep: (StepTwoState) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = stepTwoState.topicName ?: "",
            onValueChange = {
                updateSecondStep.invoke(stepTwoState.copy(topicName = it))
            },
            label = {
                Text(stringResource(id = R.string.step_two_topic))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(
                    handleColor = Orange,
                    backgroundColor = Orange.copy(alpha = 0.4f)
                )
            )
        )
        Text(stringResource(R.string.step_one_topic_desc))
    }
}

@Composable
fun StepTwoToken(stepTwoState: StepTwoState.Token, updateSecondStep: (StepTwoState) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = stepTwoState.deviceToken ?: "",
            onValueChange = {
                updateSecondStep.invoke(stepTwoState.copy(deviceToken = it))
            },
            label = {
                Text(stringResource(id = R.string.step_two_token))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(
                    handleColor = Orange,
                    backgroundColor = Orange.copy(alpha = 0.4f)
                )
            )
        )
        Text(stringResource(R.string.step_one_token_desc))
    }
}
