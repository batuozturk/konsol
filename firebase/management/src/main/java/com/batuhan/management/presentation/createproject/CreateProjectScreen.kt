package com.batuhan.management.presentation.createproject

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.management.R
import com.batuhan.management.data.model.AnalyticsAccount
import com.batuhan.management.data.model.ProjectInfo
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_ONE
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_THREE
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_TWO
import com.batuhan.management.presentation.createproject.steps.StepOne
import com.batuhan.management.presentation.createproject.steps.StepThree
import com.batuhan.management.presentation.createproject.steps.StepTwo
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange
import kotlinx.coroutines.flow.flowOf

@Composable
fun CreateProjectScreen(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<CreateProjectViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val availableProjects = viewModel.availableProjects.collectAsLazyPagingItems()
    val analyticsAccounts by viewModel.analyticsAccounts.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.createProjectEvent.collect { event ->
            when (event) {
                CreateProjectEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    CreateProjectScreenContent(
        uiState = uiState,
        availableProjects = availableProjects,
        analyticsAccounts = analyticsAccounts,
        onBackPressed = viewModel::onBackPressed,
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
        saveProjectToFirebase = viewModel::saveProject,
        setSnackbarState = viewModel::setSnackbarState,
        clearErrorState = viewModel::clearErrorState
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreenContent(
    uiState: CreateProjectUiState,
    availableProjects: LazyPagingItems<ProjectInfo>,
    analyticsAccounts: List<AnalyticsAccount>,
    onBackPressed: () -> Unit,
    updateStep: (step: Int) -> Boolean,
    retryOperation: (CreateProjectErrorState) -> Unit,
    saveFirstStep: (isCreatingFromScratch: Boolean) -> Unit,
    onProjectNameChange: (projectName: String) -> Unit,
    onProjectIdChange: (projectId: String) -> Unit,
    saveSecondStep: (projectId: String?, name: String?) -> Unit,
    onAnalyticsEnabled: (Boolean) -> Unit,
    saveThirdStep: (analyticsAccountId: String) -> Unit,
    saveProjectToFirebase: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    clearErrorState: () -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val currentStep by remember(uiState.currentStep) {
        derivedStateOf { uiState.currentStep }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val stepOneState by remember(uiState.stepOneState) {
        derivedStateOf { uiState.stepOneState }
    }
    val stepTwoState by remember(uiState.stepTwoState) {
        derivedStateOf { uiState.stepTwoState }
    }
    val stepThreeState by remember(uiState.stepThreeState) {
        derivedStateOf { uiState.stepThreeState }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val pagerState = rememberPagerState { 3 }
    val context = LocalContext.current
    LaunchedEffect(currentStep) {
        pagerState.scrollToPage(currentStep)
    }
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let {
            val titleText = context.getString(it)
            val actionText = errorState?.actionResId?.let { resId -> context.getString(resId) }
            val result = snackbarHostState.showSnackbar(
                message = titleText,
                actionLabel = actionText,
                withDismissAction = actionText == null,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    retryOperation(errorState!!)
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                else -> {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                },
                actions = {
                    if (currentStep > 0) {
                        IconButton(
                            onClick = {
                                updateStep.invoke(currentStep - 1)
                                clearErrorState.invoke()
                                setSnackbarState.invoke(false)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NavigateBefore,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                    }
                    Text("${currentStep + 1}/3")
                    if (errorState != null) {
                        IconButton(
                            onClick = {
                                setSnackbarState.invoke(true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    } else if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(horizontal = 12.dp).size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else {
                        if (currentStep > STEP_TWO) {
                            IconButton(saveProjectToFirebase) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                updateStep.invoke(currentStep + 1)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.NavigateNext,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        }
                    }
                },
                title = {
                    Text(stringResource(R.string.create_project))
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    contentColor = Color.White,
                    containerColor = Color.Red,
                    actionColor = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize().padding(it),
            state = pagerState
        ) { page ->
            when (page) {
                STEP_ONE -> {
                    StepOne(
                        stepOneState = stepOneState,
                        saveFirstStep = saveFirstStep
                    )
                }
                STEP_TWO -> {
                    StepTwo(
                        stepTwoState = stepTwoState,
                        errorState = errorState,
                        isCreatingFromScratch = stepOneState.isCreatingFromScratch ?: false,
                        projects = availableProjects,
                        saveSecondStep = saveSecondStep,
                        onProjectNameChange = onProjectNameChange,
                        onProjectIdChange = onProjectIdChange
                    )
                }
                STEP_THREE -> {
                    StepThree(
                        stepThreeState = stepThreeState,
                        analyticsAccounts = analyticsAccounts,
                        selectAnalyticsAccount = saveThirdStep,
                        saveThirdStep = onAnalyticsEnabled
                    )
                }
            }
        }
    }
}

@Composable
fun StepTitle(
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title
        )
    }
}

@Preview
@Composable
fun CreateProjectScreenPreview() {
    KonsolTheme {
        CreateProjectScreenContent(
            onBackPressed = { /*TODO*/ },
            availableProjects = flowOf(PagingData.empty<ProjectInfo>()).collectAsLazyPagingItems(),
            analyticsAccounts = listOf(),
            uiState = CreateProjectUiState(),
            onAnalyticsEnabled = { _ -> },
            saveThirdStep = { _ -> },
            saveFirstStep = { _ -> },
            saveSecondStep = { _, _ -> },
            saveProjectToFirebase = {},
            onProjectNameChange = {},
            onProjectIdChange = {},
            updateStep = { false },
            retryOperation = {},
            setSnackbarState = {},
            clearErrorState = {}
        )
    }
}
