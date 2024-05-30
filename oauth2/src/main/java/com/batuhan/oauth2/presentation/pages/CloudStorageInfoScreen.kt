package com.batuhan.oauth2.presentation.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.batuhan.theme.Orange
import com.batuhan.oauth2.R

@Composable
fun CloudStorageInfoScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = Orange,
            modifier = Modifier.size(200.dp).padding(8.dp)
        )
        Text(
            stringResource(id = R.string.cloud_storage_info_desc),
            textAlign = TextAlign.Center
        )
    }
}
