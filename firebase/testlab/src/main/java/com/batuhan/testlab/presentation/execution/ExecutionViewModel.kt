package com.batuhan.testlab.presentation.execution

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.testlab.R
import com.batuhan.testlab.data.model.execution.Execution
import com.batuhan.testlab.domain.result.GetEnvironmentList
import com.batuhan.testlab.domain.result.GetExecution
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExecutionViewModel @Inject constructor(
    private val getExecution: GetExecution,
    private val getEnvironmentList: GetEnvironmentList,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        private const val KEY_EXECUTION_ID = "executionId"
        private const val KEY_HISTORY_ID = "historyId"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)
    private val executionId = savedStateHandle.get<String>(KEY_EXECUTION_ID)
    private val historyId = savedStateHandle.get<String>(KEY_HISTORY_ID)

    val environments = getEnvironmentList.invoke(
        GetEnvironmentList.Params(
            projectId!!,
            historyId!!,
            executionId!!
        )
    ).cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(ExecutionUiState())
    val uiState = _uiState.asStateFlow()

    private val _executionEvent = Channel<ExecutionEvent> { Channel.BUFFERED }
    val executionEvent = _executionEvent.receiveAsFlow()

    init {
        getExecution()
    }

    fun getExecution() {
        setLoadingState(true)
        viewModelScope.launch {
            val result =
                getExecution.invoke(GetExecution.Params(projectId!!, historyId!!, executionId!!))
            when (result) {
                is Result.Success -> {
                    result.data.let { testExecution ->
                        _uiState.update {
                            it.copy(execution = testExecution)
                        }
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(ExecutionErrorState.GET_EXECUTION)
                }
            }
        }
    }

    fun setErrorState(executionErrorState: ExecutionErrorState) {
        _uiState.update {
            it.copy(errorState = executionErrorState)
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
        errorState: ExecutionErrorState,
        operation: (() -> Unit)?
    ) {
        when (errorState) {
            ExecutionErrorState.GET_EXECUTION -> {
                getExecution()
            }
            ExecutionErrorState.GET_ENVIRONMENT_LIST -> {
                operation?.invoke()
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
            _executionEvent.send(ExecutionEvent.Back)
        }
    }

    fun navigateToEnvironmentScreen(environmentId: String) {
        viewModelScope.launch {
            _executionEvent.send(
                ExecutionEvent.NavigateToEnvironmentScreen(
                    projectId!!,
                    historyId!!,
                    executionId!!,
                    environmentId
                )
            )
        }
    }
}

data class ExecutionUiState(
    val errorState: ExecutionErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val isLoading: Boolean = false,
    val execution: Execution? = null
)

enum class ExecutionErrorState(@StringRes val titleResId: Int, @StringRes val actionResId: Int?) {
    GET_EXECUTION(R.string.error_occurred, R.string.retry),
    GET_ENVIRONMENT_LIST(R.string.error_occurred, R.string.retry),
}

sealed class ExecutionEvent {
    object Back : ExecutionEvent()
    data class NavigateToEnvironmentScreen(
        val projectId: String,
        val historyId: String,
        val executionId: String,
        val environmentId: String
    ) : ExecutionEvent()
}
