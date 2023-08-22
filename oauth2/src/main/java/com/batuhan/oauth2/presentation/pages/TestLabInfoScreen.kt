package com.batuhan.oauth2.presentation.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DevicesOther
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
fun TestLabInfoScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.DevicesOther,
            contentDescription = null,
            tint = Orange,
            modifier = Modifier.size(200.dp).padding(8.dp)
        )
        Text(
            stringResource(id = R.string.test_lab_info_desc),
            textAlign = TextAlign.Center
        )
    }
}
