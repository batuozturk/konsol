package com.batuhan.management.presentation.project.settings.actions.androidapps

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
import com.batuhan.management.data.model.AndroidApp
import com.batuhan.management.domain.firebase.GetAndroidApps
import com.batuhan.management.domain.firebase.GetAndroidConfig
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
class AndroidAppsViewModel @Inject constructor(
    private val getAndroidApps: GetAndroidApps,
    private val getAndroidConfig: GetAndroidConfig,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId: String = savedStateHandle.get<String>(KEY_PROJECT_ID) ?: ""
    val apps =
        getAndroidApps.invoke(GetAndroidApps.Params(projectId = projectId)).cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(AndroidAppsUiState())
    val uiState = _uiState.asStateFlow()

    private val _androidAppsEvent = Channel<AndroidAppsEvent> { Channel.BUFFERED }
    val androidAppsEvent = _androidAppsEvent.receiveAsFlow()

    fun onBackPressed() {
        viewModelScope.launch {
            _androidAppsEvent.send(AndroidAppsEvent.Back)
        }
    }

    fun createAndroidApp() {
        viewModelScope.launch {
            _androidAppsEvent.send(AndroidAppsEvent.CreateAndroidApp(projectId))
        }
    }

    fun onInfoClicked(app: AndroidApp) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedAndroidApp = app)
            }
            _androidAppsEvent.send(AndroidAppsEvent.AndroidAppInfo(app))
        }
    }

    fun clearAndroidAppInfo() {
        _uiState.update {
            it.copy(selectedAndroidApp = null)
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
            when (val result = getAndroidConfig.invoke(GetAndroidConfig.Params(projectId, appId))) {
                is Result.Success -> {
                    val configFilenameContents = result.data.configFileContents ?: return@launch
                    val decodedConfigFileContent =
                        Base64.decode(configFilenameContents, Base64.DEFAULT)
                    stream?.write(decodedConfigFileContent)
                    stream?.close()
                    setFileSaved()
                }
                is Result.Error -> {
                    stream?.close()
                    setLoadingState(false)
                    setErrorState(AndroidAppsErrorState.ANDROID_CONFIG)
                }
            }
        }
    }

    fun onConfigFileClicked() {
        viewModelScope.launch {
            _androidAppsEvent.send(
                AndroidAppsEvent.SaveConfigFileToDirectory
            )
        }
    }

    fun hideBottomSheet() {
        viewModelScope.launch {
            _androidAppsEvent.send(AndroidAppsEvent.CloseBottomSheet)
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

    fun setErrorState(errorState: AndroidAppsErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun setFileSaved() {
        _uiState.update {
            it.copy(isFileSaved = true, isLoading = false)
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

    fun retryOperation(errorState: AndroidAppsErrorState, operation: (() -> Unit)? = null) {
        when (errorState) {
            AndroidAppsErrorState.ANDROID_APPS -> {
                operation?.let {
                    setRefreshState {
                        operation.invoke()
                    }
                }
            }
            AndroidAppsErrorState.ANDROID_CONFIG -> onConfigFileClicked()
        }
    }

    fun setBottomSheetOpened(isBottomSheetOpened: Boolean) {
        _uiState.update {
            it.copy(isBottomSheetOpened = isBottomSheetOpened)
        }
    }
}

data class AndroidAppsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorState: AndroidAppsErrorState? = null,
    val isFileSaved: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val selectedAndroidApp: AndroidApp? = null,
    val isBottomSheetOpened: Boolean = false
)

enum class AndroidAppsErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = null
) {
    ANDROID_CONFIG,
    ANDROID_APPS(actionResId = R.string.retry)
}

sealed class AndroidAppsEvent {
    data class CreateAndroidApp(val projectId: String) : AndroidAppsEvent()
    data class AndroidAppInfo(val app: AndroidApp) : AndroidAppsEvent()
    object Back : AndroidAppsEvent()
    object SaveConfigFileToDirectory : AndroidAppsEvent()
    object CloseBottomSheet : AndroidAppsEvent()
}
