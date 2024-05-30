package com.batuhan.realtimedatabase.presentation.databaseitem

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.Result
import com.batuhan.realtimedatabase.R
import com.batuhan.realtimedatabase.data.model.DatabaseItemType
import com.batuhan.realtimedatabase.domain.DeleteData
import com.batuhan.realtimedatabase.domain.GetDatabase
import com.batuhan.realtimedatabase.domain.PatchData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseItemViewModel @Inject constructor(
    private val getDatabase: GetDatabase,
    private val deleteData: DeleteData,
    private val patchData: PatchData,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _realtimeDatabaseEvent = Channel<DatabaseItemEvent> { Channel.BUFFERED }
    val realtimeDatabaseEvent = _realtimeDatabaseEvent.receiveAsFlow()

    private val databaseUrl = (Uri.decode(savedStateHandle.get<String>("databaseUrl")) + "/") ?: ""

    private val databasePath =
        MutableStateFlow(DatabasePath(databaseUrl, System.currentTimeMillis()))

    @OptIn(ExperimentalCoroutinesApi::class)
    val database = databasePath.flatMapLatest { path ->
        _uiState.update {
            it.copy(path = path.path)
        }
        flowOf(getDatabase(path.path))
    }

    private val _uiState = MutableStateFlow(DatabaseItemUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun getDatabase(databaseUrl: String): Map<String, JsonElement?>? {
        setLoadingState(true)
        val result = getDatabase.invoke(GetDatabase.Params("$databaseUrl.json"))
        return when (result) {
            is Result.Success -> {
                setLoadingState(false)
                result.data
            }

            is Result.Error -> {
                setLoadingState(false)
                setErrorState(DatabaseItemErrorState.GET_DATA)
                null
            }
        }
    }

    fun onBackPressed() {
        if (databasePath.value.path == databaseUrl) {
            viewModelScope.launch {
                _realtimeDatabaseEvent.send(DatabaseItemEvent.Back)
            }
        } else if (databasePath.value.path.substring(databaseUrl.length + 1)
            .count { it == '/' } == 0
        ) {
            databasePath.value = DatabasePath(databaseUrl, System.currentTimeMillis())
        } else {
            databasePath.value = DatabasePath(
                databasePath.value.path.substringBeforeLast("/"),
                System.currentTimeMillis()
            )
        }
    }

    fun setErrorState(errorState: DatabaseItemErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) clearErrorState()
    }

    fun retryOperation(errorState: DatabaseItemErrorState) {
        when (errorState) {
            DatabaseItemErrorState.GET_DATA -> {
                clearErrorState()
                val path = databasePath.value.path
                databasePath.value = DatabasePath(path, System.currentTimeMillis())
            }

            DatabaseItemErrorState.DELETE_DATA -> {
                clearErrorState()
                onDeleteClicked(uiState.value.deletedKey!!)
            }

            DatabaseItemErrorState.PATCH_DATA -> {
                clearErrorState()
                patchData()
            }

            else -> {}
        }
    }

    fun setPath(key: String) {
        databasePath.value =
            DatabasePath(
                databasePath.value.path.dropLastWhile { it == '/' } + "/$key",
                System.currentTimeMillis()
            )
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onDeleteClicked(key: String) {
        val path = databasePath.value.path
        val deletedPath = databasePath.value.path + "/$key.json"
        _uiState.update {
            it.copy(deletedKey = key)
        }
        setLoadingState(true)
        viewModelScope.launch {
            val result = deleteData.invoke(DeleteData.Params(deletedPath))
            when (result) {
                is Result.Success -> {
                    setLoadingState(false)
                    _uiState.update {
                        it.copy(deletedKey = null)
                    }
                    databasePath.value = DatabasePath(path, System.currentTimeMillis())
                }

                is Result.Error -> {
                    setErrorState(DatabaseItemErrorState.DELETE_DATA)
                    setLoadingState(false)
                }
            }
        }
    }

    fun patchData() {
        val path = databasePath.value.path
        val key = uiState.value.editingKey
        val value = uiState.value.editingValue
        val editingType = uiState.value.selectedType
        val putPath = "$path/.json"
        viewModelScope.launch {
            val jsonObject = JsonObject()
            if (editingType == DatabaseItemType.OBJECT.name) {
                jsonObject.add(
                    "$key",
                    JsonObject().apply {
                        addProperty("konsol", "required value for JsonObject")
                    }
                )
            } else {
                when (editingType) {
                    DatabaseItemType.STRING.name -> jsonObject.addProperty("$key", value)
                    DatabaseItemType.INTEGER.name -> {
                        if (value?.toIntOrNull() != null) {
                            jsonObject.addProperty(
                                "$key",
                                value.toInt()
                            )
                        } else if (value?.toDoubleOrNull() != null && value.toDoubleOrNull()
                            ?.isFinite() == true
                        ) {
                            jsonObject.addProperty(
                                "$key",
                                value.toDouble()
                            )
                        }
                    }

                    DatabaseItemType.BOOLEAN.name -> {
                        if (value?.toBoolean() != null) {
                            jsonObject.addProperty(
                                "$key",
                                value.toBoolean()
                            )
                        }
                    }

                    else -> return@launch
                }
            }
            setLoadingState(true)
            when (patchData.invoke(PatchData.Params(putPath, jsonObject))) {
                is Result.Success -> {
                    setLoadingState(false)
                    clearErrorState()
                    clearBottomSheetVariables()
                    databasePath.value = DatabasePath(path, System.currentTimeMillis())
                }

                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(DatabaseItemErrorState.PATCH_DATA)
                }
            }
        }
    }

    fun setBottomSheetState(isBottomSheetOpened: Boolean) {
        _uiState.update {
            it.copy(
                isBottomSheetOpened = isBottomSheetOpened
            )
        }
    }

    fun clearBottomSheetVariables() {
        _uiState.update {
            it.copy(
                isEditing = false,
                editingKey = null,
                editingValue = null,
                selectedType = DatabaseItemType.STRING.name
            )
        }
    }

    fun updateEditedKey(key: String) {
        _uiState.update {
            it.copy(editingKey = key)
        }
    }

    fun updateEditedValue(value: String) {
        val editingType = uiState.value.selectedType
        when (editingType) {
            DatabaseItemType.INTEGER.name -> {
                val integerConditions = value.toIntOrNull() != null && value.isNotEmpty()
                val doubleConditions = value.toDoubleOrNull() != null && value.toDoubleOrNull()
                    ?.isFinite() == true && value.isNotEmpty()
                if (!integerConditions && !doubleConditions) {
                    setErrorState(DatabaseItemErrorState.VALUE_INVALID)
                } else {
                    clearErrorState()
                }
            }

            DatabaseItemType.BOOLEAN.name -> {
                if (value.toBooleanStrictOrNull() == null) {
                    setErrorState(DatabaseItemErrorState.VALUE_INVALID)
                } else {
                    clearErrorState()
                }
            }

            else -> {
                clearErrorState()
            }
        }
        _uiState.update {
            it.copy(editingValue = value)
        }
    }

    fun updateEditedType(type: String) {
        _uiState.update {
            it.copy(selectedType = type, editingValue = null)
        }
        clearErrorState()
    }

    fun initPatchData(key: String, jsonValue: JsonElement?) {
        jsonValue ?: return
        clearErrorState()
        val value = if (jsonValue.isJsonObject) null else jsonValue.asString
        val type = when {
            jsonValue.isJsonObject -> DatabaseItemType.OBJECT
            jsonValue.isJsonPrimitive -> {
                jsonPrimitiveType(jsonValue)
            }

            else -> null
        }
        type ?: return
        _uiState.update {
            it.copy(
                editingKey = key,
                editingValue = value,
                selectedType = type.name,
                isEditing = true
            )
        }
    }

    fun setPatchDataWithInitial() {
        _uiState.update {
            it.copy(
                editingKey = null,
                editingValue = null,
                selectedType = DatabaseItemType.STRING.name,
                isEditing = false
            )
        }
    }

    fun jsonPrimitiveType(jsonValue: JsonElement): DatabaseItemType? {
        val jsonPrimitive = jsonValue.asJsonPrimitive
        return when {
            jsonPrimitive.isNumber -> DatabaseItemType.INTEGER
            jsonPrimitive.isString -> DatabaseItemType.STRING
            jsonPrimitive.isBoolean -> DatabaseItemType.BOOLEAN
            else -> null
        }
    }
}

data class DatabasePath(
    val path: String,
    val updateTimestamp: Long
)

data class DatabaseItemUiState(
    val errorState: DatabaseItemErrorState? = null,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val path: String? = "",
    val isBottomSheetOpened: Boolean = false,
    val isEditing: Boolean = false,
    val selectedType: String = DatabaseItemType.STRING.name,
    val editingKey: String? = null,
    val editingValue: String? = null,
    val deletedKey:String? = null
)

sealed class DatabaseItemEvent {

    object Back : DatabaseItemEvent()
}

enum class DatabaseItemErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int?
) {
    LOCATION_NOT_SELECTED(R.string.location_not_selected, null),
    KEY_EMPTY(R.string.name_is_empty, null),
    VALUE_INVALID(R.string.value_invalid, null),
    PATCH_DATA(R.string.error_occurred, R.string.retry),
    DELETE_DATA(R.string.error_occurred, R.string.retry),
    GET_DATA(R.string.error_occurred, R.string.retry),
}
