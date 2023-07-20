package com.batuhan.management.presentation.project.settings.actions.iosapps

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.management.data.model.IosApp
import com.batuhan.management.domain.firebase.GetIosApps
import com.batuhan.management.domain.firebase.GetIosConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IosAppsViewModel @Inject constructor(
    private val getIosApps: GetIosApps,
    private val getIosConfig: GetIosConfig,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId: String = savedStateHandle.get<String>(KEY_PROJECT_ID) ?: ""
    val apps =
        getIosApps.invoke(GetIosApps.Params(projectId = projectId)).cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(IosAppsUiState())
    val uiState = _uiState.asStateFlow()

    private val _iosAppsEvent = Channel<IosAppsEvent> { Channel.BUFFERED }
    val iosAppsEvent = _iosAppsEvent.receiveAsFlow()

    fun onBackPressed() {
        viewModelScope.launch {
            _iosAppsEvent.send(IosAppsEvent.Back)
        }
    }

    fun createIosApp() {
        viewModelScope.launch {
            _iosAppsEvent.send(IosAppsEvent.CreateIosApp(projectId))
        }
    }

    fun onInfoClicked(iosApp: IosApp) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedIosApp = iosApp)
            }
            _iosAppsEvent.send(IosAppsEvent.IosAppInfo(iosApp))
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

    fun getConfigFile(contentResolver: ContentResolver, uri: Uri, appId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            setLoadingState(true)
            val stream = contentResolver.openOutputStream(uri)
            when (val result = getIosConfig.invoke(GetIosConfig.Params(projectId, appId))) {
                is Result.Success -> {
                    val configFilenameContents = result.data.configFileContents ?: return@launch
                    val decodedConfigFileContent =
                        Base64.decode(configFilenameContents, Base64.DEFAULT)
                    stream?.write(decodedConfigFileContent)
                    stream?.close()
                    setLoadingState(false)
                    setFileSaved()
                }
                is Result.Error -> {
                    stream?.close()
                    setLoadingState(false)
                    setErrorState(IosAppsErrorState.IOS_CONFIG)
                }
            }
        }
    }

    fun onConfigFileClicked() {
        viewModelScope.launch {
            _iosAppsEvent.send(
                IosAppsEvent.SaveConfigFileToDirectory
            )
        }
    }

    fun hideBottomSheet() {
        viewModelScope.launch {
            _iosAppsEvent.send(IosAppsEvent.CloseBottomSheet)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun setRefreshingState(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    fun setErrorState(errorState: IosAppsErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun setFileSaved() {
        _uiState.update {
            it.copy(isFileSaved = true)
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

    fun retryOperation(errorState: IosAppsErrorState, operation: (() -> Unit)?) {
        when (errorState) {
            IosAppsErrorState.IOS_CONFIG -> onConfigFileClicked()
            IosAppsErrorState.IOS_APPS -> {
                operation?.let {
                    setRefreshState {
                        operation.invoke()
                    }
                }
            }
        }
    }

    fun clearIosAppInfo() {
        _uiState.update {
            it.copy(selectedIosApp = null)
        }
    }

    fun setBottomSheetOpened(isBottomSheetOpened: Boolean) {
        _uiState.update {
            it.copy(isBottomSheetOpened = isBottomSheetOpened)
        }
    }
}

data class IosAppsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorState: IosAppsErrorState? = null,
    val isFileSaved: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val selectedIosApp: IosApp? = null,
    val isBottomSheetOpened: Boolean = false
)

enum class IosAppsErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int? = null
) {
    IOS_CONFIG(R.string.error_occurred),
    IOS_APPS(R.string.error_occurred, R.string.retry)
}

sealed class IosAppsEvent {
    object Back : IosAppsEvent()
    data class CreateIosApp(val projectId: String) : IosAppsEvent()
    data class IosAppInfo(val app: IosApp) : IosAppsEvent()
    object SaveConfigFileToDirectory :
        IosAppsEvent()

    object CloseBottomSheet : IosAppsEvent()
}
