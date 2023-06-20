package com.batuhan.management.presentation.createproject.steps

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.batuhan.management.R
import com.batuhan.management.data.model.AnalyticsAccount
import com.batuhan.management.data.model.AvailableLocation
import com.batuhan.management.data.model.ProjectInfo
import com.batuhan.management.presentation.createproject.CreateProjectErrorState
import com.batuhan.management.presentation.createproject.CreateProjectUiState
import com.batuhan.theme.Orange

internal const val STEP_ONE = 1
internal const val STEP_TWO = 2
internal const val STEP_THREE = 3
internal const val STEP_FOUR = 4

@Composable
fun StepContent(
    uiState: CreateProjectUiState,
    projects: LazyPagingItems<ProjectInfo>,
    analyticsAccounts: List<AnalyticsAccount>,
    availableLocations: LazyPagingItems<AvailableLocation>,
    saveFirstStep: (isCreatingFromScratch: Boolean) -> Unit,
    onProjectNameChange: (projectName: String) -> Unit,
    onProjectIdChange: (projectId: String) -> Unit,
    saveSecondStep: (projectId: String?, name: String?) -> Unit,
    onAnalyticsEnabled: (Boolean) -> Unit,
    saveThirdStep: (analyticsAccountId: String) -> Unit,
    saveFourthStep: (projectId: String) -> Unit
) {
    val stepOneState by remember(uiState.stepOneState) {
        derivedStateOf { uiState.stepOneState }
    }
    val stepTwoState by remember(uiState.stepTwoState) {
        derivedStateOf { uiState.stepTwoState }
    }
    val stepThreeState by remember(uiState.stepThreeState) {
        derivedStateOf { uiState.stepThreeState }
    }
    val stepFourState by remember(uiState.stepFourState) {
        derivedStateOf { uiState.stepFourState }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf {
            uiState.errorState.takeIf {
                it == CreateProjectErrorState.FIREBASE_ERROR || it == CreateProjectErrorState.GOOGLE_CLOUD_ERROR
            }
        }
    }
    val currentStep by remember(uiState.currentStep) {
        derivedStateOf { uiState.currentStep }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ErrorInfo(errorState)
        StepOne(
            stepOneState = stepOneState,
            currentStep = currentStep,
            saveFirstStep = saveFirstStep
        )
        StepTwo(
            stepTwoState = stepTwoState,
            currentStep = currentStep,
            isCreatingFromScratch = stepOneState.isCreatingFromScratch ?: false,
            projects = projects,
            onProjectIdChange = onProjectIdChange,
            onProjectNameChange = onProjectNameChange,
            saveSecondStep = saveSecondStep
        )
        StepThree(
            stepThreeState = stepThreeState,
            currentStep = currentStep,
            analyticsAccounts = analyticsAccounts,
            saveThirdStep = onAnalyticsEnabled,
            selectAnalyticsAccount = saveThirdStep
        )
        StepFour(
            stepFourState = stepFourState,
            currentStep = currentStep,
            availableLocations = availableLocations,
            saveStepFour = saveFourthStep
        )
    }
}

@Composable
fun StepTitle(
    title: String,
    isPassedStep: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(8f)
        )
        if (isPassedStep) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ErrorInfo(errorState: CreateProjectErrorState?) {
    if (errorState != null) {
        val textResId = if (errorState == CreateProjectErrorState.FIREBASE_ERROR) {
            R.string.firebase_error_desc
        } else {
            R.string.gcloud_error_desc
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(2.dp, Color.Red, RoundedCornerShape(10.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(id = textResId),
                modifier = Modifier.padding(4.dp),
                fontSize = 12.sp
            )
        }
    }
}
