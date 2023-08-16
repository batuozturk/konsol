package com.batuhan.firestore.presentation.database.document.fields.bytype

import androidx.compose.runtime.Composable
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.firestore.presentation.database.document.fields.*

@Composable
fun EditableDocumentFieldItemByType(
    isEditing: Boolean,
    field: DocumentField,
    fieldIndex: Int?,
    parentFieldIndex: Int? = null,
    editDocumentField: (DocumentField, Int?, Int?) -> Unit
) {
    when (field) {
        is DocumentField.StringValue -> EditableStringField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.IntegerValue -> EditableIntegerField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.BooleanValue -> EditableBooleanField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.BytesValue -> EditableBytesField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.GeoPointValue -> EditableGeoPointField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.ReferenceValue -> EditableReferenceField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.TimestampValue -> EditableTimestampField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.DoubleValue -> EditableDoubleField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.MapValue -> EditableMapField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
        is DocumentField.ArrayValue -> EditableArrayField(
            isEditing = isEditing,
            field = field,
            fieldIndex = fieldIndex,
            parentFieldIndex = parentFieldIndex,
            editDocumentField = editDocumentField
        )
    }
}
