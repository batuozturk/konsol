package com.batuhan.management.presentation.createproject.steps

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.batuhan.management.R
import com.batuhan.management.data.model.AvailableLocation
import com.batuhan.management.presentation.createproject.StepFourState
import com.batuhan.theme.Orange

@Composable
fun StepFour(
    stepFourState: StepFourState,
    currentStep: Int,
    availableLocations: LazyPagingItems<AvailableLocation>,
    saveStepFour: (locationId: String) -> Unit
) {
    val isCurrentStep = currentStep == STEP_FOUR
    val isPassedStep = currentStep > STEP_FOUR
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp)
    ) {
        StepTitle(
            title = stringResource(id = R.string.step_four_title),
            isPassedStep = isPassedStep
        )
        StepFourContent(
            stepFourState = stepFourState,
            isCurrentStep = isCurrentStep,
            availableLocations = availableLocations,
            saveStepFour = saveStepFour
        )
    }
}

@Composable
fun StepFourContent(
    stepFourState: StepFourState,
    isCurrentStep: Boolean,
    availableLocations: LazyPagingItems<AvailableLocation>,
    saveStepFour: (locationId: String) -> Unit
) {
    var selectedInfoId: String? by remember {
        mutableStateOf(null)
    }
    AnimatedVisibility(isCurrentStep) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(availableLocations.itemCount) {
                availableLocations[it]?.let { location ->
                    AvailableLocationItem(
                        isSelectedLocation = stepFourState.locationId == location.locationId,
                        availableLocation = location,
                        saveStepFour = saveStepFour,
                        isInfoOpened = location.locationId == selectedInfoId,
                        selectedLocationInfo = { selected ->
                            selectedInfoId = selected
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AvailableLocationItem(
    isSelectedLocation: Boolean,
    isInfoOpened: Boolean = false,
    availableLocation: AvailableLocation,
    saveStepFour: (locationId: String) -> Unit,
    selectedLocationInfo: (locationId: String?) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                saveStepFour.invoke(availableLocation.locationId ?: return@clickable)
            }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = availableLocation.locationId ?: "undefined",
                modifier = Modifier.weight(if (isSelectedLocation) 7f else 8f)
            )
            if (isSelectedLocation) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.weight(1f)
                )
            }
            IconButton(onClick = {
                if (isInfoOpened) {
                    selectedLocationInfo.invoke(null)
                } else {
                    selectedLocationInfo.invoke(availableLocation.locationId)
                }
            }, modifier = Modifier.size(24.dp).weight(1f)) {
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
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = Orange,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(id = titleResId),
            modifier = Modifier.weight(7f).padding(start = 4.dp)
        )
    }
}
