package com.batuhan.management.presentation.project.settings.actions.createapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.batuhan.management.R
import com.batuhan.theme.FConsoleTheme
import com.batuhan.theme.Orange

@Composable
fun CreateAppInput(
    createAppInputState: CreateAppInputState,
    appType: AppType?,
    saveContentForAndroid: (packageName: String, appName: String?, sha1String: String?) -> Unit,
    saveContentForIos: (bundleId: String, appName: String?, appStoreId: String?) -> Unit,
    saveContentForWeb: (appName: String) -> Unit
) {
    val createAppInputStateAndroid =
        createAppInputState.takeIf { it is CreateAppInputState.Android } as? CreateAppInputState.Android
    val createAppInputStateIos =
        createAppInputState.takeIf { it is CreateAppInputState.Ios } as? CreateAppInputState.Ios
    val createAppInputStateWeb =
        createAppInputState.takeIf { it is CreateAppInputState.Web } as? CreateAppInputState.Web
    when (appType) {
        AppType.ANDROID -> CreateAppInputForAndroid(
            createAppInputState = createAppInputStateAndroid,
            saveContentForAndroid = saveContentForAndroid
        )
        AppType.IOS -> CreateAppInputForIos(
            createAppInputState = createAppInputStateIos,
            saveContentForIos = saveContentForIos
        )
        AppType.WEB -> CreateAppInputForWeb(
            createAppInputState = createAppInputStateWeb,
            saveContentForWeb = saveContentForWeb
        )
        else -> {}
    }
}

@Composable
fun CreateAppInputForAndroid(
    createAppInputState: CreateAppInputState.Android?,
    saveContentForAndroid: (packageName: String, appName: String?, sha1String: String?) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(handleColor = Orange, backgroundColor = Orange.copy(alpha = 0.4f))
            ),
            value = createAppInputState?.packageName ?: "",
            onValueChange = { packageName: String ->
                saveContentForAndroid(
                    packageName,
                    createAppInputState?.appName,
                    createAppInputState?.sha1String
                )
            },
            label = {
                Text(stringResource(id = R.string.create_app_package_name))
            }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(handleColor = Orange, backgroundColor = Orange.copy(alpha = 0.4f))
            ),
            value = createAppInputState?.appName ?: "",
            onValueChange = { appName: String ->
                saveContentForAndroid(
                    createAppInputState?.packageName ?: "",
                    appName,
                    createAppInputState?.sha1String
                )
            },
            label = {
                Text(stringResource(id = R.string.create_app_app_name))
            }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(handleColor = Orange, backgroundColor = Orange.copy(alpha = 0.4f))
            ),
            value = createAppInputState?.sha1String ?: "",
            onValueChange = { sha1String: String ->
                saveContentForAndroid(
                    createAppInputState?.packageName ?: "",
                    createAppInputState?.appName ?: "",
                    sha1String
                )
            },
            label = {
                Text(stringResource(id = R.string.create_app_sha1))
            }
        )
    }
}

@Composable
fun CreateAppInputForIos(
    createAppInputState: CreateAppInputState.Ios?,
    saveContentForIos: (bundleId: String, appName: String?, appStoreId: String?) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(handleColor = Orange, backgroundColor = Orange.copy(alpha = 0.4f))
            ),
            value = createAppInputState?.bundleId ?: "",
            onValueChange = { bundleId: String ->
                saveContentForIos(
                    bundleId,
                    createAppInputState?.appName,
                    createAppInputState?.appStoreId
                )
            },
            label = {
                Text(stringResource(id = R.string.create_app_bundle_id))
            }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(handleColor = Orange, backgroundColor = Orange.copy(alpha = 0.4f))
            ),
            value = createAppInputState?.appName ?: "",
            onValueChange = { appName: String ->
                saveContentForIos(
                    createAppInputState?.bundleId ?: "",
                    appName,
                    createAppInputState?.appStoreId
                )
            },
            label = {
                Text(stringResource(id = R.string.create_app_app_name))
            }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Orange,
                unfocusedLabelColor = Orange,
                focusedBorderColor = Orange,
                unfocusedBorderColor = Orange,
                cursorColor = Orange,
                selectionColors = TextSelectionColors(handleColor = Orange, backgroundColor = Orange.copy(alpha = 0.4f))
            ),
            value = createAppInputState?.appStoreId ?: "",
            onValueChange = { appStoreId: String ->
                saveContentForIos(
                    createAppInputState?.bundleId ?: "",
                    createAppInputState?.appName,
                    appStoreId
                )
            },
            label = {
                Text(stringResource(id = R.string.create_app_app_store_id))
            }
        )
    }
}

@Composable
fun CreateAppInputForWeb(
    createAppInputState: CreateAppInputState.Web?,
    saveContentForWeb: (appName: String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedLabelColor = Orange,
            unfocusedLabelColor = Orange,
            focusedBorderColor = Orange,
            unfocusedBorderColor = Orange,
            cursorColor = Orange,
            selectionColors = TextSelectionColors(handleColor = Orange, backgroundColor = Orange.copy(alpha = 0.4f))
        ),
        value = createAppInputState?.appName ?: "",
        onValueChange = { appName: String ->
            saveContentForWeb(appName)
        },
        label = {
            Text(stringResource(id = R.string.create_app_app_name))
        }
    )
}

@Preview
@Composable
fun StepOnePreview() {
    FConsoleTheme {
        CreateAppInput(
            createAppInputState = CreateAppInputState.Unspecified,
            appType = AppType.ANDROID,
            saveContentForAndroid = { _, _, _ -> },
            saveContentForIos = { _, _, _ -> },
            saveContentForWeb = {}
        )
    }
}
