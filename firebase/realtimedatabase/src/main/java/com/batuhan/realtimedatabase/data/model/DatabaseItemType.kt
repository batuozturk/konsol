package com.batuhan.realtimedatabase.data.model

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.batuhan.realtimedatabase.R

@Keep
enum class DatabaseItemType(@StringRes val titleResId: Int) {
    OBJECT(R.string.type_object),
    STRING(R.string.type_string),
    INTEGER(R.string.type_integer),
    BOOLEAN(R.string.type_boolean)
}
