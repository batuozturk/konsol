package com.batuhan.cloudstorage.presentation.objectlist.createfolder

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.theme.Orange

@Composable
fun CreateFolderScreen(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<CreateFolderViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.createFolderEvent.collect { event ->
            when (event) {
                CreateFolderEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    CreateFolderScreenContent(
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        uploadFile = viewModel::uploadFile,
        setSnackbarState = viewModel::setSnackbarState,
        updateFolderName = viewModel::updateFolderName
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderScreenContent(
    uiState: CreateFolderUiState,
    onBackPressed: () -> Unit,
    uploadFile: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    updateFolderName: (String) -> Unit
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
    val folderName by remember(uiState.folderName) {
        derivedStateOf { uiState.folderName }
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
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
                },
                title = {
                    Text("Create folder")
                },
                actions = {
                    if (errorState != null) {
                        IconButton(onClick = { setSnackbarState.invoke(true) }) {
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
                        IconButton(onClick = uploadFile) {
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
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(it).padding(8.dp),
            value = folderName ?: "",
            onValueChange = updateFolderName,
            label = {
                Text("Folder Name")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(
                    handleColor = Orange,
                    backgroundColor = Orange.copy(alpha = 0.4f)
                )
            )
        )
    }
}
