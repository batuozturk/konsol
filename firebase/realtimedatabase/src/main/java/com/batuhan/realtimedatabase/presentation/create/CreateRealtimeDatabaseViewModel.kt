package com.batuhan.realtimedatabase.presentation.create

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.batuhan.core.data.model.management.AvailableLocation
import com.batuhan.core.domain.management.GetAvailableLocations
import com.batuhan.core.util.Result
import com.batuhan.realtimedatabase.R
import com.batuhan.realtimedatabase.data.model.DatabaseInstance
import com.batuhan.realtimedatabase.data.model.DatabaseType
import com.batuhan.realtimedatabase.domain.CreateDatabaseInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRealtimeDatabaseViewModel @Inject constructor(
    private val createDatabaseInstance: CreateDatabaseInstance,
    private val getAvailableLocations: GetAvailableLocations,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _realtimeDatabaseEvent = Channel<CreateDatabaseEvent> { Channel.BUFFERED }
    val realtimeDatabaseEvent = _realtimeDatabaseEvent.receiveAsFlow()

    private val projectId = savedStateHandle.get<String>("projectId") ?: ""
    private val isFirst = savedStateHandle.get<Boolean>("isFirst") ?: false

    private val _uiState = MutableStateFlow(CreateDatabaseUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkFirstDatabase()
    }

    val locations = flowOf(
        PagingData.from(
            listOf(
                AvailableLocation(
                    "us-central1",
                    null,
                    null,
                    null
                ),
                AvailableLocation(
                    "europe-west1",
                    null,
                    null,
                    null
                ),
                AvailableLocation(
                    "asia-southeast1",
                    null,
                    null,
                    null
                )
            )
        )
    )

    fun checkFirstDatabase() {
        if (isFirst) {
            _uiState.update {
                it.copy(name = "$projectId-default-rtdb", isEditable = false)
            }
        }
    }

    fun createDatabase() {
        viewModelScope.launch {
            val locationId = uiState.value.selectedLocation ?: run {
                setErrorState(CreateDatabaseErrorState.LOCATION_NOT_SELECTED)
                return@launch
            }
            val name = uiState.value.name ?: run {
                setErrorState(CreateDatabaseErrorState.NAME_EMPTY)
                return@launch
            }
            setLoadingState(true)
            val result = createDatabaseInstance.invoke(
                CreateDatabaseInstance.Params(
                    projectId,
                    locationId,
                    databaseId = if (isFirst) "$projectId-default-rtdb" else name,
                    databaseInstance = DatabaseInstance(
                        type = DatabaseType.DEFAULT_DATABASE,
                        name = name
                    )
                )
            )
            when (result) {
                is Result.Success -> {
                    setLoadingState(false)
                    onBackPressed()
                }

                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateDatabaseErrorState.CREATE_DATABASE)
                }
            }
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _realtimeDatabaseEvent.send(CreateDatabaseEvent.Back)
        }
    }

    fun setRefreshingState(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    fun setErrorState(errorState: CreateDatabaseErrorState) {
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

    fun retryOperation(errorState: CreateDatabaseErrorState, operation: () -> Unit) {
        when (errorState) {
            CreateDatabaseErrorState.LOCATION_LIST -> {
                clearErrorState()
                operation.invoke()
            }

            CreateDatabaseErrorState.CREATE_DATABASE -> {
                clearErrorState()
                createDatabase()
            }

            else -> {}
        }
    }

    fun setName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun setLocationId(locationId: String) {
        _uiState.update {
            it.copy(selectedLocation = locationId)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }
}

data class CreateDatabaseUiState(
    val errorState: CreateDatabaseErrorState? = null,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val selectedLocation: String? = null,
    val name: String? = "",
    val isEditable: Boolean = true
)

sealed class CreateDatabaseEvent {

    data class Save(val databaseUrl: String) : CreateDatabaseEvent()

    object Back : CreateDatabaseEvent()
}

enum class CreateDatabaseErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int?
) {
    LOCATION_NOT_SELECTED(R.string.location_not_selected, null),
    NAME_EMPTY(R.string.name_is_empty, null),
    CREATE_DATABASE(R.string.error_occurred, R.string.retry),
    LOCATION_LIST(R.string.error_occurred, R.string.retry)
}
