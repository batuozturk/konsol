package com.batuhan.firestore.presentation.database.document.fields.bytype

import androidx.compose.runtime.Composable
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.firestore.presentation.database.document.fields.*

@Composable
fun DocumentFieldItemByType(
    field: DocumentField,
    fieldIndex: Int,
    parentFieldIndex: Int? = null,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit,
    removeDocumentField: (Int?, Int?) -> Unit,
    setEditingState: (Boolean) -> Unit
) {
    when (field) {
        is DocumentField.StringValue -> StringField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.ArrayValue -> ArrayField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.BooleanValue -> BooleanField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.BytesValue -> BytesField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.DoubleValue -> DoubleField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.GeoPointValue -> GeoPointField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.IntegerValue -> IntegerField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.MapValue -> MapField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.ReferenceValue -> ReferenceField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
        is DocumentField.TimestampValue -> TimestampField(
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField,
            removeDocumentField = removeDocumentField,
            setEditingState = setEditingState
        )
    }
}