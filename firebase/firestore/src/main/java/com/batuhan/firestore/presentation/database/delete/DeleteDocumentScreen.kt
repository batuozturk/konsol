package com.batuhan.firestore.presentation.database.delete

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.firestore.R
import com.batuhan.theme.Orange

@Composable
fun DeleteDocumentScreen(onBackPressed: (Boolean) -> Unit) {
    val viewModel = hiltViewModel<DeleteDocumentViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.deleteDocumentEvent.collect { event ->
            when (event) {
                is DeleteDocumentEvent.Back -> onBackPressed.invoke(event.isSuccess)
            }
        }
    }
    DeleteDocumentContent(
        uiState = uiState,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation,
        onBackPressed = viewModel::onBackPressed,
        updateDocumentName = viewModel::updateDocumentName,
        deleteDocument = viewModel::deleteDocument
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDocumentContent(
    uiState: DeleteDocumentUiState,
    setSnackbarState: (Boolean) -> Unit,
    retryOperation: (DeleteDocumentErrorState) -> Unit,
    onBackPressed: () -> Unit,
    updateDocumentName: (String) -> Unit,
    deleteDocument: () -> Unit
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
    val documentName by remember(uiState.documentName) {
        derivedStateOf { uiState.documentName }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
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
                SnackbarResult.Dismissed -> {
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.ActionPerformed -> {
                    retryOperation(errorState!!)
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.delete_document_title)) },
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
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else {
                        IconButton(onClick = deleteDocument) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                    }
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
        Column(modifier = Modifier.fillMaxSize().padding(it).padding(8.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = documentName ?: "",
                onValueChange = updateDocumentName,
                label = {
                    Text(stringResource(id = R.string.document_name))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Orange,
                    focusedLabelColor = Orange,
                    unfocusedBorderColor = Orange,
                    unfocusedLabelColor = Orange,
                    cursorColor = Orange,
                    selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
                )
            )
        }
    }
}
