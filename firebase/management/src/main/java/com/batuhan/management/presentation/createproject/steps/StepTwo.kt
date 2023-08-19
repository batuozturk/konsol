package com.batuhan.management.presentation.createproject.steps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.management.R
import com.batuhan.management.data.model.ProjectInfo
import com.batuhan.management.presentation.createproject.StepTwoState
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange
import kotlinx.coroutines.flow.flowOf

@Composable
fun StepTwo(
    stepTwoState: StepTwoState,
    currentStep: Int,
    isCreatingFromScratch: Boolean,
    projects: LazyPagingItems<ProjectInfo>,
    saveSecondStep: (projectId: String?, name: String?) -> Unit,
    onProjectNameChange: (projectName: String) -> Unit,
    onProjectIdChange: (projectId: String) -> Unit
) {
    val isCurrentStep = currentStep == STEP_TWO
    val isPassedStep = currentStep > STEP_TWO
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp)
    ) {
        StepTitle(
            title = stringResource(id = R.string.step_two_title),
            isPassedStep = isPassedStep
        )
        StepTwoContent(
            stepTwoState = stepTwoState,
            projects = projects,
            isCurrentStep = isCurrentStep,
            isCreatingFromScratch = isCreatingFromScratch,
            saveSecondStep = saveSecondStep,
            onProjectNameChange = onProjectNameChange,
            onProjectIdChange = onProjectIdChange
        )
    }
}

@Composable
fun StepTwoContent(
    stepTwoState: StepTwoState,
    isCurrentStep: Boolean,
    isCreatingFromScratch: Boolean,
    projects: LazyPagingItems<ProjectInfo>,
    saveSecondStep: (projectId: String?, name: String?) -> Unit,
    onProjectNameChange: (projectName: String) -> Unit,
    onProjectIdChange: (projectId: String) -> Unit
) {
    AnimatedVisibility(isCurrentStep) {
        if (isCreatingFromScratch) {
            StepTwoEnterDetails(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                stepTwoState,
                onProjectNameChange,
                onProjectIdChange
            )
        } else {
            StepTwoSelectProject(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                projects = projects,
                selectedProject = stepTwoState.projectId ?: "",
                saveSecondStep = saveSecondStep
            )
        }
    }
}

@Composable
fun StepTwoSelectProject(
    modifier: Modifier = Modifier,
    selectedProject: String,
    projects: LazyPagingItems<ProjectInfo>,
    saveSecondStep: (projectId: String?, name: String?) -> Unit
) {
    LazyColumn(modifier) {
        items(projects.itemCount) {
            projects[it]?.let { project ->
                ProjectListItem(
                    project = project,
                    isSelectedProject = project.project == selectedProject,
                    saveSecondStep = saveSecondStep
                )
            }
        }
    }
}

@Composable
fun ProjectListItem(
    project: ProjectInfo,
    isSelectedProject: Boolean,
    saveSecondStep: (projectId: String?, name: String?) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                saveSecondStep(
                    project.project,
                    project.displayName
                )
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = project.displayName ?: "undefined"
        )
        if (isSelectedProject) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}

@Composable
fun StepTwoEnterDetails(
    modifier: Modifier = Modifier,
    stepTwoState: StepTwoState,
    onProjectNameChange: (projectName: String) -> Unit,
    onProjectIdChange: (projectId: String) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = stepTwoState.projectName ?: "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onProjectNameChange,
            label = {
                Text(
                    text = stringResource(id = R.string.project_name_text_field),
                    style = TextStyle(color = Orange)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                focusedLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                errorPlaceholderColor = Color.Red
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = stepTwoState.projectId ?: "",
            onValueChange = onProjectIdChange,
            label = {
                Text(
                    text = stringResource(id = R.string.project_id_text_field),
                    style = TextStyle(color = Orange)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                focusedLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red
            )
        )
    }
}

@Preview
@Composable
fun StepTwoPreview() {
    KonsolTheme {
        StepTwo(
            stepTwoState = StepTwoState(),
            currentStep = STEP_TWO,
            isCreatingFromScratch = true,
            projects = flowOf(PagingData.empty<ProjectInfo>()).collectAsLazyPagingItems(),
            saveSecondStep = { projectId: String?, name: String? -> },
            onProjectNameChange = { projectName: String -> },
            onProjectIdChange = { projectId: String -> }
        )
    }
}
