package com.batuhan.firestore.data.model

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.batuhan.firestore.R

open class DocumentField(
    open val attributeName: String,
    open val fieldType: Type,
    open val errorState: DocumentFieldErrorState?
) {
    @Keep
    data class StringValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.STRING,
        override val errorState: DocumentFieldErrorState? = null,
        val value: String = ""
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class BooleanValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.BOOLEAN,
        override val errorState: DocumentFieldErrorState? = null,
        val value: Boolean = false
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class IntegerValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.INTEGER,
        override val errorState: DocumentFieldErrorState? = null,
        val value: String = "0"
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class DoubleValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.DOUBLE,
        override val errorState: DocumentFieldErrorState? = null,
        val value: String = ""
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class TimestampValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.TIMESTAMP,
        override val errorState: DocumentFieldErrorState? = null,
        val value: TimestampFormat = TimestampFormat(),
        val stringValue: String = ""
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class BytesValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.BYTES,
        override val errorState: DocumentFieldErrorState? = null,
        val value: String = ""
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class ReferenceValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.REFERENCE,
        override val errorState: DocumentFieldErrorState? = null,
        val value: String = ""
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class GeoPointValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.GEOPOINT,
        override val errorState: DocumentFieldErrorState? = null,
        val latitude: String = "",
        val longitude: String = ""
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class ArrayValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.ARRAY,
        override val errorState: DocumentFieldErrorState? = null,
        val values: List<DocumentField> = listOf()
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    data class MapValue(
        override val attributeName: String = "",
        override val fieldType: Type = Type.MAP,
        override val errorState: DocumentFieldErrorState? = null,
        val values: List<DocumentField> = listOf()
    ) : DocumentField(attributeName, fieldType, errorState)

    @Keep
    enum class Type(@StringRes val typeResId: Int) {
        STRING(R.string.document_string_type),
        INTEGER(R.string.document_integer_type),
        MAP(R.string.document_map_type),
        ARRAY(R.string.document_array_type),
        DOUBLE(R.string.document_double_type),
        GEOPOINT(R.string.document_geopoint_type),
        REFERENCE(R.string.document_reference_type),
        BOOLEAN(R.string.document_boolean_type),
        BYTES(R.string.document_bytes_type),
        TIMESTAMP(R.string.document_timestamp_type);
    }
    enum class DocumentFieldErrorState(@StringRes val errorTitleResId: Int) {
        ATTRIBUTE_NAME_EMPTY(R.string.attribute_name_empty),
        ATTRIBUTE_NAME_INVALID(R.string.attribute_name_invalid),
        INTEGER_VALUE_INVALID(R.string.integer_value_invalid),
        DOUBLE_VALUE_INVALID(R.string.double_value_invalid),
        GEOPOINT_LATITUDE_VALUE_INVALID(R.string.latitude_value_invalid),
        GEOPOINT_LONGITUDE_VALUE_INVALID(R.string.longitude_value_invalid),
        TIMESTAMP_YEAR_VALUE_INVALID(R.string.timestamp_year_value_invalid),
        TIMESTAMP_MONTH_VALUE_INVALID(R.string.timestamp_month_value_invalid),
        TIMESTAMP_DAY_VALUE_INVALID(R.string.timestamp_day_value_invalid),
        TIMESTAMP_HOUR_VALUE_INVALID(R.string.timestamp_hour_value_invalid),
        TIMESTAMP_MINUTE_VALUE_INVALID(R.string.timestamp_minute_value_invalid),
        TIMESTAMP_SECOND_VALUE_INVALID(R.string.timestamp_second_value_invalid);
    }
}