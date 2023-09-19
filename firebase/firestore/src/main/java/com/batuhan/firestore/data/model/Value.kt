package com.batuhan.firestore.data.model

import androidx.annotation.Keep
import com.batuhan.firestore.util.createDocumentField
import com.google.gson.annotations.SerializedName

@Keep
data class Value(
    @SerializedName("booleanValue") val booleanValue: Boolean? = null,
    @SerializedName("integerValue") val integerValue: String? = null,
    @SerializedName("doubleValue") val doubleValue: Double? = null,
    @SerializedName("timestampValue") val timestampValue: String? = null,
    @SerializedName("stringValue") val stringValue: String? = null,
    @SerializedName("bytesValue") val bytesValue: String? = null,
    @SerializedName("referenceValue") val referenceValue: String? = null,
    @SerializedName("geoPointValue") val geoPointValue: LatLng? = null,
    @SerializedName("arrayValue") val arrayValue: ArrayValue? = null,
    @SerializedName("mapValue") val mapValue: MapValue? = null
)

@Keep
data class LatLng(
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

@Keep
data class ArrayValue(
    @SerializedName("values") val values: List<Value>?
)

@Keep
data class MapValue(
    @SerializedName("fields") val fields: Map<String, Value>?
)

fun Map<String, Value>.toDocumentFieldList(): List<DocumentField> {
    val list = mutableListOf<DocumentField>()
    keys.sorted().forEach { attributeName ->
        this[attributeName]?.let { value ->
            list.add(createDocumentField(attributeName, value))
        }
    }
    return list
}
