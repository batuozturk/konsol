package com.batuhan.management.presentation.project.settings.actions.projectdetail.selectlocation

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.management.data.model.Status
import com.batuhan.management.domain.firebase.FinalizeLocation
import com.batuhan.management.domain.firebase.GetAvailableLocations
import com.batuhan.management.domain.firebase.GetFirebaseOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectLocationViewModel @Inject constructor(
    private val finalizeLocation: FinalizeLocation,
    private val getFirebaseOperation: GetFirebaseOperation,
    private val getAvailableLocations: GetAvailableLocations,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(SelectLocationUiState())
    val uiState = _uiState.asStateFlow()

    private val _updateProjectEvent = Channel<SelectLocationEvent> { Channel.BUFFERED }
    val selectLocationEvent = _updateProjectEvent.receiveAsFlow()

    val availableLocations =
        getAvailableLocations.invoke(GetAvailableLocations.Params("projects/" + projectId!!))
            .cachedIn(viewModelScope)

    suspend fun executeOperation(operation: suspend (String) -> String?): Boolean {
        val operationId = operation.invoke(uiState.value.selectedLocationId ?: return false)
        operationId ?: return false
        return getFirebaseOperation(operationId)
    }

    suspend fun getFirebaseOperation(operationId: String): Boolean {
        var isDone = false
        var error: Status? = null
        while (error == null && !isDone) {
            val result =
                getFirebaseOperation.invoke(GetFirebaseOperation.Params(operationId))
            when (result) {
                is Result.Success -> {
                    isDone = result.data.done ?: false
                    error = result.data.error
                }
                is Result.Error -> {
                    setErrorState(SelectLocationErrorState.OPERATION)
                }
            }
            delay(3000)
        }
        return if (isDone && error == null) {
            true
        } else {
            setErrorState(SelectLocationErrorState.OPERATION)
            false
        }
    }

    suspend fun setLocation(locationId: String): String? {
        setLoadingState(true)
        return when (
            val result =
                finalizeLocation.invoke(FinalizeLocation.Params("projects/${projectId!!}", locationId))
        ) {
            is Result.Success -> {
                setLoadingState(false)
                result.data.name
            }
            is Result.Error -> {
                setLoadingState(false)
                setErrorState(SelectLocationErrorState.FINALIZE_LOCATION)
                null
            }
        }
    }

    fun saveChanges() {
        if (uiState.value.selectedLocationId == null) {
            setErrorState(SelectLocationErrorState.LOCATION_NOT_SELECTED)
            return
        }

        viewModelScope.launch {
            executeOperation(::setLocation)
        }
    }

    fun setErrorState(errorState: SelectLocationErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _updateProjectEvent.send(SelectLocationEvent.Back)
        }
    }

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) {
            clearErrorState()
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun retryOperation(errorState: SelectLocationErrorState, operation: (() -> Unit)?) {
        when (errorState) {
            SelectLocationErrorState.AVAILABLE_LOCATIONS -> {
                operation?.invoke()
            }
            SelectLocationErrorState.FINALIZE_LOCATION -> {
                viewModelScope.launch {
                    executeOperation(::setLocation)
                }
            }
            else -> {
                // no-op
            }
        }
    }

    fun setSelectedLocationInfoId(locationId: String?) {
        _uiState.update {
            it.copy(selectedLocationInfoId = locationId)
        }
    }

    fun setSelectedLocationId(locationId: String?) {
        _uiState.update {
            it.copy(selectedLocationId = locationId)
        }
    }
}

sealed class SelectLocationEvent {
    object Back : SelectLocationEvent()
}

data class SelectLocationUiState(
    val errorState: SelectLocationErrorState? = null,
    val projectAttributes: List<Pair<Int, String>>? = null,
    val isLoading: Boolean = false,
    val selectedLocationId: String? = null,
    val isSnackbarOpened: Boolean = false,
    val selectedLocationInfoId: String? = null
)

enum class SelectLocationErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    FINALIZE_LOCATION,
    AVAILABLE_LOCATIONS,
    OPERATION(actionResId = null),
    LOCATION_NOT_SELECTED(R.string.location_not_selected, null)
}
