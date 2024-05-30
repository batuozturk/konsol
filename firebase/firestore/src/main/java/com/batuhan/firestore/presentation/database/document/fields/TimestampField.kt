package com.batuhan.firestore.presentation.database.document.fields

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.firestore.data.model.TimestampFormat
import com.batuhan.firestore.data.model.toFormattedTimestamp
import com.batuhan.theme.Orange

@Composable
fun TimestampField(
    field: DocumentField.TimestampValue,
    fieldIndex: Int,
    parentFieldIndex: Int? = null,
    parentCount: Int = 0,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    removeDocumentField: (Int?, Int?) -> Unit,
    setEditingState: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(7f)
        ) {
            field.attributeName.takeIf { it.isNotEmpty() }?.let { Text(field.attributeName) }
            Text(field.value.toFormattedTimestamp())
            Text(field.fieldType.name.lowercase())
        }
        if (parentCount < 2) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    setEditingState.invoke(true)
                    editDocumentField.invoke(field, fieldIndex, parentFieldIndex)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Orange
                )
            }
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { removeDocumentField.invoke(fieldIndex, parentFieldIndex) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Orange
                )
            }
        }
    }
}

@Composable
fun EditableTimestampField(
    isEditing: Boolean,
    field: DocumentField.TimestampValue,
    fieldIndex: Int?,
    parentFieldIndex: Int?,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            label = { Text(stringResource(id = R.string.attribute_name)) },
            value = field.attributeName,
            isError = field.errorState == DocumentField.DocumentFieldErrorState.ATTRIBUTE_NAME_EMPTY,
            readOnly = isEditing,
            enabled = !isEditing,
            onValueChange = {
                editDocumentField.invoke(
                    field.copy(attributeName = it),
                    fieldIndex,
                    parentFieldIndex
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedBorderColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                errorCursorColor = Orange,
                disabledBorderColor = Orange,
                disabledLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
            )
        )
        field.errorState.takeIf { it == DocumentField.DocumentFieldErrorState.ATTRIBUTE_NAME_EMPTY }
            ?.let {
                Text(stringResource(id = it.errorTitleResId), color = Color.Red, fontSize = 14.sp)
            }
        TimestampEditableDateField(
            errorState = field.errorState,
            focusManager = focusManager,
            updateValue = {
                editDocumentField.invoke(
                    field.copy(value = it),
                    fieldIndex,
                    parentFieldIndex
                )
            },
            timestampFormat = field.value
        )
        field.errorState.takeIf {
            it == DocumentField.DocumentFieldErrorState.TIMESTAMP_YEAR_VALUE_INVALID ||
                it == DocumentField.DocumentFieldErrorState.TIMESTAMP_MONTH_VALUE_INVALID ||
                it == DocumentField.DocumentFieldErrorState.TIMESTAMP_DAY_VALUE_INVALID
        }?.let {
            Text(stringResource(id = it.errorTitleResId), color = Color.Red, fontSize = 14.sp)
        }
        TimestampEditableTimeField(
            errorState = field.errorState,
            focusManager = focusManager,
            updateValue = {
                editDocumentField.invoke(
                    field.copy(value = it),
                    fieldIndex,
                    parentFieldIndex
                )
            },
            timestampFormat = field.value
        )
        field.errorState.takeIf {
            it == DocumentField.DocumentFieldErrorState.TIMESTAMP_HOUR_VALUE_INVALID ||
                it == DocumentField.DocumentFieldErrorState.TIMESTAMP_MINUTE_VALUE_INVALID ||
                it == DocumentField.DocumentFieldErrorState.TIMESTAMP_SECOND_VALUE_INVALID
        }?.let {
            Text(stringResource(id = it.errorTitleResId), color = Color.Red, fontSize = 14.sp)
        }
    }
}

@Composable
fun TimestampEditableDateField(
    errorState: DocumentField.DocumentFieldErrorState?,
    focusManager: FocusManager,
    updateValue: (TimestampFormat) -> Unit,
    timestampFormat: TimestampFormat
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier.weight(1f).padding(8.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            label = { Text(stringResource(id = R.string.year)) },
            isError = errorState == DocumentField.DocumentFieldErrorState.TIMESTAMP_YEAR_VALUE_INVALID,
            value = timestampFormat.year.toString(),
            onValueChange = {
                updateValue.invoke(
                    timestampFormat.copy(year = it)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedBorderColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                errorCursorColor = Orange,
                disabledBorderColor = Orange,
                disabledLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
            )
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f).padding(8.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            isError = errorState == DocumentField.DocumentFieldErrorState.TIMESTAMP_MONTH_VALUE_INVALID,
            label = { Text(stringResource(id = R.string.month)) },
            value = timestampFormat.month.toString(),
            onValueChange = {
                updateValue.invoke(
                    timestampFormat.copy(month = it)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedBorderColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                errorCursorColor = Orange,
                disabledBorderColor = Orange,
                disabledLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
            )
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f).padding(8.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            isError = errorState == DocumentField.DocumentFieldErrorState.TIMESTAMP_DAY_VALUE_INVALID,
            label = { Text(stringResource(id = R.string.day)) },
            value = timestampFormat.day.toString(),
            onValueChange = {
                updateValue.invoke(
                    timestampFormat.copy(day = it)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedBorderColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                errorCursorColor = Orange,
                disabledBorderColor = Orange,
                disabledLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
            )
        )
    }
}

@Composable
fun TimestampEditableTimeField(
    errorState: DocumentField.DocumentFieldErrorState?,
    focusManager: FocusManager,
    updateValue: (TimestampFormat) -> Unit,
    timestampFormat: TimestampFormat
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier.weight(1f).padding(8.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            label = { Text(stringResource(id = R.string.hour)) },
            isError = errorState == DocumentField.DocumentFieldErrorState.TIMESTAMP_HOUR_VALUE_INVALID,
            value = timestampFormat.hour.toString(),
            onValueChange = {
                updateValue.invoke(
                    timestampFormat.copy(hour = it)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedBorderColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                errorCursorColor = Orange,
                disabledBorderColor = Orange,
                disabledLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
            )
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f).padding(8.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            isError = errorState == DocumentField.DocumentFieldErrorState.TIMESTAMP_MINUTE_VALUE_INVALID,
            label = { Text(stringResource(id = R.string.minute)) },
            value = timestampFormat.minute.toString(),
            onValueChange = {
                updateValue.invoke(
                    timestampFormat.copy(minute = it)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedBorderColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                errorCursorColor = Orange,
                disabledBorderColor = Orange,
                disabledLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
            )
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f).padding(8.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            isError = errorState == DocumentField.DocumentFieldErrorState.TIMESTAMP_SECOND_VALUE_INVALID,
            label = { Text(stringResource(id = R.string.second)) },
            value = timestampFormat.second.toString(),
            onValueChange = {
                updateValue.invoke(
                    timestampFormat.copy(second = it)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange,
                unfocusedBorderColor = Orange,
                unfocusedLabelColor = Orange,
                cursorColor = Orange,
                errorCursorColor = Orange,
                disabledBorderColor = Orange,
                disabledLabelColor = Orange,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red,
                selectionColors = TextSelectionColors(Orange, Orange.copy(alpha = 0.4f))
            )
        )
    }
}
