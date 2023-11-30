package com.batuhan.realtimedatabase.presentation.databaseitem

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batuhan.realtimedatabase.R
import com.batuhan.realtimedatabase.data.model.DatabaseItemType
import com.batuhan.theme.Orange
import com.batuhan.realtimedatabase.data.model.DatabaseItemType as ItemType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseItemBottomSheet(
    errorState: DatabaseItemErrorState?,
    isEditing: Boolean,
    bottomSheetState: SheetState,
    selectedType: String,
    key: String?,
    value: String?,
    patchData: () -> Unit,
    onClose: () -> Unit,
    updateKey: (String) -> Unit,
    updateValue: (String) -> Unit,
    updateType: (String) -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        dragHandle = {},
        onDismissRequest = {
            onClose.invoke()
        },
        sheetState = bottomSheetState
    ) {
        Column(
            Modifier.fillMaxWidth().padding(8.dp)
        ) {
            DatabaseItemBottomSheetToolbar(
                errorState,
                isEditing = isEditing,
                onClose = {
                    onClose.invoke()
                },
                patchData = patchData
            )
            DatabaseItemType(
                isSelected = selectedType == ItemType.STRING.name,
                type = ItemType.STRING,
                setType = updateType
            )
            DatabaseItemType(
                isSelected = selectedType == ItemType.INTEGER.name,
                type = ItemType.INTEGER,
                setType = updateType
            )
            DatabaseItemType(
                isSelected = selectedType == ItemType.BOOLEAN.name,
                type = ItemType.BOOLEAN,
                setType = updateType
            )
            DatabaseItemType(
                isSelected = selectedType == ItemType.OBJECT.name,
                type = ItemType.OBJECT,
                setType = updateType
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = key ?: "",
                onValueChange = updateKey,
                enabled = !(isEditing),
                label = {
                    Text(stringResource(id = R.string.key_text))
                },
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
            errorState?.takeIf { it == DatabaseItemErrorState.KEY_EMPTY }?.let {
                Text(
                    stringResource(id = it.titleResId),
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = value ?: "",
                onValueChange = updateValue,
                enabled = selectedType != DatabaseItemType.OBJECT.name,
                label = {
                    Text(stringResource(id = R.string.value_text))
                },
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
            errorState?.takeIf { it == DatabaseItemErrorState.VALUE_INVALID }?.let {
                Text(
                    stringResource(id = it.titleResId),
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DatabaseItemBottomSheetToolbar(
    errorState: DatabaseItemErrorState?,
    isEditing: Boolean,
    onClose: () -> Unit,
    patchData: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClose, modifier = Modifier.weight(1f).padding(end = 4.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                tint = Orange,
                contentDescription = null
            )
        }
        Text(
            if (isEditing) "Edit database field" else "Add database field",
            modifier = Modifier.weight(5f)
        )
        IconButton(onClick = {
            if (errorState.takeIf {
                it == DatabaseItemErrorState.KEY_EMPTY ||
                    it == DatabaseItemErrorState.VALUE_INVALID
            } == null
            ) {
                patchData.invoke()
                onClose.invoke()
            }
        }, modifier = Modifier.weight(1f).padding(end = 4.dp)) {
            Icon(
                imageVector = Icons.Default.Save,
                tint = Orange,
                contentDescription = null
            )
        }
    }
}

@Composable
fun DatabaseItemType(
    isSelected: Boolean,
    type: ItemType,
    setType: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable(role = Role.RadioButton) {
                setType.invoke(type.name)
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { setType.invoke(type.name) },
            colors = RadioButtonDefaults.colors(Orange)
        )
        Text(stringResource(type.titleResId))
    }
}
