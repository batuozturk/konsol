package com.batuhan.management.presentation.project.settings.actions.projectdetail

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.management.R
import com.batuhan.theme.Orange

// todo screen name can be changed
@Composable
fun ProjectDetail(
    onBackPressed: () -> Unit,
    navigateToSelectLocation: (String) -> Unit
) {
    val viewModel = hiltViewModel<ProjectDetailViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = true) {
        viewModel.updateProjectEvent.collect { event ->
            when (event) {
                is ProjectDetailEvent.Back -> onBackPressed.invoke()
                is ProjectDetailEvent.SaveChanges -> viewModel.updateProject()
                is ProjectDetailEvent.NavigateToSelectLocation -> navigateToSelectLocation.invoke(
                    event.projectId
                )
            }
        }
    }
    DisposableEffect(lifecycleOwner){
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_RESUME){
                viewModel.getProject()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    ProjectDetailContent(
        uiState = uiState,
        onBackPressed = viewModel::onBackPressed,
        onSaveClick = viewModel::onSaveClick,
        updateDisplayName = viewModel::updateDisplayName,
        setEditing = viewModel::setEditing,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation,
        navigateToSelectLocation = viewModel::navigateToSelectLocation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailContent(
    uiState: ProjectDetailUiState,
    onBackPressed: () -> Unit,
    onSaveClick: () -> Unit,
    updateDisplayName: (String) -> Unit,
    setEditing: (Boolean) -> Unit,
    retryOperation: (ProjectDetailErrorState) -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    navigateToSelectLocation: () -> Unit
) {
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
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
    val isDisplayNameEditing by remember(uiState.isEditing) {
        derivedStateOf { uiState.isEditing }
    }
    val context = LocalContext.current
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let {
            val title = context.getString(it)
            val actionTitle = errorState?.actionResId?.let { resId -> context.getString(resId) }
            when (
                snackbarHostState.showSnackbar(
                    title,
                    withDismissAction = actionTitle == null,
                    actionLabel = actionTitle,
                    duration = SnackbarDuration.Indefinite
                )
            ) {
                SnackbarResult.ActionPerformed -> {
                    setSnackbarState.invoke(false)
                    retryOperation.invoke(errorState!!)
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
                    Text(stringResource(id = R.string.update_project))
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
                        IconButton(onClick = onSaveClick) {
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
        LazyColumn(modifier = Modifier.padding(it)) {
            items(uiState.projectAttributes?.size ?: 0) { index ->
                uiState.projectAttributes?.get(index)?.let { attribute ->
                    ProjectDetailItem(
                        editableDisplayName = uiState.editableDisplayName,
                        projectDetail = attribute,
                        updateDisplayName = updateDisplayName,
                        isEditing = isDisplayNameEditing,
                        setEditing = setEditing,
                        navigateToSelectLocation = navigateToSelectLocation
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectDetailItem(
    editableDisplayName: String?,
    projectDetail: Pair<Int, String>,
    updateDisplayName: (String) -> Unit,
    setEditing: (Boolean) -> Unit,
    isEditing: Boolean,
    navigateToSelectLocation: () -> Unit
) {
    if (projectDetail.first == R.string.project_display_name && isEditing) {
        EditableDisplayName(
            label = projectDetail.first,
            editableDisplayName = editableDisplayName,
            updateDisplayName = updateDisplayName,
            setEditing = setEditing
        )
    } else {
        DetailItem(projectDetail, setEditing, navigateToSelectLocation)
    }
}

@Composable
fun EditableDisplayName(
    @StringRes label: Int,
    editableDisplayName: String?,
    updateDisplayName: (String) -> Unit,
    setEditing: (Boolean) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        value = editableDisplayName ?: "",
        label = {
            Text(text = stringResource(id = label))
        },
        onValueChange = {
            updateDisplayName(it)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Orange,
            focusedLabelColor = Orange,
            unfocusedBorderColor = Orange,
            unfocusedLabelColor = Orange,
            cursorColor = Orange,
            selectionColors = TextSelectionColors(
                handleColor = Orange,
                backgroundColor = Orange.copy(alpha = 0.4f)
            )
        ),
        trailingIcon = {
            IconButton(onClick = { setEditing(false) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Orange
                )
            }
        }
    )
}

@Composable
fun DetailItem(
    projectDetail: Pair<Int, String>,
    setEditing: (Boolean) -> Unit,
    navigateToSelectLocation: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = stringResource(id = projectDetail.first))
            Text(text = projectDetail.second)
        }
        if (projectDetail.first == R.string.project_display_name) {
            IconButton(
                onClick = {
                    setEditing(true)
                }
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Orange)
            }
        } else if (projectDetail.first == R.string.project_location_id &&
            projectDetail.second == stringResource(id = R.string.undefined).lowercase()
        ) {
            IconButton(
                onClick = {
                    navigateToSelectLocation.invoke()
                }
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Orange)
            }
        }
    }
}
