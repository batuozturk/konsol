package com.batuhan.cloudstorage.presentation.bucket

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.cloudstorage.R
import com.batuhan.theme.Orange

@Composable
fun BucketScreen(
    onBackPressed: () -> Unit,
    navigateToCreateBucketScreen: (String) -> Unit,
    navigateToObjectListScreen: (String) -> Unit
) {
    val viewModel = hiltViewModel<BucketViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(true) {
        viewModel.bucketEvent.collect { event ->
            when (event) {
                BucketEvent.Back -> onBackPressed.invoke()
                is BucketEvent.CreateBucket -> navigateToCreateBucketScreen.invoke(event.projectId)
                is BucketEvent.ObjectList -> navigateToObjectListScreen.invoke(event.bucketName)
            }
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getDefaultBucket()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    BucketScreenContent(
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        createDefaultBucket = viewModel::createDefaultBucket,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation,
        navigateToObjectListScreen = viewModel::navigateToObjectListScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BucketScreenContent(
    uiState: BucketUiState,
    onBackPressed: () -> Unit,
    createDefaultBucket: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    retryOperation: (BucketErrorState) -> Unit,
    navigateToObjectListScreen: () -> Unit
) {
    val defaultBucket by remember(uiState.defaultBucket) {
        derivedStateOf { uiState.defaultBucket }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val context = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
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
                SnackbarResult.Dismissed -> {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                },
                title = {
                    Text("Bucket screen")
                },
                actions = {
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
                    } else if (defaultBucket == null) {
                        IconButton(
                            onClick = {
                                createDefaultBucket.invoke()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                    }
                }
            )
        }
    ) {
        defaultBucket?.let { defaultBucket ->
            Column(
                modifier = Modifier.fillMaxWidth().padding(it).padding(8.dp)
                    .clickable { navigateToObjectListScreen.invoke() }
                    .border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp)
            ) {
                Text(defaultBucket.bucket?.name ?: stringResource(id = R.string.undefined))
                Text(defaultBucket.location ?: stringResource(id = R.string.undefined_location))
            }
        }
    }
}
