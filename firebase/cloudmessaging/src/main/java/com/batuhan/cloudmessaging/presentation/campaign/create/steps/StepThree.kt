package com.batuhan.cloudmessaging.presentation.campaign.create.steps

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.cloudmessaging.R
import com.batuhan.cloudmessaging.presentation.campaign.create.StepThreeState
import com.batuhan.theme.Orange

@Composable
fun StepThree(stepThreeState: StepThreeState, updateStepThree: (StepThreeState) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        StepThreeTextField(stepThreeState.title ?: "", R.string.step_three_notification_title) {
            updateStepThree.invoke(stepThreeState.copy(title = it))
        }
        Spacer(modifier = Modifier.height(8.dp))
        StepThreeTextField(stepThreeState.body ?: "", R.string.step_three_notification_body) {
            updateStepThree.invoke(stepThreeState.copy(body = it))
        }
        Spacer(modifier = Modifier.height(8.dp))
        StepThreeTextField(
            stepThreeState.imageUrl ?: "",
            R.string.step_three_notification_image_url
        ) {
            updateStepThree.invoke(stepThreeState.copy(imageUrl = it))
        }
    }
}

@Composable
fun StepThreeTextField(
    value: String,
    @StringRes labelResId: Int,
    updateStepThree: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = {
            updateStepThree.invoke(it)
        },
        label = {
            Text(stringResource(id = labelResId))
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
}
