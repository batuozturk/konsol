package com.batuhan.firestore.presentation.database.createitem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Save
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
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.firestore.presentation.database.document.DocumentFieldBottomSheet
import com.batuhan.firestore.util.createDocumentField
import com.batuhan.firestore.presentation.database.document.fields.bytype.DocumentFieldItemByType
import com.batuhan.theme.Orange

@Composable
fun CreateItemScreen(onBackPressed: (Boolean) -> Unit) {
    val viewModel = hiltViewModel<CreateItemViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.createItemEvent.collect { event ->
            when (event) {
                is CreateItemEvent.Back -> onBackPressed.invoke(event.needsRefresh)
            }
        }
    }
    CreateItemContent(
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        editDocumentField = viewModel::editDocumentField,
        removeDocumentField = viewModel::removeDocumentFieldList,
        setSnackbarState = viewModel::setSnackbarState,
        createDocument = viewModel::createDocument,
        setBottomSheetState = viewModel::setBottomSheetState,
        updateCollectionId = viewModel::updateCollectionId,
        updateDocumentId = viewModel::updateDocumentId,
        retryOperation = viewModel::retryOperation,
        onSave = viewModel::onSave
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemContent(
    uiState: CreateItemUiState,
    onBackPressed: () -> Unit,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    removeDocumentField: (Int?, Int?) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    createDocument: () -> Unit,
    setBottomSheetState: (Boolean) -> Unit,
    updateCollectionId: (String) -> Unit,
    updateDocumentId: (String) -> Unit,
    retryOperation: (CreateItemErrorState) -> Unit,
    onSave: () -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val documentFieldList by remember(uiState.valuesList) {
        derivedStateOf { uiState.valuesList }
    }
    val editableField by remember(uiState.currentEditedField) {
        derivedStateOf { uiState.currentEditedField }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false }
    )
    val isBottomSheetOpened by remember(uiState.isBottomSheetOpened) {
        derivedStateOf { uiState.isBottomSheetOpened }
    }
    val currentEditedFieldParentIndex by remember(uiState.currentEditedFieldParentIndex) {
        derivedStateOf { uiState.currentEditedFieldParentIndex }
    }
    val currentEditedFieldIndex by remember(uiState.currentEditedFieldIndex) {
        derivedStateOf { uiState.currentEditedFieldIndex }
    }
    val collectionId by remember(uiState.collectionId) {
        derivedStateOf { uiState.collectionId }
    }
    val documentId by remember(uiState.documentId) {
        derivedStateOf { uiState.documentId }
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
                    retryOperation.invoke(errorState!!)
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
                    Text(
                        stringResource(
                            if (uiState.createCollection) R.string.create_collection_title
                            else R.string.create_document_title
                        )
                    )
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
                        IconButton(
                            onClick = {
                                editDocumentField.invoke(
                                    createDocumentField(DocumentField.Type.STRING),
                                    documentFieldList.size,
                                    null
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = Orange
                            )
                        }
                        IconButton(
                            onClick = createDocument
                        ) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (uiState.createCollection) {
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        value = collectionId ?: "",
                        onValueChange = updateCollectionId,
                        label = {
                            Text(stringResource(R.string.collection_id))
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
            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    value = documentId ?: "",
                    onValueChange = updateDocumentId,
                    label = {
                        Text(stringResource(id = R.string.document_id))
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
            itemsIndexed(documentFieldList) { index, item ->
                DocumentFieldItemByType(
                    field = item,
                    fieldIndex = index,
                    editDocumentField = editDocumentField,
                    removeDocumentField = removeDocumentField,
                    setEditingState = { } // no-op, it will be false in this screen
                )
            }
        }
    }
    if (isBottomSheetOpened) {
        DocumentFieldBottomSheet(
            coroutineScope = coroutineScope,
            field = editableField,
            parentDocumentFieldIndex = currentEditedFieldParentIndex,
            fieldIndex = currentEditedFieldIndex,
            bottomSheetState = bottomSheetState,
            setBottomSheetState = setBottomSheetState,
            editDocumentField = editDocumentField,
            onSave = onSave
        )
    }
}
