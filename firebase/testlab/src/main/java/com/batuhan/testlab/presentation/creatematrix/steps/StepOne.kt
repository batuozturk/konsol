package com.batuhan.testlab.presentation.creatematrix.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.batuhan.testlab.R
import com.batuhan.testlab.presentation.creatematrix.StepOneState
import com.batuhan.testlab.presentation.creatematrix.TestType
import com.batuhan.theme.KonsolFontFamily
import com.batuhan.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepOne(
    stepOneState: StepOneState,
    updateStepOne: (StepOneState) -> Unit
) {
    val testTimeout by remember(stepOneState.testTimeout) {
        derivedStateOf { stepOneState.testTimeout }
    }
    val disableVideoRecording by remember(stepOneState.disableVideoRecording) {
        derivedStateOf { stepOneState.disableVideoRecording }
    }
    val disablePerformanceMetrics by remember(stepOneState.disablePerformanceMetrics) {
        derivedStateOf { stepOneState.disablePerformanceMetrics }
    }
    val accountTestEnabled by remember(stepOneState.accountTestEnabled) {
        derivedStateOf { stepOneState.accountTestEnabled }
    }
    val testType by remember(stepOneState.testType) {
        derivedStateOf { stepOneState.testType }
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    value = testType.value,
                    readOnly = true,
                    onValueChange = {},
                    trailingIcon = {
                        Icon(
                            if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Orange
                        )
                    },
                    label = {
                        Text(stringResource(id = R.string.test_type))
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
                        ),
                        unfocusedTrailingIconColor = Orange,
                        focusedTrailingIconColor = Orange
                    )
                )
                ExposedDropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onDismissRequest = { expanded = !expanded }
                ) {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(TestType.AndroidRoboTest().value, fontFamily = KonsolFontFamily)
                        },
                        onClick = {
                            updateStepOne.invoke(stepOneState.copy(testType = TestType.AndroidRoboTest()))
                            expanded = !expanded
                        }
                    )
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(
                                TestType.AndroidInstrumentationTest().value,
                                fontFamily = KonsolFontFamily
                            )
                        },
                        onClick = {
                            updateStepOne.invoke(stepOneState.copy(testType = TestType.AndroidInstrumentationTest()))
                            expanded = !expanded
                        }
                    )
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(TestType.AndroidTestLoop().value, fontFamily = KonsolFontFamily)
                        },
                        onClick = {
                            updateStepOne.invoke(stepOneState.copy(testType = TestType.AndroidTestLoop()))
                            expanded = !expanded
                        }
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                value = testTimeout ?: "",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    updateStepOne.invoke(stepOneState.copy(testTimeout = it))
                },
                label = {
                    Text(stringResource(id = R.string.test_timeout))
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
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(id = R.string.disable_video_recording),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = disableVideoRecording,
                    onCheckedChange = {
                        updateStepOne.invoke(stepOneState.copy(disableVideoRecording = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Orange,
                        checkedBorderColor = Orange,
                        checkedThumbColor = Color.White,
                        uncheckedTrackColor = Orange,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = Orange
                    )
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.disable_performance_metrics),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = disablePerformanceMetrics,
                    onCheckedChange = {
                        updateStepOne.invoke(stepOneState.copy(disablePerformanceMetrics = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Orange,
                        checkedBorderColor = Orange,
                        checkedThumbColor = Color.White,
                        uncheckedTrackColor = Orange,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = Orange
                    )
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.enable_test_account),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = accountTestEnabled,
                    onCheckedChange = {
                        updateStepOne.invoke(stepOneState.copy(accountTestEnabled = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Orange,
                        checkedBorderColor = Orange,
                        checkedThumbColor = Color.White,
                        uncheckedTrackColor = Orange,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = Orange
                    )
                )
            }
        }
    }
}
