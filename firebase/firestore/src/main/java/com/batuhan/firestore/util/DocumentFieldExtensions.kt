package com.batuhan.firestore.util

import com.batuhan.firestore.data.model.*

fun DocumentField.getValue(): Value {
    return when (this) {
        is DocumentField.StringValue -> Value(stringValue = value)
        is DocumentField.BooleanValue -> Value(booleanValue = value)
        is DocumentField.IntegerValue -> Value(integerValue = value)
        is DocumentField.DoubleValue -> Value(doubleValue = value.toDouble())
        is DocumentField.TimestampValue -> Value(timestampValue = value.toFormattedTimestamp())
        is DocumentField.BytesValue -> Value(bytesValue = value)
        is DocumentField.ReferenceValue -> Value(referenceValue = value)
        is DocumentField.GeoPointValue -> Value(geoPointValue = LatLng(latitude.toDouble(), longitude.toDouble()))
        is DocumentField.MapValue -> Value(mapValue = MapValue(getValueMap()))
        is DocumentField.ArrayValue -> Value(arrayValue = ArrayValue(getValueList()))
        else -> Value()
    }
}

private fun DocumentField.ArrayValue.getValueList(): List<Value> {
    val list = mutableListOf<Value>()
    this.values.forEach {
        list.add(it.getValue())
    }
    return list
}

private fun DocumentField.MapValue.getValueMap(): Map<String, Value> {
    val map = mutableMapOf<String, Value>()
    this.values.forEach {
        map[it.attributeName] = it.getValue()
    }
    return map
}

fun DocumentField.updateDocumentFieldErrorState(errorState: DocumentField.DocumentFieldErrorState?): DocumentField? {
    return when (this) {
        is DocumentField.StringValue -> this.copy(errorState = errorState)
        is DocumentField.IntegerValue -> this.copy(errorState = errorState)
        is DocumentField.MapValue -> this.copy(errorState = errorState)
        is DocumentField.ArrayValue -> this.copy(errorState = errorState)
        is DocumentField.DoubleValue -> this.copy(errorState = errorState)
        is DocumentField.GeoPointValue -> this.copy(errorState = errorState)
        is DocumentField.ReferenceValue -> this.copy(errorState = errorState)
        is DocumentField.BooleanValue -> this.copy(errorState = errorState)
        is DocumentField.BytesValue -> this.copy(errorState = errorState)
        is DocumentField.TimestampValue -> this.copy(errorState = errorState)
        else -> null
    }
}

fun DocumentField.checkValidation(): DocumentField? {
    return when (this) {
        is DocumentField.MapValue,
        is DocumentField.ArrayValue,
        is DocumentField.ReferenceValue,
        is DocumentField.BooleanValue,
        is DocumentField.BytesValue,
        is DocumentField.StringValue -> this
        is DocumentField.IntegerValue -> {
            val errorState = if (this.value.toIntOrNull() == null) {
                DocumentField.DocumentFieldErrorState.INTEGER_VALUE_INVALID
            } else null
            this.copy(errorState = errorState)
        }
        is DocumentField.GeoPointValue -> {
            val errorState = if (
                (this.latitude.toDoubleOrNull() ?: 90.1) > 90.0 ||
                (this.latitude.toDoubleOrNull() ?: -90.1) < -90.0
            ) {
                DocumentField.DocumentFieldErrorState.GEOPOINT_LATITUDE_VALUE_INVALID
            } else if (
                (this.longitude.toDoubleOrNull() ?: 180.1) > 180.0 ||
                (this.longitude.toDoubleOrNull() ?: -180.1) < 180.0
            ) {
                DocumentField.DocumentFieldErrorState.GEOPOINT_LONGITUDE_VALUE_INVALID
            } else null
            this.copy(errorState = errorState)
        }
        is DocumentField.DoubleValue -> {
            val errorState =
                if (this.value.toDoubleOrNull() == null || this.value.toDoubleOrNull()
                    ?.isFinite() != true
                ) {
                    DocumentField.DocumentFieldErrorState.DOUBLE_VALUE_INVALID
                } else null
            this.copy(errorState = errorState)
        }
        is DocumentField.TimestampValue -> {
            val errorState = this.value.timestampFormatValidation()
            this.copy(errorState = errorState)
        }
        else -> null
    }
}

fun createDocumentField(typeName: DocumentField.Type, attributeName: String = ""): DocumentField {
    return when (typeName) {
        DocumentField.Type.STRING -> DocumentField.StringValue(attributeName = attributeName)
        DocumentField.Type.INTEGER -> DocumentField.IntegerValue(attributeName = attributeName)
        DocumentField.Type.MAP -> DocumentField.MapValue(attributeName = attributeName)
        DocumentField.Type.ARRAY -> DocumentField.ArrayValue(attributeName = attributeName)
        DocumentField.Type.DOUBLE -> DocumentField.DoubleValue(attributeName = attributeName)
        DocumentField.Type.GEOPOINT -> DocumentField.GeoPointValue(attributeName = attributeName)
        DocumentField.Type.REFERENCE -> DocumentField.ReferenceValue(attributeName = attributeName)
        DocumentField.Type.BOOLEAN -> DocumentField.BooleanValue(attributeName = attributeName)
        DocumentField.Type.BYTES -> DocumentField.BytesValue(attributeName = attributeName)
        DocumentField.Type.TIMESTAMP -> DocumentField.TimestampValue(attributeName = attributeName)
    }
}

fun createDocumentField(attributeName: String, value: Value): DocumentField {
    lateinit var documentField: DocumentField
    value.run {
        booleanValue?.let { documentField = DocumentField.BooleanValue(attributeName, value = it) }
        integerValue?.let { documentField = DocumentField.IntegerValue(attributeName, value = it) }
        doubleValue?.let {
            documentField = DocumentField.DoubleValue(attributeName, value = it.toString())
        }
        stringValue?.let { documentField = DocumentField.StringValue(attributeName, value = it) }
        geoPointValue?.let {
            documentField = DocumentField.GeoPointValue(
                attributeName,
                latitude = it.latitude.toString() ?: "",
                longitude = it.longitude.toString() ?: ""
            )
        }
        bytesValue?.let { documentField = DocumentField.BytesValue(attributeName, value = it) }
        referenceValue?.let {
            documentField = DocumentField.ReferenceValue(attributeName, value = it)
        }
        timestampValue?.let {
            documentField = DocumentField.TimestampValue(
                attributeName,
                value = it.toTimestampFormat(),
                stringValue = it
            )
        }
        booleanValue?.let {
            documentField = DocumentField.BooleanValue(attributeName, value = it)
        }
        arrayValue?.let {
            val values = arrayValue.values?.map {
                createDocumentField("", it)
            } ?: listOf()
            documentField = DocumentField.ArrayValue(attributeName, values = values)
        }
        mapValue?.let {
            val values = mapValue.fields?.map { (attributeName, value) ->
                createDocumentField(attributeName, value)
            } ?: listOf()
            documentField = DocumentField.MapValue(attributeName, values = values)
        }
    }
    return documentField
}

fun generateFieldTypeList(isParentFieldExist: Boolean = false): List<DocumentField.Type> {
    val list = mutableListOf<DocumentField.Type>()
    list.add(DocumentField.Type.STRING)
    list.add(DocumentField.Type.INTEGER)
    list.add(DocumentField.Type.BOOLEAN)
    list.add(DocumentField.Type.DOUBLE)
    list.add(DocumentField.Type.TIMESTAMP)
    list.add(DocumentField.Type.GEOPOINT)
    list.add(DocumentField.Type.BYTES)
    list.add(DocumentField.Type.REFERENCE)
    if (!isParentFieldExist) {
        list.add(DocumentField.Type.ARRAY)
        list.add(DocumentField.Type.MAP)
    }
    return list
}

fun List<DocumentField>.toDocumentFieldMap(): Map<String, Value> {
    val map = mutableMapOf<String, Value>()
    forEach { field ->
        map[field.attributeName] = field.getValue()
    }
    return map
}

fun TimestampFormat.timestampFormatValidation(): DocumentField.DocumentFieldErrorState? {
    return if (this.year.toIntOrNull() !in 0..5000) {
        DocumentField.DocumentFieldErrorState.TIMESTAMP_YEAR_VALUE_INVALID
    } else if (this.month.toIntOrNull() !in 1..12) {
        DocumentField.DocumentFieldErrorState.TIMESTAMP_MONTH_VALUE_INVALID
    } else if (this.day.toIntOrNull() !in 1..this.getMonthDay()) {
        DocumentField.DocumentFieldErrorState.TIMESTAMP_DAY_VALUE_INVALID
    } else if (this.hour.toIntOrNull() !in 0 until 24) {
        DocumentField.DocumentFieldErrorState.TIMESTAMP_HOUR_VALUE_INVALID
    } else if (this.minute.toIntOrNull() !in 0 until 60) {
        DocumentField.DocumentFieldErrorState.TIMESTAMP_MINUTE_VALUE_INVALID
    } else if (this.second.toIntOrNull() !in 0 until 60) {
        DocumentField.DocumentFieldErrorState.TIMESTAMP_SECOND_VALUE_INVALID
    } else null
}

fun TimestampFormat.getMonthDay(): Int {
    return when (month.toInt()) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> getFebruaryMonthDay()
        else -> 30 // not necessary
    }
}

fun TimestampFormat.getFebruaryMonthDay(): Int {
    return this.year.toIntOrNull()?.let { if (it.isLeapYear()) 29 else 28 } ?: 28
}

fun Int.isLeapYear(): Boolean {
    return this % 4 == 0
}
