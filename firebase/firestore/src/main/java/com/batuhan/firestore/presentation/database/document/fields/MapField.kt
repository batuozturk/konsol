package com.batuhan.firestore.presentation.database.document.fields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.firestore.presentation.database.document.fields.bytype.DocumentFieldItemByType
import com.batuhan.firestore.util.createDocumentField
import com.batuhan.theme.Orange

@Composable
fun MapField(
    field: DocumentField.MapValue,
    fieldIndex: Int,
    parentFieldIndex: Int? = null,
    parentCount: Int = 0,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    removeDocumentField: (Int?, Int?) -> Unit,
    setEditingState: (Boolean) -> Unit
) {
    var isOpened by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
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
                modifier = Modifier.weight(7f)
            ) {
                field.attributeName.takeIf { it.isNotEmpty() }?.let { Text(field.attributeName) }
                Text(field.fieldType.name.lowercase())
            }
            if (parentCount < 1) {
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        setEditingState.invoke(false)
                        editDocumentField.invoke(
                            createDocumentField(DocumentField.Type.STRING),
                            null,
                            fieldIndex
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = Orange
                    )
                }
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
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { isOpened = !isOpened }
            ) {
                Icon(
                    imageVector = if (isOpened) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Orange
                )
            }
        }
        AnimatedVisibility(visible = isOpened) {
            Column(modifier = Modifier.fillMaxSize().padding(start = 8.dp)) {
                field.values.forEachIndexed { index, documentField ->
                    DocumentFieldItemByType(
                        field = documentField,
                        fieldIndex = index,
                        parentCount = parentCount + 1,
                        parentFieldIndex = fieldIndex,
                        editDocumentField = editDocumentField,
                        removeDocumentField = removeDocumentField,
                        setEditingState = setEditingState
                    )
                }
            }
        }
    }
}

@Composable
fun EditableMapField(
    isEditing: Boolean,
    field: DocumentField.MapValue,
    fieldIndex: Int?,
    parentFieldIndex: Int?,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            label = { Text(stringResource(id = R.string.attribute_name)) },
            value = field.attributeName,
            isError = field.errorState != null,
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
        field.errorState?.let {
            Text(stringResource(id = it.errorTitleResId), color = Color.Red, fontSize = 14.sp)
        }
    }
}
