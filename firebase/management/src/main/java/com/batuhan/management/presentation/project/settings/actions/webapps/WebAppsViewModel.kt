package com.batuhan.management.presentation.project.settings.actions.webapps

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.core.data.model.management.WebApp
import com.batuhan.management.domain.firebase.GetWebApps
import com.batuhan.management.domain.firebase.GetWebConfig
import com.google.gson.Gson
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
class WebAppsViewModel @Inject constructor(
    private val getWebApps: GetWebApps,
    private val getWebConfig: GetWebConfig,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId: String = savedStateHandle.get<String>(KEY_PROJECT_ID) ?: ""
    val apps =
        getWebApps.invoke(GetWebApps.Params(projectId = projectId)).cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(WebAppsUiState())
    val uiState = _uiState.asStateFlow()

    private val _webAppsEvent = Channel<WebAppsEvent> { Channel.BUFFERED }
    val webAppsEvent = _webAppsEvent.receiveAsFlow()

    fun onBackPressed() {
        viewModelScope.launch {
            _webAppsEvent.send(WebAppsEvent.Back)
        }
    }

    fun createWebApp() {
        viewModelScope.launch {
            _webAppsEvent.send(WebAppsEvent.CreateWebApp(projectId))
        }
    }

    fun onInfoClicked(webApp: WebApp) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedWebApp = webApp)
            }
            _webAppsEvent.send(WebAppsEvent.WebAppInfo(webApp))
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
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
            when (val result = getWebConfig.invoke(GetWebConfig.Params(projectId, appId))) {
                is Result.Success -> {
                    val configFilenameContents = result.data
                    val serializedContent = Gson().toJson(configFilenameContents).toByteArray()
                    stream?.write(serializedContent)
                    stream?.close()
                    setLoadingState(false)
                    setFileSaved()
                }
                is Result.Error -> {
                    stream?.close()
                    setLoadingState(false)
                    setErrorState(WebAppsErrorState.WEB_CONFIG)
                }
            }
        }
    }

    fun onConfigFileClicked() {
        viewModelScope.launch {
            _webAppsEvent.send(
                WebAppsEvent.SaveConfigFileToDirectory
            )
        }
    }

    fun hideBottomSheet() {
        viewModelScope.launch {
            _webAppsEvent.send(WebAppsEvent.CloseBottomSheet)
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

    fun setErrorState(errorState: WebAppsErrorState) {
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

    fun retryOperation(errorState: WebAppsErrorState, operation: (() -> Unit)?) {
        when (errorState) {
            WebAppsErrorState.WEB_APPS -> {
                operation?.let {
                    setRefreshState {
                        operation.invoke()
                    }
                }
            }
            WebAppsErrorState.WEB_CONFIG -> onConfigFileClicked()
        }
    }

    fun clearWebAppInfo() {
        _uiState.update {
            it.copy(selectedWebApp = null)
        }
    }

    fun setBottomSheetOpened(isBottomSheetOpened: Boolean) {
        _uiState.update {
            it.copy(isBottomSheetOpened = isBottomSheetOpened)
        }
    }
}

data class WebAppsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorState: WebAppsErrorState? = null,
    val isFileSaved: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val selectedWebApp: WebApp? = null,
    val isBottomSheetOpened: Boolean = false
)

enum class WebAppsErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int? = null
) {
    WEB_APPS(R.string.error_occurred, R.string.retry),
    WEB_CONFIG(R.string.error_occurred, R.string.retry)
}

sealed class WebAppsEvent {
    object Back : WebAppsEvent()
    data class CreateWebApp(val projectId: String) : WebAppsEvent()
    data class WebAppInfo(val app: WebApp) : WebAppsEvent()
    object SaveConfigFileToDirectory :
        WebAppsEvent()

    object CloseBottomSheet : WebAppsEvent()
}
