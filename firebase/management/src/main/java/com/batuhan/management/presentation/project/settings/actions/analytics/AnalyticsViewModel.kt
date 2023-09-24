package com.batuhan.management.presentation.project.settings.actions.analytics

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.core.data.model.management.AnalyticsProperty
import com.batuhan.core.data.model.management.RemoveGoogleAnalyticsRequest
import com.batuhan.core.data.model.management.Status
import com.batuhan.management.domain.firebase.GetAnalyticsDetails
import com.batuhan.management.domain.firebase.GetFirebaseOperation
import com.batuhan.management.domain.firebase.RemoveGoogleAnalytics
import com.batuhan.management.presentation.project.settings.actions.androidapps.AndroidAppsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsDetails: GetAnalyticsDetails,
    private val removeGoogleAnalytics: RemoveGoogleAnalytics,
    private val getFirebaseOperation: GetFirebaseOperation,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId = savedStateHandle.get<String>(AndroidAppsViewModel.KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(AnalyticsInfoUiState())
    val uiState = _uiState.asStateFlow()

    private val _analyticsInfoEvent = Channel<AnalyticsInfoEvent> { Channel.BUFFERED }
    val analyticsInfoEvent = _analyticsInfoEvent.receiveAsFlow()

    fun getAnalyticsDetails() {
        viewModelScope.launch {
            setLoadingState(true)
            when (val result = getAnalyticsDetails.invoke(GetAnalyticsDetails.Params(projectId!!))) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(analyticsProperty = result.data.analyticsProperty)
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    if ((result.throwable as? HttpException)?.code() != 404) {
                        setErrorState(AnalyticsInfoErrorState.ANALYTICS_INFO)
                    }
                    setLoadingState(false)
                }
            }
        }
    }

    fun removeAnalyticsAccount() {
        viewModelScope.launch {
            removeGoogleAnalytics()
        }
    }

    fun addAnalyticsAccount() {
        viewModelScope.launch {
            _analyticsInfoEvent.send(AnalyticsInfoEvent.NavigateToAddAnalyticsAccount(projectId!!))
        }
    }

    suspend fun removeGoogleAnalytics() {
        setLoadingState(true)
        viewModelScope.launch {
            val result = removeGoogleAnalytics.invoke(
                RemoveGoogleAnalytics.Params(
                    projectId!!,
                    RemoveGoogleAnalyticsRequest(uiState.value.analyticsProperty?.id)
                )
            )
            when (result) {
                is Result.Success -> {
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(AnalyticsInfoErrorState.REMOVE_ANALYTICS)
                }
            }
        }
    }

    fun retryOperation(errorState: AnalyticsInfoErrorState) {
        when (errorState) {
            AnalyticsInfoErrorState.ANALYTICS_INFO -> {
                getAnalyticsDetails()
            }
            AnalyticsInfoErrorState.REMOVE_ANALYTICS -> removeAnalyticsAccount()
            else -> {
                // no-op
            }
        }
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
                    setErrorState(AnalyticsInfoErrorState.OPERATION)
                }
            }
            delay(3000)
        }
        return if (isDone && error == null) {
            true
        } else {
            setErrorState(AnalyticsInfoErrorState.OPERATION)
            false
        }
    }

    fun setErrorState(errorState: AnalyticsInfoErrorState) {
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
            _analyticsInfoEvent.send(AnalyticsInfoEvent.Back)
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
}

sealed class AnalyticsInfoEvent {
    object Back : AnalyticsInfoEvent()
    data class NavigateToAddAnalyticsAccount(val projectId: String) : AnalyticsInfoEvent()
}

data class AnalyticsInfoUiState(
    val errorState: AnalyticsInfoErrorState? = null,
    val isLoading: Boolean = true,
    val isSnackbarOpened: Boolean = false,
    val analyticsProperty: AnalyticsProperty? = null
)

enum class AnalyticsInfoErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {

    REMOVE_ANALYTICS,
    OPERATION,
    ANALYTICS_INFO,
}
