package com.batuhan.testlab.presentation.creatematrix.steps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.testlab.R
import com.batuhan.testlab.data.model.devicecatalog.*
import com.batuhan.testlab.data.model.matrix.AndroidDevice
import com.batuhan.theme.Orange
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
fun StepThreeBottomSheet(
    onDismiss: () -> Unit,
    androidDeviceList: List<AndroidModel>,
    selectedModel: AndroidDevice?,
    locales: List<Locale>,
    orientations: List<Orientation>,
    localeFilterInput: String,
    deviceFilterInput: String,
    addAndroidDevice: (AndroidDevice) -> Unit,
    updateLocaleFilter: (String) -> Unit,
    updateDeviceFilter: (String) -> Unit,
    updateSelectedItem: (AndroidDevice?) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Orange
                )
            }
            Text(stringResource(id = R.string.add_device), modifier = Modifier.weight(6f))
            IconButton(onClick = {
                if (selectedModel == null) return@IconButton
                else {
                    addAndroidDevice(selectedModel)
                    onDismiss.invoke()
                }
            }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Orange
                )
            }
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = deviceFilterInput,
            onValueChange = updateDeviceFilter,
            label = {
                Text(stringResource(R.string.device_name))
            },
            trailingIcon = {
                IconButton(onClick = { updateDeviceFilter.invoke("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Orange
                    )
                }
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
                ),
                unfocusedTrailingIconColor = Orange,
                focusedTrailingIconColor = Orange
            )
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(androidDeviceList.size) {
                EditableAndroidDeviceItem(
                    selectedModel?.androidModelId == androidDeviceList[it].id,
                    selectedModel,
                    androidDeviceList[it],
                    orientations,
                    locales,
                    localeFilterInput,
                    openConfigList = { androidModel ->
                        // todo close
                        updateSelectedItem(
                            androidModel?.toAndroidDevice()
                        )
                    },
                    updateLocaleFilter = updateLocaleFilter,
                    updateSelectedItemLocale = { locale ->
                        updateSelectedItem(
                            selectedModel?.copy(locale = locale) ?: return@EditableAndroidDeviceItem
                        )
                    },
                    updateSelectedItemOrientation = { orientation ->
                        updateSelectedItem(
                            selectedModel?.copy(orientation = orientation)
                                ?: return@EditableAndroidDeviceItem
                        )
                    },
                    updateSelectedItemVersion = { version ->
                        updateSelectedItem(
                            selectedModel?.copy(androidVersionId = version)
                                ?: return@EditableAndroidDeviceItem
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun EditableAndroidDeviceItem(
    isConfigListOpened: Boolean,
    selectedModel: AndroidDevice?,
    androidDevice: AndroidModel,
    orientations: List<Orientation>,
    locales: List<Locale>,
    localeFilterInput: String,
    openConfigList: (AndroidModel?) -> Unit,
    updateLocaleFilter: (String) -> Unit,
    updateSelectedItemOrientation: (Orientation) -> Unit,
    updateSelectedItemLocale: (Locale) -> Unit,
    updateSelectedItemVersion: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        AndroidDeviceItem(
            androidDevice = androidDevice,
            isConfigListOpened = isConfigListOpened,
            openConfigList = openConfigList
        )
    }
    AnimatedVisibility(visible = isConfigListOpened) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.version_list))
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(androidDevice.supportedVersionIds?.size ?: 0) {
                    VersionConfigItem(
                        isSelectedConfig =
                        selectedModel?.androidVersionId == androidDevice.supportedVersionIds?.get(it),
                        config = androidDevice.supportedVersionIds?.get(it),
                        updateSelectedItem = updateSelectedItemVersion
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(id = R.string.locale_list))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = localeFilterInput,
                onValueChange = updateLocaleFilter,
                label = {
                    Text(stringResource(id = R.string.locale_name))
                },
                trailingIcon = {
                    IconButton(onClick = { updateLocaleFilter.invoke("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Orange
                        )
                    }
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
                    ),
                    unfocusedTrailingIconColor = Orange,
                    focusedTrailingIconColor = Orange
                )
            )
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(locales.size) {
                    LocaleConfigItem(
                        isSelectedConfig = selectedModel?.locale?.id == locales[it].id,
                        config = locales[it],
                        updateSelectedItem = updateSelectedItemLocale
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(id = R.string.orientation_list))
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(orientations.size) {
                    OrientationConfigItem(
                        isSelectedConfig = orientations[it].id == selectedModel?.orientation?.id,
                        config = orientations[it],
                        updateSelectedItem = updateSelectedItemOrientation
                    )
                }
            }
        }
    }
}

@Composable
fun OrientationConfigItem(
    isSelectedConfig: Boolean,
    config: Orientation?,
    updateSelectedItem: (Orientation) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                updateSelectedItem.invoke(config ?: return@clickable)
            }
            .padding(10.dp)
    ) {
        Text(config?.name ?: stringResource(id = R.string.undefined))
        if (isSelectedConfig) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}

@Composable
fun LocaleConfigItem(
    isSelectedConfig: Boolean,
    config: Locale?,
    updateSelectedItem: (Locale) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                updateSelectedItem.invoke(config ?: return@clickable)
            }
            .padding(10.dp)
    ) {
        Text(
            "${config?.name ?: stringResource(id = R.string.unknown_locale)} " +
                "(${config?.region ?: stringResource(id = R.string.unknown_region)})"
        )
        if (isSelectedConfig) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}

@Composable
fun VersionConfigItem(
    isSelectedConfig: Boolean,
    config: String?,
    updateSelectedItem: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                updateSelectedItem.invoke(config ?: return@clickable)
            }
            .padding(10.dp)
    ) {
        Text(config ?: stringResource(id = R.string.undefined))
        if (isSelectedConfig) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AndroidDeviceItem(
    androidDevice: AndroidModel,
    isConfigListOpened: Boolean = false,
    openConfigList: (AndroidModel?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        GlideImage(
            model = androidDevice.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier.weight(2f)
        )
        Column(modifier = Modifier.weight(6f)) {
            Text(androidDevice.name ?: stringResource(id = R.string.undefined))
            Text(androidDevice.brand ?: stringResource(id = R.string.undefined))
            Text(androidDevice.manufacturer ?: stringResource(id = R.string.undefined))
            Text(androidDevice.form?.name ?: stringResource(id = R.string.undefined))
            Text("${androidDevice.screenY} x ${androidDevice.screenX}")
        }
        IconButton(
            onClick = {
                if (isConfigListOpened) {
                    openConfigList.invoke(null)
                } else {
                    openConfigList.invoke(androidDevice ?: return@IconButton)
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = if (isConfigListOpened) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}

/*@Composable
fun StepThreeTextField(
    value: String,
    @StringRes labelResId: Int,
    updateStepThree: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = {
            updateStepThree.invoke(it)
        },
        label = {
            Text(stringResource(id = labelResId))
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
}*/
