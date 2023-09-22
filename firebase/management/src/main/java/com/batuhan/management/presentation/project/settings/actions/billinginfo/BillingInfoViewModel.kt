package com.batuhan.management.presentation.project.settings.actions.billinginfo

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.core.data.model.management.ProjectBillingInfo
import com.batuhan.core.data.model.management.UpdateBillingInfoRequest
import com.batuhan.management.domain.googlecloud.GetBillingAccounts
import com.batuhan.management.domain.googlecloud.GetBillingInfo
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
class BillingInfoViewModel @Inject constructor(
    private val getBillingInfo: GetBillingInfo,
    private val updateBillingInfo: UpdateBillingInfo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(BillingInfoUiState())
    val uiState = _uiState.asStateFlow()

    private val _billingInfoEvent = Channel<BillingInfoEvent> { Channel.BUFFERED }
    val billingInfoEvent = _billingInfoEvent.receiveAsFlow()

    init {
        getBillingInfo()
    }

    fun getBillingInfo() {
        setLoadingState(true)
        viewModelScope.launch {
            when (val result = getBillingInfo.invoke(GetBillingInfo.Params(projectId!!))) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            billingInfo = result.data
                        )
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setErrorState(BillingInfoErrorState.BILLING_INFO_ERROR)
                    setLoadingState(false)
                }
            }
        }
    }

    fun setErrorState(errorState: BillingInfoErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun removeBillingInfo() {
        setLoadingState(true)

        viewModelScope.launch {
            val updateBillingInfoRequest =
                UpdateBillingInfoRequest(billingAccountName = null)
            val result = updateBillingInfo.invoke(
                UpdateBillingInfo.Params(
                    projectId!!,
                    updateBillingInfoRequest
                )
            )
            when (result) {
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(BillingInfoErrorState.UPDATE_BILLING_INFO)
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
            _billingInfoEvent.send(BillingInfoEvent.Back)
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

    fun retryOperation(errorState: BillingInfoErrorState) {
        when (errorState) {
            BillingInfoErrorState.BILLING_INFO_ERROR -> {
                getBillingInfo()
            }
            BillingInfoErrorState.UPDATE_BILLING_INFO -> removeBillingInfo()
        }
    }

    fun addBillingInfo() {
        viewModelScope.launch {
            _billingInfoEvent.send(BillingInfoEvent.AddBillingInfo(projectId!!))
        }
    }
}

sealed class BillingInfoEvent {
    object Back : BillingInfoEvent()
    data class SaveChanges(val billingInfo: ProjectBillingInfo, val isEnabled: Boolean) :
        BillingInfoEvent()
    data class AddBillingInfo(val projectId: String) : BillingInfoEvent()
}

data class BillingInfoUiState(
    val errorState: BillingInfoErrorState? = null,
    val billingInfo: ProjectBillingInfo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isSnackbarOpened: Boolean = false
)

enum class BillingInfoErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    UPDATE_BILLING_INFO,
    BILLING_INFO_ERROR,
}
