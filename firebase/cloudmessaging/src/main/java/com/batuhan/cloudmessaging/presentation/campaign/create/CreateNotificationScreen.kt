package com.batuhan.cloudmessaging.presentation.campaign.create

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.cloudmessaging.R
import com.batuhan.cloudmessaging.presentation.campaign.create.CreateNotificationViewModel.Companion.STEP_FOUR
import com.batuhan.cloudmessaging.presentation.campaign.create.CreateNotificationViewModel.Companion.STEP_ONE
import com.batuhan.cloudmessaging.presentation.campaign.create.CreateNotificationViewModel.Companion.STEP_SEND
import com.batuhan.cloudmessaging.presentation.campaign.create.CreateNotificationViewModel.Companion.STEP_THREE
import com.batuhan.cloudmessaging.presentation.campaign.create.CreateNotificationViewModel.Companion.STEP_TWO
import com.batuhan.cloudmessaging.presentation.campaign.create.steps.StepOne
import com.batuhan.cloudmessaging.presentation.campaign.create.steps.StepPreview
import com.batuhan.cloudmessaging.presentation.campaign.create.steps.StepThree
import com.batuhan.cloudmessaging.presentation.campaign.create.steps.StepTwo
import com.batuhan.theme.Orange
import kotlinx.coroutines.launch

@Composable
fun CreateNotificationScreen(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<CreateNotificationViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.createCampaignEvent.collect { event ->
            when (event) {
                CreateNotificationEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    CreateNotificationContent(
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        updateStep = viewModel::updateStep,
        setSnackbarState = viewModel::setSnackbarState,
        createCampaign = viewModel::sendMessage,
        retryOperation = viewModel::retryOperation,
        updateStepOne = viewModel::updateStepOne,
        updateStepTwo = viewModel::updateStepTwo,
        updateStepThree = viewModel::updateStepThree,
        clearErrorState = viewModel::clearErrorState
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateNotificationContent(
    uiState: CreateNotificationUiState,
    onBackPressed: () -> Unit,
    updateStep: (Int) -> Boolean,
    setSnackbarState: (Boolean) -> Unit,
    createCampaign: () -> Unit,
    retryOperation: (CreateNotificationErrorState) -> Unit,
    updateStepOne: (StepOneState) -> Unit,
    updateStepTwo: (StepTwoState) -> Unit,
    updateStepThree: (StepThreeState) -> Unit,
    clearErrorState: () -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val context = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val currentStep by remember(uiState.currentStep) {
        derivedStateOf { uiState.currentStep }
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
    val pagerState = rememberPagerState { STEP_SEND }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.let {
            val title = context.getString(it)
            val actionTitle = errorState?.actionResId?.let { resId -> context.getString(resId) }
            val result = snackbarHostState.showSnackbar(
                message = title,
                actionLabel = actionTitle,
                withDismissAction = actionTitle == null,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    setSnackbarState.invoke(false)
                    retryOperation.invoke(errorState!!)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.Dismissed -> {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    shape = RoundedCornerShape(10.dp),
                    snackbarData = it,
                    actionColor = Color.White,
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                },
                title = {
                    Text(stringResource(R.string.create_notification))
                },
                actions = {
                    if (currentStep > 0) {
                        IconButton(
                            onClick = {
                                updateStep.invoke(currentStep - 1)
                                coroutineScope.launch {
                                    pagerState.scrollToPage(currentStep - 1)
                                }
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
                    Text("${currentStep + 1}/4")
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
                        if (currentStep > STEP_THREE) {
                            IconButton(createCampaign) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                val result = updateStep.invoke(currentStep + 1)
                                if (result) {
                                    coroutineScope.launch {
                                        pagerState.scrollToPage(currentStep + 1)
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.NavigateNext,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        }
                    }
                }
            )
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(it),
            userScrollEnabled = false
        ) { currentPage ->
            when (currentPage) {
                STEP_ONE -> {
                    StepOne(stepOneState = stepOneState, updateStepOne = updateStepOne)
                }
                STEP_TWO -> {
                    StepTwo(stepTwoState = stepTwoState, updateStepTwo = updateStepTwo)
                }
                STEP_THREE -> {
                    StepThree(stepThreeState = stepThreeState, updateStepThree = updateStepThree)
                }
                STEP_FOUR -> {
                    StepPreview(
                        stepOneState = stepOneState,
                        stepTwoState = stepTwoState,
                        stepThreeState = stepThreeState
                    )
                }
            }
        }
    }
}
