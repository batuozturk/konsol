package com.batuhan.testlab.presentation.creatematrix

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.batuhan.testlab.R
import com.batuhan.testlab.data.model.matrix.AndroidDevice
import com.batuhan.testlab.presentation.creatematrix.CreateMatrixViewModel.Companion.STEP_CREATE_MATRIX
import com.batuhan.testlab.presentation.creatematrix.CreateMatrixViewModel.Companion.STEP_ONE
import com.batuhan.testlab.presentation.creatematrix.CreateMatrixViewModel.Companion.STEP_THREE
import com.batuhan.testlab.presentation.creatematrix.CreateMatrixViewModel.Companion.STEP_TWO
import com.batuhan.testlab.presentation.creatematrix.steps.*
import com.batuhan.theme.Orange
import kotlinx.coroutines.launch

@Composable
fun CreateMatrixScreen(
    gcsPath: String?,
    onBackPressed: () -> Unit,
    navigateToSelectFileScreen: (String) -> Unit,
    clearGcsPath: () -> Unit
) {
    val viewModel = hiltViewModel<CreateMatrixViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    gcsPath?.let {
        val stepTwoState = if (uiState.stepTwoState.isTestAppEditing) {
            uiState.stepTwoState.copy(testGcsPath = it)
        } else {
            uiState.stepTwoState.copy(gcsPath = it)
        }
        viewModel.updateStepTwo(stepTwoState)
        clearGcsPath.invoke()
    }
    val activityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            viewModel.uploadFile(
                context.contentResolver,
                it ?: return@rememberLauncherForActivityResult
            )
        }
    )
    LaunchedEffect(true) {
        viewModel.createMatrixEvent.collect { event ->
            when (event) {
                CreateMatrixEvent.Back -> onBackPressed.invoke()
                is CreateMatrixEvent.SelectFile -> navigateToSelectFileScreen.invoke(
                    event.bucketName
                )
            }
        }
    }
    CreateMatrixContent(
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        updateStep = viewModel::updateStep,
        setSnackbarState = viewModel::setSnackbarState,
        createMatrix = viewModel::createMatrix,
        retryOperation = viewModel::retryOperation,
        updateStepOne = viewModel::updateStepOne,
        clearErrorState = viewModel::clearErrorState,
        browseFilesOnCloudStorage = viewModel::browseFilesOnCloudStorage,
        uploadFile = {
            viewModel.setTestAppIsEditing(it)
            activityResult.launch(
                arrayOf(
                    "application/vnd.android.package-archive",
                    "application/octet-stream"
                )
            )
        },
        updateDeviceFilter = viewModel::updateDeviceFilter,
        updateLocaleFilter = viewModel::updateLocaleFilter,
        clearApp = viewModel::clearApp,
        removeDevice = viewModel::removeDevice,
        setBottomSheetState = viewModel::setBottomSheetState,
        addAndroidDevice = viewModel::addDevice,
        updateSelectedItem = viewModel::updateSelectedItem
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateMatrixContent(
    uiState: CreateMatrixUiState,
    onBackPressed: () -> Unit,
    updateStep: (Int) -> Boolean,
    setSnackbarState: (Boolean) -> Unit,
    createMatrix: () -> Unit,
    retryOperation: (CreateMatrixErrorState) -> Unit,
    updateStepOne: (StepOneState) -> Unit,
    clearErrorState: () -> Unit,
    browseFilesOnCloudStorage: (Boolean) -> Unit,
    uploadFile: (Boolean) -> Unit,
    updateDeviceFilter: (String) -> Unit,
    updateLocaleFilter: (String) -> Unit,
    clearApp: (Boolean) -> Unit,
    removeDevice: (Int) -> Unit,
    setBottomSheetState: (Boolean) -> Unit,
    addAndroidDevice: (AndroidDevice) -> Unit,
    updateSelectedItem: (AndroidDevice?) -> Unit
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
    val androidDeviceList by remember(uiState.filteredAndroidDeviceList) {
        derivedStateOf { uiState.filteredAndroidDeviceList }
    }
    val isBottomSheetOpened by remember(uiState.isBottomSheetOpened) {
        derivedStateOf { uiState.isBottomSheetOpened }
    }
    val locales by remember(uiState.filteredLocales) {
        derivedStateOf { uiState.filteredLocales }
    }
    val orientations by remember(uiState.orientations) {
        derivedStateOf { uiState.orientations }
    }
    val deviceFilterInput by remember(uiState.deviceFilterInput) {
        derivedStateOf { uiState.deviceFilterInput }
    }
    val localeFilterInput by remember(uiState.localeFilterInput) {
        derivedStateOf { uiState.localeFilterInput }
    }
    val selectedModel by remember(stepThreeState.selectedModel) {
        derivedStateOf { stepThreeState.selectedModel }
    }
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { false })
    val pagerState = rememberPagerState { STEP_CREATE_MATRIX }
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
                    Text(stringResource(R.string.create_matrix))
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
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else {
                        if (currentStep > STEP_TWO) {
                            IconButton(createMatrix) {
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
                    StepTwo(
                        testType = stepOneState.testType,
                        stepTwoState = stepTwoState,
                        browseFilesOnCloudStorage = browseFilesOnCloudStorage,
                        uploadFile = uploadFile,
                        clearApp = clearApp
                    )
                }
                STEP_THREE -> {
                    StepThreeList(
                        deviceList = stepThreeState.androidDeviceList ?: listOf(),
                        addDevice = {
                            setBottomSheetState.invoke(true)
                        },
                        removeDevice = removeDevice
                    )
                }
            }
        }
    }
    if (isBottomSheetOpened) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    setBottomSheetState.invoke(false)
                }
            },
            dragHandle = {
            }
        ) {
            StepThreeBottomSheet(
                onDismiss = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        setBottomSheetState.invoke(false)
                    }
                },
                selectedModel = selectedModel,
                androidDeviceList = androidDeviceList,
                locales = locales,
                orientations = orientations,
                deviceFilterInput = deviceFilterInput,
                localeFilterInput = localeFilterInput,
                addAndroidDevice = addAndroidDevice,
                updateDeviceFilter = updateDeviceFilter,
                updateLocaleFilter = updateLocaleFilter,
                updateSelectedItem = updateSelectedItem
            )
        }
    }
}
