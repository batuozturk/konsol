package com.batuhan.firestore.presentation.database.document

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
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
import com.batuhan.firestore.presentation.database.document.fields.bytype.EditableDocumentFieldItemByType
import com.batuhan.firestore.util.createDocumentField
import com.batuhan.firestore.util.generateFieldTypeList
import com.batuhan.theme.Orange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentFieldBottomSheet(
    field: DocumentField?,
    parentDocumentFieldIndex: Int?,
    fieldIndex: Int?,
    isEditing: Boolean = false,
    coroutineScope: CoroutineScope,
    bottomSheetState: SheetState,
    setBottomSheetState: (Boolean) -> Unit,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    onSave: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        dragHandle = {},
        onDismissRequest = {
            coroutineScope.launch {
                bottomSheetState.hide()
            }.invokeOnCompletion {
                setBottomSheetState.invoke(false)
            }
        },
        sheetState = bottomSheetState
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .weight(1f),
                onClick = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }.invokeOnCompletion {
                        setBottomSheetState.invoke(false)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Orange
                )
            }
            Text(
                text = stringResource(
                    id =
                    if (isEditing) R.string.edit_document_field_title
                    else R.string.add_coument_field_title
                ),
                color = Color.Black,
                fontSize = 20.sp,
                modifier = Modifier.weight(4f)
            )
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .weight(1f),
                onClick = {
                    if (field?.errorState == null) {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }.invokeOnCompletion {
                            setBottomSheetState.invoke(false)
                            onSave.invoke()
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Orange
                )
            }
        }
        DocumentFieldList(
            isEditing = isEditing,
            editDocumentField = editDocumentField,
            field = field!!,
            parentFieldIndex = parentDocumentFieldIndex,
            fieldIndex = fieldIndex
        )
    }
}

@Composable
fun DocumentFieldList(
    isEditing: Boolean,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    field: DocumentField,
    parentFieldIndex: Int? = null,
    fieldIndex: Int? = null
) {
    val isParentFieldExist = parentFieldIndex != null
    val options = generateFieldTypeList(isParentFieldExist)
    var selectedType: String by remember {
        mutableStateOf(field.fieldType.name)
    }
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(options.size) {
            DocumentFieldTypeItem(
                isSelected = selectedType == options[it].name,
                documentFieldType = options[it],
                parentFieldIndex = parentFieldIndex,
                fieldIndex = fieldIndex,
                editDocumentField = editDocumentField,
                setType = {
                    selectedType = it.name
                },
                attributeName = field.attributeName
            )
        }
        item(span = { GridItemSpan(2) }) {
            EditableDocumentFieldItemByType(
                isEditing = isEditing,
                field = field,
                fieldIndex = fieldIndex,
                parentFieldIndex = parentFieldIndex,
                editDocumentField = editDocumentField
            )
        }
    }
}

@Composable
fun DocumentFieldTypeItem(
    isSelected: Boolean,
    documentFieldType: DocumentField.Type,
    parentFieldIndex: Int?,
    fieldIndex: Int?,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    setType: (DocumentField.Type) -> Unit,
    attributeName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp).clickable {
                setType.invoke(documentFieldType)
                editDocumentField.invoke(
                    createDocumentField(documentFieldType, attributeName = attributeName),
                    fieldIndex,
                    parentFieldIndex
                )
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
                setType.invoke(documentFieldType)
                editDocumentField.invoke(
                    createDocumentField(documentFieldType, attributeName = attributeName),
                    fieldIndex,
                    parentFieldIndex
                )
            }
        )
        Text(stringResource(id = documentFieldType.typeResId))
    }
}
