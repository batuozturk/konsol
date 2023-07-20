package com.batuhan.management.presentation.project.settings.actions.analytics.add

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.management.data.model.AnalyticsAccount
import com.batuhan.management.data.model.Status
import com.batuhan.management.domain.firebase.AddGoogleAnalytics
import com.batuhan.management.domain.firebase.GetFirebaseOperation
import com.batuhan.management.domain.googleanalytics.GetGoogleAnalyticsAccounts
import com.batuhan.management.presentation.project.settings.actions.androidapps.AndroidAppsViewModel
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
class AddAnalyticsAccountViewModel @Inject constructor(
    private val addGoogleAnalytics: AddGoogleAnalytics,
    private val getGoogleAnalyticsAccounts: GetGoogleAnalyticsAccounts,
    private val getFirebaseOperation: GetFirebaseOperation,

    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId: String =
        savedStateHandle.get<String>(AndroidAppsViewModel.KEY_PROJECT_ID) ?: ""

    private val _uiState = MutableStateFlow(AddAnalyticsAccountUiState())
    val uiState = _uiState.asStateFlow()

    private val _analyticsAccounts = MutableStateFlow<List<AnalyticsAccount>>(listOf())
    val analyticsAccounts = _analyticsAccounts.asStateFlow()

    private val _addAnalyticsAccountEvent = Channel<AddAnalyticsAccountEvent> { Channel.BUFFERED }
    val addAnalyticsAccountEvent = _addAnalyticsAccountEvent.receiveAsFlow()

    init {
        getGoogleAnalyticsAccounts()
    }

    fun getGoogleAnalyticsAccounts() {
        setLoadingState(true)
        viewModelScope.launch {
            when (val result = getGoogleAnalyticsAccounts.invoke()) {
                is Result.Success -> {
                    result.data.items?.let {
                        _analyticsAccounts.value = it
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(AddAnalyticsAccountErrorState.ANALYTICS_ACCOUNTS)
                }
            }
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            setLoadingState(true)
            executeFirebaseOperation(::addAnalytics)
            setLoadingState(false)
        }
    }

    suspend fun addAnalytics(): String? {
        val analyticsAccountId = uiState.value.selectedAnalyticsAccount?.id ?: run {
            setErrorState(AddAnalyticsAccountErrorState.ANALYTICS_ACCOUNT_NOT_SELECTED)
            return null
        }
        return when (
            val result = addGoogleAnalytics.invoke(
                AddGoogleAnalytics.Params(
                    analyticsAccountId,
                    "projects/$projectId"
                )
            )
        ) {
            is Result.Success -> {
                result.data.name
            }
            is Result.Error -> {
                setErrorState(AddAnalyticsAccountErrorState.ADD_ANALYTICS)
                null
            }
        }
    }

    suspend fun executeFirebaseOperation(
        operation: suspend () -> String?
    ) {
        val operationId = operation.invoke()
        operationId ?: return
        getFirebaseOperation(operationId)
    }

    fun retryOperation(errorState: AddAnalyticsAccountErrorState) {
        when (errorState) {
            AddAnalyticsAccountErrorState.ANALYTICS_ACCOUNTS -> getGoogleAnalyticsAccounts()
            AddAnalyticsAccountErrorState.ADD_ANALYTICS -> saveChanges()
            else -> {
                // no-op
            }
        }
    }

    suspend fun getFirebaseOperation(operationId: String) {
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
                    setErrorState(AddAnalyticsAccountErrorState.OPERATION)
                }
            }
            delay(3000)
        }
        if (isDone && error != null) {
            setErrorState(AddAnalyticsAccountErrorState.OPERATION)
        }
    }

    fun setErrorState(errorState: AddAnalyticsAccountErrorState) {
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
            _addAnalyticsAccountEvent.send(AddAnalyticsAccountEvent.Back)
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

    fun selectAnalyticsAccount(analyticsAccount: AnalyticsAccount) {
        _uiState.update {
            it.copy(selectedAnalyticsAccount = analyticsAccount)
        }
    }
}

data class AddAnalyticsAccountUiState(
    val errorState: AddAnalyticsAccountErrorState? = null,
    val isLoading: Boolean = false,
    val selectedAnalyticsAccount: AnalyticsAccount? = null,
    val isSnackbarOpened: Boolean = false
)

enum class AddAnalyticsAccountErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    ANALYTICS_ACCOUNTS,
    ADD_ANALYTICS,
    OPERATION,
    ANALYTICS_ACCOUNT_NOT_SELECTED(
        titleResId = R.string.analytics_account_not_selected,
        actionResId = null
    )
}

sealed class AddAnalyticsAccountEvent {
    object Back : AddAnalyticsAccountEvent()
}
