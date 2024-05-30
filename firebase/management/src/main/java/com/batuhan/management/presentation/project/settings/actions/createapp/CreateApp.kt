package com.batuhan.management.presentation.project.settings.actions.createapp

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.management.R
import com.batuhan.theme.DarkGreen
import com.batuhan.theme.Orange

@Composable
fun CreateApp(appType: String, onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<CreateAppViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.createAppEvent.collect { event ->
            when (event) {
                is CreateAppEvent.Back -> {
                    onBackPressed.invoke()
                }
            }
        }
    }
    ActionScreenContent(
        appType = appType,
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        onSave = viewModel::onSave,
        saveContentForAndroid = { packageName: String, appName: String?, sha1String: String? ->
            viewModel.saveContentForAndroid(
                CreateAppInputState.Android(
                    packageName,
                    appName,
                    sha1String
                )
            )
        },
        saveContentForIos = { bundleId: String, appName: String?, appStoreId: String? ->
            viewModel.saveContentForIos(
                CreateAppInputState.Ios(
                    bundleId,
                    appName,
                    appStoreId
                )
            )
        },
        saveContentForWeb = { appName: String ->
            viewModel.saveContentForWeb(CreateAppInputState.Web(appName))
        },
        openSnackbar = viewModel::setSnackbarState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionScreenContent(
    appType: String,
    uiState: CreateAppUiState,
    onBackPressed: () -> Unit,
    onSave: () -> Unit,
    saveContentForAndroid: (packageName: String, appName: String?, sha1String: String?) -> Unit,
    saveContentForIos: (bundleId: String, appName: String?, appStoreId: String?) -> Unit,
    saveContentForWeb: (appName: String) -> Unit,
    openSnackbar: (Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val createAppInputState by remember(uiState.createAppInputState) {
        derivedStateOf { uiState.createAppInputState }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val isSuccessful by remember(uiState.isSuccessful) {
        derivedStateOf { uiState.isSuccessful }
    }
    LaunchedEffect(isSnackbarOpened) {
        errorState?.messageResId?.takeIf { isSnackbarOpened }?.let { resId ->
            val message = context.getString(resId)
            val actionText = errorState?.actionResId?.let { context.getString(it) }
            when (
                snackbarHostState.showSnackbar(
                    message,
                    actionText,
                    withDismissAction = actionText == null,
                    duration = SnackbarDuration.Indefinite
                )
            ) {
                SnackbarResult.Dismissed -> {
                    openSnackbar.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.ActionPerformed -> {
                    openSnackbar.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                    onSave.invoke()
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
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    actionColor = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.create_app_title))
                },
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
                    if (errorState != null) {
                        IconButton(
                            onClick = {
                                openSnackbar.invoke(true)
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
                            modifier = Modifier.padding(end = 16.dp).size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else if (isSuccessful) {
                        Icon(
                            modifier = Modifier.padding(end = 16.dp).size(24.dp),
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = DarkGreen
                        )
                    } else {
                        IconButton(onClick = onSave) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                    }
                }
            )
        }

    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CreateAppInput(
                createAppInputState = createAppInputState,
                appType = AppType.valueOf(appType),
                saveContentForAndroid = saveContentForAndroid,
                saveContentForIos = saveContentForIos,
                saveContentForWeb = saveContentForWeb
            )
        }
    }
}
