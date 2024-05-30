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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.theme.Orange

@Composable
fun IntegerField(
    field: DocumentField.IntegerValue,
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
            Text(field.value)
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
fun EditableIntegerField(
    isEditing: Boolean,
    field: DocumentField.IntegerValue,
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
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            label = { Text(stringResource(id = R.string.attribute_value)) },
            value = field.value,
            isError = field.errorState == DocumentField.DocumentFieldErrorState.INTEGER_VALUE_INVALID,
            onValueChange = {
                editDocumentField.invoke(
                    field.copy(value = it),
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
        field.errorState.takeIf { it == DocumentField.DocumentFieldErrorState.INTEGER_VALUE_INVALID }
            ?.let {
                Text(stringResource(id = it.errorTitleResId), color = Color.Red, fontSize = 14.sp)
            }
    }
}
