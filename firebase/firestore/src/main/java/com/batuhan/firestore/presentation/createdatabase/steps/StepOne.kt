package com.batuhan.firestore.presentation.createdatabase.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.firestore.R
import com.batuhan.firestore.presentation.createdatabase.StepOneState
import com.batuhan.theme.Orange

@Composable
fun StepOne(stepOneState: StepOneState, updateDatabaseName: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = {
                Text(stringResource(id = R.string.database_name))
            },
            value = stepOneState.databaseName ?: "",
            onValueChange = { updateDatabaseName.invoke(it) },
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
    }
}
