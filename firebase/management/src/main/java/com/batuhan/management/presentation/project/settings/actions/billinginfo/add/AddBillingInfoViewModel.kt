package com.batuhan.management.presentation.project.settings.actions.billinginfo.add

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.management.data.model.BillingAccount
import com.batuhan.management.data.model.UpdateBillingInfoRequest
import com.batuhan.management.domain.googlecloud.GetBillingAccounts
import com.batuhan.management.domain.googlecloud.UpdateBillingInfo
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
class AddBillingInfoViewModel @Inject constructor(
    private val getBillingAccounts: GetBillingAccounts,
    private val updateBillingInfo: UpdateBillingInfo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    val billingAccounts = getBillingAccounts.invoke().cachedIn(viewModelScope)

    val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _addBillingInfoEvent = Channel<AddBillingInfoEvent> { Channel.BUFFERED }
    val addBillingInfoEvent = _addBillingInfoEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow(AddBillingInfoUiState())
    val uiState = _uiState.asStateFlow()

    fun addBillingInfo() {
        val billingName = uiState.value.selectedBillingAccount?.name ?: run {
            setErrorState(AddBillingInfoErrorState.BILLING_ACCOUNT_NOT_SELECTED)
            return
        }
        setLoadingState(true)

        viewModelScope.launch {
            val updateBillingInfoRequest =
                UpdateBillingInfoRequest(billingAccountName = billingName)
            val result = updateBillingInfo.invoke(
                UpdateBillingInfo.Params(
                    projectId!!,
                    updateBillingInfoRequest
                )
            )
            when (result) {
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(AddBillingInfoErrorState.UPDATE_BILLING_INFO)
                }
                is Result.Success -> {
                    setLoadingState(false)
                    setSuccessState(true)
                    delay(3000L)
                    setSuccessState(false)
                }
            }
        }
    }

    fun retryOperation(errorState: AddBillingInfoErrorState, operation: (() -> Unit)?) {
        clearErrorState()
        when (errorState) {
            AddBillingInfoErrorState.BILLING_ACCOUNTS -> {
                operation?.invoke()
            }
            AddBillingInfoErrorState.UPDATE_BILLING_INFO -> {
                addBillingInfo()
            }
            else -> {
                // no-op
            }
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun setSuccessState(isSuccess: Boolean) {
        _uiState.update {
            it.copy(isSuccess = isSuccess)
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _addBillingInfoEvent.send(AddBillingInfoEvent.Back)
        }
    }

    fun setErrorState(errorState: AddBillingInfoErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun selectBillingAccount(billingAccount: BillingAccount) {
        _uiState.update {
            it.copy(selectedBillingAccount = billingAccount)
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

data class AddBillingInfoUiState(
    val errorState: AddBillingInfoErrorState? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val selectedBillingAccount: BillingAccount? = null,
    val isSnackbarOpened: Boolean = false
)

sealed class AddBillingInfoEvent {
    object Back : AddBillingInfoEvent()
}

enum class AddBillingInfoErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    BILLING_ACCOUNT_NOT_SELECTED(R.string.billing_account_not_selected, null),
    UPDATE_BILLING_INFO,
    BILLING_ACCOUNTS
}
