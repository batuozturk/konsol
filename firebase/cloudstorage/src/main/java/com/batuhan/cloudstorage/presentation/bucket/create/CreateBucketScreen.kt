package com.batuhan.cloudstorage.presentation.bucket.create

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.cloudstorage.R
import com.batuhan.core.data.model.management.AvailableLocation
import com.batuhan.theme.Orange

@Composable
fun CreateBucketScreen(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<CreateBucketViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locations = viewModel.locations.collectAsLazyPagingItems()
    LaunchedEffect(true) {
        viewModel.createBucketEvent.collect { event ->
            when (event) {
                CreateBucketEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    CreateBucketScreenContent(
        uiState = uiState,
        locations = locations,
        onBackPressed = viewModel::onBackPressed,
        createDefaultBucket = viewModel::createDefaultCreateBucket,
        setSnackbarState = viewModel::setSnackbarState,
        retryOperation = viewModel::retryOperation,
        updateBucketName = viewModel::updateBucketName,
        selectLocation = viewModel::updateLocation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBucketScreenContent(
    uiState: CreateBucketUiState,
    locations: LazyPagingItems<AvailableLocation>,
    onBackPressed: () -> Unit,
    createDefaultBucket: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    retryOperation: (CreateBucketErrorState) -> Unit,
    updateBucketName: (String) -> Unit,
    selectLocation: (String) -> Unit
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
    val bucketName by remember(uiState.bucketName) {
        derivedStateOf { uiState.bucketName }
    }
    val selectedLocationId by remember(uiState.selectedLocationId) {
        derivedStateOf { uiState.selectedLocationId }
    }
    val firestoreLocationSelected by remember(uiState.isFirestoreLocationSelected) {
        derivedStateOf { uiState.isFirestoreLocationSelected }
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
                    Text("Create default bucket")
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
                                .padding(horizontal = 12.dp)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = Orange,
                            trackColor = Color.White
                        )
                    } else {
                        IconButton(
                            onClick = {
                                createDefaultBucket.invoke()
                            }
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
        ) {
            if (firestoreLocationSelected) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(
                            2.dp,
                            Orange,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp),
                    text = stringResource(R.string.bucket_location_info, selectedLocationId ?: "")
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = bucketName ?: "",
                onValueChange = updateBucketName,
                label = {
                    Text(text = stringResource(id = R.string.bucket_name))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Orange,
                    unfocusedLabelColor = Orange,
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Orange,
                    cursorColor = Orange,
                    selectionColors = TextSelectionColors(
                        handleColor = Orange,
                        backgroundColor = Orange.copy(alpha = 0.4f)
                    )
                )
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        stringResource(id = R.string.select_location),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
                items(locations.itemCount) { index ->
                    locations.get(index)?.let { location ->
                        AvailableLocationItem(
                            availableLocation = location,
                            isSelected = selectedLocationId == location.locationId,
                            selectLocation = {
                                if (!firestoreLocationSelected) {
                                    selectLocation.invoke(it)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableLocationItem(
    availableLocation: AvailableLocation,
    isSelected: Boolean,
    selectLocation: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                selectLocation(availableLocation.locationId ?: return@clickable)
            }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = availableLocation.locationId ?: stringResource(id = R.string.undefined),
                modifier = Modifier.weight(8f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
