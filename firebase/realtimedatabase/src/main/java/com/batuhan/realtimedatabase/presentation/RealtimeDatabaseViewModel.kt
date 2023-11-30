package com.batuhan.realtimedatabase.presentation

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.realtimedatabase.R
import com.batuhan.realtimedatabase.domain.GetDatabaseInstances
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
class RealtimeDatabaseViewModel @Inject constructor(
    private val getDatabaseInstances: GetDatabaseInstances,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _realtimeDatabaseEvent = Channel<RealtimeDatabaseEvent> { Channel.BUFFERED }
    val realtimeDatabaseEvent = _realtimeDatabaseEvent.receiveAsFlow()

    private val projectId = savedStateHandle.get<String>("projectId") ?: ""

    val databases =
        getDatabaseInstances.invoke(GetDatabaseInstances.Params(projectId)).cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(RealtimeDatabaseUiState())
    val uiState = _uiState.asStateFlow()

    fun onBackPressed() {
        viewModelScope.launch {
            _realtimeDatabaseEvent.send(RealtimeDatabaseEvent.Back)
        }
    }

    fun setErrorState(errorState: RealtimeDatabaseErrorState) {
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

    fun retryOperation(errorState: RealtimeDatabaseErrorState, operation: () -> Unit) {
        when (errorState) {
            RealtimeDatabaseErrorState.DATABASE_LIST -> {
                clearErrorState()
                operation.invoke()
            }
        }
    }

    fun navgiateToCreateDatabase(isFirst: Boolean) {
        viewModelScope.launch {
            _realtimeDatabaseEvent.send(RealtimeDatabaseEvent.CreateDatabase(isFirst, projectId))
        }
    }

    fun navigateToDatabaseScreen(databaseUrl: String) {
        viewModelScope.launch {
            _realtimeDatabaseEvent.send(RealtimeDatabaseEvent.Database(Uri.encode(databaseUrl)))
        }
    }

    fun setRefreshState(operation: () -> Unit) {
        viewModelScope.launch {
            clearErrorState()
            setRefreshingState(true)
            operation.invoke()
            delay(3000L)
            setRefreshingState(false)
        }
    }

    fun setRefreshingState(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }
}

data class RealtimeDatabaseUiState(
    val errorState: RealtimeDatabaseErrorState? = null,
    val isRefreshing: Boolean = false,
    val isSnackbarOpened: Boolean = false
)

sealed class RealtimeDatabaseEvent {

    data class CreateDatabase(val isFirst: Boolean, val projectId: String) : RealtimeDatabaseEvent()

    data class Database(val databaseUrl: String) : RealtimeDatabaseEvent()

    object Back : RealtimeDatabaseEvent()
}

enum class RealtimeDatabaseErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int?
) {
    DATABASE_LIST(R.string.error_occurred, R.string.retry)
}
