package com.batuhan.firestore.presentation.createdatabase.steps

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.firestore.R
import com.batuhan.firestore.presentation.createdatabase.StepOneState
import com.batuhan.firestore.presentation.createdatabase.StepTwoState
import com.batuhan.theme.Orange

@Composable
fun StepPreview(stepOneState: StepOneState, stepTwoState: StepTwoState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(stringResource(id = R.string.database_preview), modifier = Modifier.padding(8.dp))
        }
        item {
            PreviewItem(title = R.string.database_name, value = stepOneState.databaseName ?: "")
        }
        item {
            PreviewItem(
                title = R.string.database_location_id,
                value = stepTwoState.locationId ?: ""
            )
        }
    }
}

@Composable
fun PreviewItem(@StringRes title: Int, value: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(stringResource(id = title))
        Text(value)
    }
}
