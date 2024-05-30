package com.batuhan.testlab.presentation.execution.environment

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.Result
import com.batuhan.testlab.R
import com.batuhan.testlab.data.model.execution.ExecutionEnvironment
import com.batuhan.testlab.domain.result.GetEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnvironmentViewModel @Inject constructor(
    private val getEnvironment: GetEnvironment,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        private const val KEY_EXECUTION_ID = "executionId"
        private const val KEY_HISTORY_ID = "historyId"
        private const val KEY_ENVIRONMENT_ID = "environmentId"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)
    private val executionId = savedStateHandle.get<String>(KEY_EXECUTION_ID)
    private val historyId = savedStateHandle.get<String>(KEY_HISTORY_ID)
    private val environmentId = savedStateHandle.get<String>(KEY_ENVIRONMENT_ID)

    private val _uiState = MutableStateFlow(EnvironmentUiState())
    val uiState = _uiState.asStateFlow()

    private val _environmentEvent = Channel<EnvironmentEvent> { Channel.BUFFERED }
    val environmentEvent = _environmentEvent.receiveAsFlow()

    fun getEnvironment() {
        setLoadingState(true)
        viewModelScope.launch {
            val result =
                getEnvironment.invoke(
                    GetEnvironment.Params(
                        projectId!!,
                        historyId!!,
                        executionId!!,
                        environmentId!!
                    )
                )
            when (result) {
                is Result.Success -> {
                    result.data.let { environment ->
                        _uiState.update {
                            it.copy(environment = environment)
                        }
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(EnvironmentErrorState.GET_ENVIRONMENT)
                }
            }
        }
    }

    fun setErrorState(environmentErrorState: EnvironmentErrorState) {
        _uiState.update {
            it.copy(errorState = environmentErrorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun retryOperation(
        errorState: EnvironmentErrorState,
        operation: (() -> Unit)?
    ) {
        when (errorState) {
            EnvironmentErrorState.GET_ENVIRONMENT -> {
                getEnvironment()
            }
        }
    }

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) clearErrorState()
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _environmentEvent.send(EnvironmentEvent.Back)
        }
    }
}

data class EnvironmentUiState(
    val errorState: EnvironmentErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val isLoading: Boolean = true,
    val environment: ExecutionEnvironment? = null

)

enum class EnvironmentErrorState(@StringRes val titleResId: Int, @StringRes val actionResId: Int?) {
    GET_ENVIRONMENT(R.string.error_occurred, R.string.retry)
}

sealed class EnvironmentEvent {
    object Back : EnvironmentEvent()
}
