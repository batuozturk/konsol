package com.batuhan.firestore.presentation.createdatabase

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.FirestoreLocation
import com.batuhan.firestore.presentation.createdatabase.CreateDatabaseViewModel.Companion.STEP_ONE
import com.batuhan.firestore.presentation.createdatabase.CreateDatabaseViewModel.Companion.STEP_PREVIEW
import com.batuhan.firestore.presentation.createdatabase.CreateDatabaseViewModel.Companion.STEP_TWO
import com.batuhan.firestore.presentation.createdatabase.steps.StepOne
import com.batuhan.firestore.presentation.createdatabase.steps.StepPreview
import com.batuhan.firestore.presentation.createdatabase.steps.StepTwo
import com.batuhan.theme.Orange
import kotlinx.coroutines.launch

@Composable
fun CreateDatabaseScreen(onBackPressed: () -> Unit, selectLocation: (String, String?) -> Unit) {
    val viewModel = hiltViewModel<CreateDatabaseViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locations = viewModel.locations.collectAsLazyPagingItems()
    LaunchedEffect(true) {
        viewModel.createDatabaseEvent.collect { event ->
            when (event) {
                is CreateDatabaseEvent.Back -> onBackPressed.invoke()
                is CreateDatabaseEvent.SelectLocation -> selectLocation.invoke(
                    event.projectId,
                    event.locationId
                )
            }
        }
    }
    CreateDatabaseContent(
        uiState = uiState,
        locations = locations,
        onBackPressed = viewModel::onBackPressed,
        createDatabase = viewModel::createDatabaseOperation,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation,
        updateDatabaseName = viewModel::updateDatabaseName,
        setLocationId = viewModel::selectLocation,
        setErrorState = viewModel::setErrorState,
        updateStep = viewModel::updateStep
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateDatabaseContent(
    uiState: CreateDatabaseUiState,
    locations: LazyPagingItems<FirestoreLocation>,
    onBackPressed: () -> Unit,
    createDatabase: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    retryOperation: (CreateDatabaseErrorState, (() -> Unit)?) -> Unit,
    updateDatabaseName: (String) -> Unit,
    setLocationId: (String) -> Unit,
    setErrorState: (CreateDatabaseErrorState) -> Unit,
    updateStep: (Int) -> Boolean
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
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val stepOneState by remember(uiState.stepOneState) {
        derivedStateOf { uiState.stepOneState }
    }
    val stepTwoState by remember(uiState.stepTwoState) {
        derivedStateOf { uiState.stepTwoState }
    }
    val currentStep by remember(uiState.currentStep) {
        derivedStateOf { uiState.currentStep }
    }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(STEP_ONE) { STEP_PREVIEW + 1 }
    val context = LocalContext.current

    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let {
            val title = context.getString(it)
            val actionText = errorState?.actionResId?.let { resId -> context.getString(resId) }
            val result = snackbarHostState.showSnackbar(
                message = title,
                actionLabel = actionText,
                withDismissAction = actionText == null,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.Dismissed -> {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.ActionPerformed -> {
                    retryOperation(errorState!!) {
                        locations.refresh()
                    }
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
                    snackbarData = it,
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    actionColor = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
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
                title = {
                    Text(stringResource(R.string.create_database_title))
                },
                actions = {
                    if (currentStep > 0) {
                        IconButton(
                            onClick = {
                                val result = updateStep.invoke(currentStep - 1)
                                if (result) {
                                    coroutineScope.launch {
                                        pagerState.scrollToPage(currentStep - 1)
                                    }
                                }
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
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else {
                        if (currentStep > STEP_TWO) {
                            IconButton(
                                onClick = {
                                    createDatabase.invoke()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = Orange
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    val result = updateStep.invoke(currentStep + 1)
                                    if (result) {
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(currentStep + 1)
                                        }
                                    }
                                }
                            ) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) { page ->
            when (page) {
                STEP_ONE -> {
                    StepOne(
                        stepOneState = stepOneState,
                        updateDatabaseName = updateDatabaseName
                    )
                }
                STEP_TWO -> {
                    StepTwo(
                        stepTwoState = stepTwoState,
                        locations = locations,
                        setLocationId = setLocationId,
                        setErrorState = setErrorState
                    )
                }
                STEP_PREVIEW -> {
                    StepPreview(
                        stepOneState = stepOneState,
                        stepTwoState = stepTwoState
                    )
                }
            }
        }
    }
}
