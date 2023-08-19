package com.batuhan.management.presentation.createproject.steps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuhan.management.R
import com.batuhan.management.presentation.createproject.StepOneState
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange

@Composable
fun StepOne(
    stepOneState: StepOneState,
    currentStep: Int,
    saveFirstStep: (isCreatingFromScratch: Boolean) -> Unit
) {
    val isCurrentStep = currentStep == STEP_ONE
    val isPassedStep = currentStep > STEP_ONE
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp)
    ) {
        StepTitle(
            title = stringResource(id = R.string.step_one_title),
            isPassedStep = isPassedStep
        )
        StepOneContent(
            stepOneState = stepOneState,
            isCurrentStep = isCurrentStep,
            saveFirstStep = saveFirstStep
        )
    }
}

@Composable
fun StepOneContent(
    stepOneState: StepOneState,
    isCurrentStep: Boolean,
    saveFirstStep: (isCreatingFromScratch: Boolean) -> Unit
) {
    AnimatedVisibility(isCurrentStep) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepOneItem(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        saveFirstStep(false)
                    }
                    .border(2.dp, Orange, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                title = stringResource(id = R.string.step_one_choice_one),
                isSelected = stepOneState.isCreatingFromScratch == false,
                saveFirstStep = {
                    saveFirstStep(false)
                }
            )
            StepOneItem(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        saveFirstStep(true)
                    }
                    .border(2.dp, Orange, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                title = stringResource(id = R.string.step_one_choice_two),
                isSelected = stepOneState.isCreatingFromScratch == true,
                saveFirstStep = {
                    saveFirstStep(true)
                }
            )
        }
    }
}

@Composable
fun StepOneItem(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    saveFirstStep: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = isSelected,
            onClick = {
                saveFirstStep.invoke()
            },
            colors = RadioButtonDefaults.colors(selectedColor = Orange)
        )
        Text(
            text = title,
            textAlign = TextAlign.Start,
            minLines = 2,
            maxLines = 3
        )
    }
}

@Composable
@Preview
fun StepOnePreview() {
    KonsolTheme {
        StepOne(StepOneState(), STEP_ONE, {})
    }
}
