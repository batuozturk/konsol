package com.batuhan.management.presentation.createproject.steps

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuhan.management.R
import com.batuhan.management.presentation.createproject.StepOneState
import com.batuhan.management.presentation.createproject.StepTitle
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange

@Composable
fun StepOne(
    stepOneState: StepOneState,
    saveFirstStep: (isCreatingFromScratch: Boolean) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
    ) {
        StepTitle(
            title = stringResource(id = R.string.step_one_title)
        )
        StepOneContent(
            stepOneState = stepOneState,
            saveFirstStep = saveFirstStep
        )
    }
}

@Composable
fun StepOneContent(
    stepOneState: StepOneState,
    saveFirstStep: (isCreatingFromScratch: Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
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
            textAlign = TextAlign.Start
        )
    }
}

@Composable
@Preview
fun StepOnePreview() {
    KonsolTheme {
        StepOne(StepOneState(), {})
    }
}
