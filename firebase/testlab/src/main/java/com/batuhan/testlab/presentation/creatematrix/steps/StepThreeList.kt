package com.batuhan.testlab.presentation.creatematrix.steps

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.batuhan.testlab.R
import com.batuhan.testlab.data.model.matrix.AndroidDevice
import com.batuhan.theme.Orange
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
fun StepThreeList(
    deviceList: List<AndroidDevice>,
    addDevice: () -> Unit,
    removeDevice: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Button(
                onClick = addDevice,
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                Text(
                    text = "Add device",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
        items(deviceList.size) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(2.dp, Orange, RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                AndroidDeviceItem(
                    androidDevice = deviceList[it],
                    removeDevice = {
                        removeDevice.invoke(it)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AndroidDeviceItem(
    androidDevice: AndroidDevice,
    removeDevice: () -> Unit
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
            Text(androidDevice.androidVersionId ?: stringResource(id = R.string.undefined))
            Text(
                "${androidDevice.locale?.name ?: stringResource(id = R.string.unknown_locale)} " +
                    "(${androidDevice.locale?.region ?: stringResource(id = R.string.unknown_region)})"
            )
            Text(androidDevice.orientation?.name ?: stringResource(id = R.string.undefined))
        }
        IconButton(
            onClick = removeDevice,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}
