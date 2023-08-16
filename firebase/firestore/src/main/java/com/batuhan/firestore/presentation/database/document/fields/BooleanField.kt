package com.batuhan.firestore.presentation.database.document.fields

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.theme.Orange

@Composable
fun BooleanField(
    field: DocumentField.BooleanValue,
    fieldIndex: Int,
    parentFieldIndex: Int? = null,
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
            Text(field.value.toString())
            Text(field.fieldType.name.lowercase())
        }
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

@Composable
fun EditableBooleanField(
    isEditing: Boolean,
    field: DocumentField.BooleanValue,
    fieldIndex: Int?,
    parentFieldIndex: Int?,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit
) {
    var isSelectedValue by remember {
        mutableStateOf(field.value)
    }
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
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
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BooleanRadioButtonItem(
                modifier = Modifier.weight(1f),
                isSelected = isSelectedValue,
                booleanValue = true
            ) {
                isSelectedValue = true
                editDocumentField.invoke(
                    field.copy(value = true),
                    fieldIndex,
                    parentFieldIndex
                )
            }
            BooleanRadioButtonItem(
                modifier = Modifier.weight(1f),
                isSelected = !isSelectedValue,
                booleanValue = false
            ) {
                isSelectedValue = false
                editDocumentField.invoke(
                    field.copy(value = false),
                    fieldIndex,
                    parentFieldIndex
                )
            }
        }
    }
}

@Composable
fun BooleanRadioButtonItem(
    modifier: Modifier,
    isSelected: Boolean,
    booleanValue: Boolean,
    selectValue: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(8.dp).clickable {
                selectValue.invoke()
            }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            colors = RadioButtonDefaults.colors(
                selectedColor = Orange,
                unselectedColor = Color.Black
            ),
            selected = isSelected,
            onClick = {
                selectValue.invoke()
            }
        )
        Text(booleanValue.toString())
    }
}
