package com.batuhan.management.presentation.createproject

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.management.R
import com.batuhan.management.data.model.AnalyticsAccount
import com.batuhan.management.data.model.AvailableLocation
import com.batuhan.management.data.model.ProjectInfo
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_SAVE_PROJECT
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_SUCCESS
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_TWO
import com.batuhan.management.presentation.createproject.steps.StepContent
import com.batuhan.theme.DarkGreen
import com.batuhan.theme.FConsoleTheme
import com.batuhan.theme.Orange
import kotlinx.coroutines.flow.flowOf

@Composable
fun CreateProjectScreen(onDismiss: () -> Unit) {
    val viewModel = hiltViewModel<CreateProjectViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val availableProjects = viewModel.availableProjects.collectAsLazyPagingItems()
    val analyticsAccounts by viewModel.analyticsAccounts.collectAsStateWithLifecycle()
    val availableLocations = viewModel.availableLocations.collectAsLazyPagingItems()
    CreateProjectScreenContent(
        uiState = uiState,
        availableProjects = availableProjects,
        analyticsAccounts = analyticsAccounts,
        availableLocations = availableLocations,
        onDismiss = onDismiss,
        updateStep = viewModel::updateStep,
        retryOperation = viewModel::retryOperation,
        saveFirstStep = viewModel::saveFirstStep,
        onProjectIdChange = viewModel::updateProjectId,
        onProjectNameChange = viewModel::updateProjectName,
        saveSecondStep = viewModel::saveSecondStep,
        onAnalyticsEnabled = { enabled ->
            viewModel.setGoogleAnalyticsEnabled(enabled)
            if (enabled) viewModel.getAnalyticsAccounts()
        },
        saveThirdStep = viewModel::saveThirdStep,
        saveFourthStep = viewModel::setFourthStep,
        saveProjectToFirebase = viewModel::saveProject
    )
}

@Composable
fun CreateProjectScreenContent(
    uiState: CreateProjectUiState,
    availableProjects: LazyPagingItems<ProjectInfo>,
    analyticsAccounts: List<AnalyticsAccount>,
    availableLocations: LazyPagingItems<AvailableLocation>,
    onDismiss: () -> Unit,
    updateStep: (step: Int) -> Unit,
    retryOperation: (CreateProjectErrorState) -> Unit,
    saveFirstStep: (isCreatingFromScratch: Boolean) -> Unit,
    onProjectNameChange: (projectName: String) -> Unit,
    onProjectIdChange: (projectId: String) -> Unit,
    saveSecondStep: (projectId: String?, name: String?) -> Unit,
    onAnalyticsEnabled: (Boolean) -> Unit,
    saveThirdStep: (analyticsAccountId: String) -> Unit,
    saveFourthStep: (projectId: String) -> Unit,
    saveProjectToFirebase: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TopContent(
                uiState = uiState,
                onDismiss = onDismiss,
                saveProjectToFirebase = saveProjectToFirebase,
                changeStep = updateStep,
                retryOperation = retryOperation
            )
            StepContent(
                uiState = uiState,
                projects = availableProjects,
                analyticsAccounts = analyticsAccounts,
                availableLocations = availableLocations,
                onProjectNameChange = onProjectNameChange,
                onProjectIdChange = onProjectIdChange,
                onAnalyticsEnabled = onAnalyticsEnabled,
                saveThirdStep = saveThirdStep,
                saveFirstStep = saveFirstStep,
                saveSecondStep = saveSecondStep,
                saveFourthStep = saveFourthStep
            )
        }
    }
}

@Composable
fun TopContent(
    uiState: CreateProjectUiState,
    onDismiss: () -> Unit,
    saveProjectToFirebase: () -> Unit,
    changeStep: (Int) -> Unit,
    retryOperation: (CreateProjectErrorState) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onDismiss.invoke() }) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Orange)
        }
        StepButtons(
            currentStep = uiState.currentStep,
            errorState = uiState.errorState,
            isLoading = uiState.isLoading,
            onDismiss = onDismiss,
            changeStep = changeStep,
            saveProjectToFirebase = saveProjectToFirebase,
            retryOperation = retryOperation
        )
    }
}

@Composable
fun StepButtons(
    currentStep: Int,
    errorState: CreateProjectErrorState?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    changeStep: (Int) -> Unit,
    saveProjectToFirebase: () -> Unit,
    retryOperation: (CreateProjectErrorState) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (currentStep in STEP_TWO until STEP_SUCCESS) {
            OutlinedButton(
                onClick = {
                    changeStep(currentStep - 1)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Orange)
            ) {
                Text(stringResource(id = R.string.button_back), color = Orange)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        if (errorState != null) {
            Button(
                onClick = {
                    retryOperation(errorState)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    text = stringResource(id = errorState.messageResId),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            when (currentStep) {
                STEP_SAVE_PROJECT -> {
                    Button(
                        onClick = {
                            if (!isLoading) {
                                saveProjectToFirebase.invoke()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier
                                    .height(16.dp)
                                    .aspectRatio(1f),
                                strokeWidth = 2.dp
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = stringResource(id = R.string.step_wait_title),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.button_save),
                                color = Color.White
                            )
                        }
                    }
                }
                STEP_SUCCESS -> {
                    Button(
                        onClick = {
                            if (!isLoading) {
                                onDismiss.invoke()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier
                                    .height(16.dp)
                                    .aspectRatio(1f),
                                strokeWidth = 2.dp
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = stringResource(id = R.string.step_wait_title),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.step_success),
                                color = Color.White
                            )
                        }
                    }
                }
                else -> {
                    Button(
                        onClick = {
                            if (!isLoading) {
                                changeStep(currentStep + 1)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Orange)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier
                                    .height(16.dp)
                                    .aspectRatio(1f),
                                strokeWidth = 2.dp
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = stringResource(id = R.string.step_wait_title),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.button_next),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateProjectScreenPreview() {
    FConsoleTheme {
        CreateProjectScreenContent(
            onDismiss = { /*TODO*/ },
            availableProjects = flowOf(PagingData.empty<ProjectInfo>()).collectAsLazyPagingItems(),
            analyticsAccounts = listOf(),
            uiState = CreateProjectUiState(),
            availableLocations = flowOf(PagingData.empty<AvailableLocation>()).collectAsLazyPagingItems(),
            onAnalyticsEnabled = { _ -> },
            saveThirdStep = { _ -> },
            saveFirstStep = { _ -> },
            saveSecondStep = { _, _ -> },
            saveFourthStep = { _ -> },
            saveProjectToFirebase = {},
            onProjectNameChange = {},
            onProjectIdChange = {},
            updateStep = {},
            retryOperation = {}
        )
    }
}
