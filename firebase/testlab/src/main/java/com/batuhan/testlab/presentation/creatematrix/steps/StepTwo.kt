package com.batuhan.testlab.presentation.creatematrix.steps

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.testlab.R
import com.batuhan.testlab.presentation.creatematrix.StepTwoState
import com.batuhan.testlab.presentation.creatematrix.TestType
import com.batuhan.theme.Orange

@Composable
fun StepTwo(
    testType: TestType,
    stepTwoState: StepTwoState,
    browseFilesOnCloudStorage: (Boolean) -> Unit,
    uploadFile: (Boolean) -> Unit,
    clearApp: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        AppBox(
            isTestApp = false,
            gcsPath = stepTwoState.gcsPath,
            clearApp = {
                clearApp.invoke(false)
            },
            browseFilesOnCloudStorage = {
                browseFilesOnCloudStorage.invoke(false)
            },
            uploadFile = {
                uploadFile.invoke(false)
            }
        )
        if (testType is TestType.AndroidInstrumentationTest) {
            AppBox(
                isTestApp = true,
                gcsPath = stepTwoState.testGcsPath,
                clearApp = {
                    clearApp.invoke(true)
                },
                browseFilesOnCloudStorage = {
                    browseFilesOnCloudStorage.invoke(true)
                },
                uploadFile = {
                    uploadFile.invoke(true)
                }
            )
        }
    }
}

@Composable
fun AppBox(
    isTestApp: Boolean,
    gcsPath: String?,
    clearApp: () -> Unit,
    browseFilesOnCloudStorage: () -> Unit,
    uploadFile: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            if (isTestApp) stringResource(id = R.string.test_app) else stringResource(id = R.string.app),
            modifier = Modifier.padding(8.dp)
        )
        gcsPath?.let {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp)
                    .border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(it, modifier = Modifier.weight(7f))
                IconButton(onClick = clearApp, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Orange
                    )
                }
            }
        } ?: run {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { uploadFile.invoke() }
                    .border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    stringResource(id = R.string.upload_file),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp)
                    .clickable { browseFilesOnCloudStorage.invoke() }
                    .border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FileOpen,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    stringResource(id = R.string.select_file_cloud_storage),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
