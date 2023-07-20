package com.batuhan.management.presentation.project.settings.actions.projectdetail.selectlocation

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.management.R
import com.batuhan.management.data.model.AvailableLocation
import com.batuhan.theme.Orange

@Composable
fun SelectLocation(onBackPressed: () -> Unit) {
    val viewModel = hiltViewModel<SelectLocationViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val availableLocations = viewModel.availableLocations.collectAsLazyPagingItems()
    LaunchedEffect(key1 = true) {
        viewModel.selectLocationEvent.collect { event ->
            when (event) {
                SelectLocationEvent.Back -> onBackPressed.invoke()
            }
        }
    }
    SelectLocationContent(
        uiState = uiState,
        availableLocations = availableLocations,
        onLocationInfoSelected = viewModel::setSelectedLocationInfoId,
        onBackPressed = viewModel::onBackPressed,
        selectLocation = viewModel::setSelectedLocationId,
        setSnackbarState = viewModel::setSnackbarState,
        onSaveClick = viewModel::saveChanges,
        setErrorState = viewModel::setErrorState,
        retryOperation = viewModel::retryOperation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationContent(
    uiState: SelectLocationUiState,
    availableLocations: LazyPagingItems<AvailableLocation>,
    onLocationInfoSelected: (String?) -> Unit,
    selectLocation: (String) -> Unit,
    onBackPressed: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    setErrorState: (SelectLocationErrorState) -> Unit,
    retryOperation: (SelectLocationErrorState, (() -> Unit)?) -> Unit
) {
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val selectedLocationInfoId by remember(uiState.selectedLocationInfoId) {
        derivedStateOf { uiState.selectedLocationInfoId }
    }
    val selectedLocationId by remember(uiState.selectedLocationId) {
        derivedStateOf { uiState.selectedLocationId }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
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
                    retryOperation.invoke(errorState!!) {
                        availableLocations.refresh()
                    }
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
                    Text(stringResource(id = R.string.select_location))
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
                            modifier = Modifier.padding(end = 16.dp).size(24.dp),
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
        when (availableLocations.loadState.refresh) {
            is LoadState.Error -> {
                setErrorState.invoke(SelectLocationErrorState.AVAILABLE_LOCATIONS)
            }
            else -> {
            }
        }

        when (availableLocations.loadState.append) {
            is LoadState.Error -> {
                setErrorState.invoke(SelectLocationErrorState.AVAILABLE_LOCATIONS)
            }
            else -> {
            }
        }
        LazyColumn(
            modifier = Modifier.padding(it)
                .fillMaxWidth()
        ) {
            items(availableLocations.itemCount) {
                availableLocations[it]?.let { location ->
                    AvailableLocationItem(
                        selectedLocationInfoId == location.locationId,
                        availableLocation = location,
                        isSelected = selectedLocationId == location.locationId,
                        selectLocation = selectLocation,
                        setSelectedLocationInfoId = onLocationInfoSelected
                    )
                }
            }
        }
    }
}

@Composable
fun AvailableLocationItem(
    isInfoOpened: Boolean = false,
    availableLocation: AvailableLocation,
    isSelected: Boolean,
    selectLocation: (String) -> Unit,
    setSelectedLocationInfoId: (locationId: String?) -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp)
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
            IconButton(
                onClick = {
                    if (isInfoOpened) {
                        setSelectedLocationInfoId.invoke(null)
                    } else {
                        setSelectedLocationInfoId.invoke(availableLocation.locationId)
                    }
                },
                modifier = Modifier
                    .size(24.dp)
                    .weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Orange
                )
            }
        }
        if (isInfoOpened) {
            Spacer(modifier = Modifier.height(8.dp))
            LocationInfo(
                isMultiRegional = availableLocation.isMultiRegional(),
                isCloudFunctionsSupported = availableLocation.isCloudFunctionsSupported(),
                isFirestoreSupported = availableLocation.isFirestoreSupported(),
                isDefaultCloudStorageBucketSupported = availableLocation.isDefaultCloudStorageBucketSupported()
            )
        }
    }
}

@Composable
fun LocationInfo(
    isMultiRegional: Boolean,
    isCloudFunctionsSupported: Boolean,
    isFirestoreSupported: Boolean,
    isDefaultCloudStorageBucketSupported: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (isMultiRegional) {
            LocationInfoItem(
                imageVector = Icons.Default.Public,
                titleResId = R.string.multiregional_supported_title
            )
        }
        if (isCloudFunctionsSupported) {
            LocationInfoItem(
                imageVector = Icons.Default.Code,
                titleResId = R.string.cloud_functions_supported_title
            )
        }
        if (isFirestoreSupported) {
            LocationInfoItem(
                imageVector = Icons.Default.Storage,
                titleResId = R.string.firestore_supported_title
            )
        }
        if (isDefaultCloudStorageBucketSupported) {
            LocationInfoItem(
                imageVector = Icons.Default.PermMedia,
                titleResId = R.string.default_cloud_storage_bucket_supported_title
            )
        }
    }
}

@Composable
fun LocationInfoItem(imageVector: ImageVector, @StringRes titleResId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = Orange,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(id = titleResId),
            modifier = Modifier
                .weight(7f)
                .padding(start = 4.dp)
        )
    }
}
