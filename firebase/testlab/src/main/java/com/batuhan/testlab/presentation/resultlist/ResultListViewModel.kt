package com.batuhan.testlab.presentation.resultlist

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.testlab.R
import com.batuhan.testlab.domain.result.GetExecutionList
import com.batuhan.testlab.domain.result.GetHistoryList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultListViewModel @Inject constructor(
    private val getHistoryList: GetHistoryList,
    private val getExecutionList: GetExecutionList,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val historyId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val executionList = historyId.filterNotNull().flatMapLatest { historyId ->
        getExecutionList.invoke(GetExecutionList.Params(projectId!!, historyId))
            .cachedIn(viewModelScope)
    }

    private val _uiState = MutableStateFlow(ResultListUiState())
    val uiState = _uiState.asStateFlow()

    private val _resultListEvent = Channel<ResultListEvent> { Channel.BUFFERED }
    val resultListEvent = _resultListEvent.receiveAsFlow()

    init {
        getHistoryList() // todo on resume
    }

    fun getHistoryList() {
        viewModelScope.launch {
            val result = getHistoryList.invoke(GetHistoryList.Params(projectId!!))
            when (result) {
                is Result.Success -> {
                    result.data.histories?.let {
                        historyId.value = it[0].historyId
                    }
                }
                is Result.Error -> {
                    setErrorState(ResultListErrorState.HISTORY_ID)
                }
            }
        }
    }

    fun setErrorState(resultListErrorState: ResultListErrorState) {
        _uiState.update {
            it.copy(errorState = resultListErrorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun setRefreshingState(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    fun onRefresh(operation: () -> Unit) {
        viewModelScope.launch {
            setRefreshingState(true)
            operation.invoke()
            delay(3000L)
            setRefreshingState(false)
        }
    }

    fun retryOperation(
        errorState: ResultListErrorState,
        operation: (() -> Unit)?
    ) {
        when (errorState) {
            ResultListErrorState.RESULT_LIST -> {
                getHistoryList()
                operation?.invoke()
            }
            ResultListErrorState.HISTORY_ID -> {
                getHistoryList()
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
            _resultListEvent.send(ResultListEvent.Back)
        }
    }

    fun navigateToCreateMatrixScreen() {
        viewModelScope.launch {
            _resultListEvent.send(ResultListEvent.CreateMatrix(projectId!!))
        }
    }

    fun navigateToExecutionDetailScreen(executionId: String) {
        viewModelScope.launch {
            _resultListEvent.send(
                ResultListEvent.ExecutionDetail(
                    projectId!!,
                    historyId.value!!,
                    executionId
                )
            )
        }
    }
}

data class ResultListUiState(
    val errorState: ResultListErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val isRefreshing: Boolean = false

)

enum class ResultListErrorState(@StringRes val titleResId: Int, @StringRes val actionResId: Int?) {
    RESULT_LIST(R.string.error_occurred, R.string.retry),
    HISTORY_ID(R.string.error_occurred, R.string.retry),
}

sealed class ResultListEvent {
    object Back : ResultListEvent()
    data class CreateMatrix(val projectId: String) : ResultListEvent()
    data class ExecutionDetail(
        val projectId: String,
        val historyId: String,
        val executionId: String
    ) : ResultListEvent()
}
