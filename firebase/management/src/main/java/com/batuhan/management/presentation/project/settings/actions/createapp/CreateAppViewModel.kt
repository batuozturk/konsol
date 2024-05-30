package com.batuhan.management.presentation.project.settings.actions.createapp

import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.core.data.model.management.AndroidApp
import com.batuhan.core.data.model.management.IosApp
import com.batuhan.core.data.model.management.Status
import com.batuhan.core.data.model.management.WebApp
import com.batuhan.management.domain.firebase.*
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
class CreateAppViewModel @Inject constructor(
    private val createAndroidApp: CreateAndroidApp,
    private val createIosApp: CreateIosApp,
    private val createWebApp: CreateWebApp,
    private val getFirebaseOperation: GetFirebaseOperation,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
        internal const val KEY_APP_TYPE = "appType"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)
    private val appType = savedStateHandle.get<String>(KEY_APP_TYPE)

    private val _uiState = MutableStateFlow(CreateAppUiState())
    val uiState = _uiState.asStateFlow()

    private val _createAppEvent = Channel<CreateAppEvent> { Channel.BUFFERED }
    val createAppEvent = _createAppEvent.receiveAsFlow()

    fun onSave() {
        setLoadingState(true)
        viewModelScope.launch {
            val isSuccessful = decideOperation(uiState.value.createAppInputState)
            setLoadingState(false)
            if (!isSuccessful) {
                return@launch
            }
            _uiState.update {
                it.copy(isSuccessful = true)
            }
            delay(1000L)
            onBackPressed()
        }
    }

    fun saveContentForIos(createAppInputState: CreateAppInputState.Ios) {
        if (uiState.value.errorState != null) clearErrorState()
        _uiState.update {
            it.copy(
                createAppInputState = createAppInputState
            )
        }
    }

    fun saveContentForAndroid(createAppInputState: CreateAppInputState.Android) {
        if (uiState.value.errorState != null) clearErrorState()
        _uiState.update {
            it.copy(
                createAppInputState = createAppInputState
            )
        }
    }

    fun saveContentForWeb(createAppInputState: CreateAppInputState.Web) {
        if (uiState.value.errorState != null) clearErrorState()
        _uiState.update {
            it.copy(
                createAppInputState = createAppInputState
            )
        }
    }

    suspend fun decideOperation(createAppInputState: CreateAppInputState): Boolean {
        return when (createAppInputState) {
            is CreateAppInputState.Android -> {
                executeOperation(
                    createAppInputState
                ) {
                    createAndroidApp(createAppInputState)
                }
            }
            is CreateAppInputState.Ios -> {
                executeOperation(
                    createAppInputState
                ) {
                    createIosApp(createAppInputState)
                }
            }
            is CreateAppInputState.Web -> {
                executeOperation(
                    createAppInputState
                ) {
                    createWebApp(createAppInputState)
                }
            }
            else -> {
                val errorState = when (appType) {
                    AppType.ANDROID.name -> ActionErrorState.PACKAGE_NAME_IS_EMPTY
                    AppType.IOS.name -> ActionErrorState.BUNDLE_ID_IS_EMPTY
                    AppType.WEB.name -> ActionErrorState.APP_NAME_IS_EMPTY
                    else -> ActionErrorState.UNKNOWN_ERROR_OCCURED
                }
                setErrorState(errorState)
                false
            }
        }
    }

    suspend fun createAndroidApp(createAppInputState: CreateAppInputState.Android): String? {
        if (createAppInputState.packageName.isEmpty() || createAppInputState.packageName.isBlank()) {
            setErrorState(ActionErrorState.PACKAGE_NAME_IS_EMPTY)
            return null
        }
        if (!createAppInputState.packageName.isValidPackageName()) {
            setErrorState(ActionErrorState.PACKAGE_NAME_INVALID)
            return null
        }

        if (createAppInputState.appName?.isNotEmpty() == true && createAppInputState.appName.isBlank()) {
            setErrorState(ActionErrorState.APP_NAME_IS_EMPTY)
            return null
        }

        if (createAppInputState.sha1String?.isNotEmpty() == true && !createAppInputState.sha1String.isValidSha1()) {
            setErrorState(ActionErrorState.SHA1_INVALID)
            return null
        }

        val result = createAndroidApp.invoke(
            CreateAndroidApp.Params(
                projectId = projectId!!,
                createAppInputState.toAndroidApp()
            )
        )
        return when (result) {
            is Result.Success -> result.data.name
            is Result.Error -> {
                setLoadingState(false)
                setErrorState(ActionErrorState.FIREBASE_API_ERROR)
                null
            }
        }
    }

    suspend fun createIosApp(createAppInputState: CreateAppInputState.Ios): String? {
        if (createAppInputState.bundleId.isEmpty() || createAppInputState.bundleId.isBlank()) {
            setErrorState(ActionErrorState.BUNDLE_ID_IS_EMPTY)
            return null
        }
        if (!createAppInputState.bundleId.isValidBundleId()) {
            setErrorState(ActionErrorState.BUNDLE_ID_INVALID)
            return null
        }
        if (createAppInputState.appName?.isNotEmpty() == true && createAppInputState.appName.isBlank()) {
            setErrorState(ActionErrorState.APP_NAME_IS_EMPTY)
            return null
        }
        if (createAppInputState.appStoreId?.isNotEmpty() == true && createAppInputState.appStoreId.isBlank()) {
            setErrorState(ActionErrorState.APP_STORE_ID_IS_EMPTY)
            return null
        }

        val result = createIosApp.invoke(
            CreateIosApp.Params(
                projectId = projectId!!,
                createAppInputState.toIosApp()
            )
        )
        return when (result) {
            is Result.Success -> result.data.name
            is Result.Error -> {
                setLoadingState(false)
                setErrorState(ActionErrorState.FIREBASE_API_ERROR)
                null
            }
        }
    }

    suspend fun createWebApp(createAppInputState: CreateAppInputState.Web): String? {
        if (createAppInputState.appName.isEmpty() || createAppInputState.appName.isBlank()) {
            setErrorState(ActionErrorState.APP_NAME_IS_EMPTY)
            return null
        }

        val result = createWebApp.invoke(
            CreateWebApp.Params(
                projectId = projectId!!,
                createAppInputState.toWebApp()
            )
        )
        return when (result) {
            is Result.Success -> result.data.name
            is Result.Error -> {
                setLoadingState(false)
                setErrorState(ActionErrorState.FIREBASE_API_ERROR)
                null
            }
        }
    }

    suspend fun executeOperation(
        state: CreateAppInputState,
        operation: suspend (state: CreateAppInputState) -> String?
    ): Boolean {
        val operationIdResult = operation.invoke(state)
        operationIdResult ?: return false
        val isSuccess = getFirebaseOperation(operationId = operationIdResult)
        if (!isSuccess) {
            setLoadingState(false)
            return false
        }
        return true
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
                    setErrorState(ActionErrorState.FIREBASE_API_ERROR)
                }
            }
            delay(3000)
        }
        return if (isDone && error == null) {
            true
        } else {
            setErrorState(ActionErrorState.FIREBASE_API_ERROR)
            false
        }
    }

    fun setErrorState(actionErrorState: ActionErrorState) {
        _uiState.update {
            it.copy(errorState = actionErrorState)
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

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) {
            clearErrorState()
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _createAppEvent.send(CreateAppEvent.Back)
        }
    }

    fun CreateAppInputState.Android.toAndroidApp() = AndroidApp(
        displayName = appName.takeIf { it?.isNotEmpty() == true },
        packageName = packageName,
        sha1Hashes = sha1String.takeIf { it?.isNotEmpty() == true }?.let {
            listOf(it)
        }
    )

    fun CreateAppInputState.Web.toWebApp() =
        WebApp(displayName = appName.takeIf { it.isNotEmpty() })

    fun CreateAppInputState.Ios.toIosApp() =
        IosApp(
            displayName = appName.takeIf { it?.isNotEmpty() == true },
            bundleId = bundleId,
            appStoreId = appStoreId.takeIf { it?.isNotEmpty() == true }
        )

    fun String.isValidBundleId() =
        this.matches("^[A-Za-z\\d.\\-]{1,155}\$".toRegex()) // https://stackoverflow.com/a/50329089

    fun String.isValidPackageName() =
        this.matches("^([A-Za-z]\\w*\\.)+[A-Za-z]\\w*\$".toRegex()) // https://stackoverflow.com/a/40772073

    fun String.isValidSha1() =
        this.matches("^[a-fA-F\\d]{40}$".toRegex()) // https://stackoverflow.com/a/1896748
}

data class CreateAppUiState(
    val isLoading: Boolean = false,
    val createAppInputState: CreateAppInputState = CreateAppInputState.Unspecified,
    val errorState: ActionErrorState? = null,
    val isSuccessful: Boolean = false,
    val isSnackbarOpened: Boolean = false
)

sealed class CreateAppInputState {
    data class Android(val packageName: String, val appName: String?, val sha1String: String?) :
        CreateAppInputState()

    data class Ios(val bundleId: String, val appName: String?, val appStoreId: String?) :
        CreateAppInputState()

    data class Web(val appName: String) : CreateAppInputState()

    object Unspecified : CreateAppInputState()
}

sealed class CreateAppEvent {
    object Back : CreateAppEvent()
}

@Keep
enum class AppType {
    ANDROID, IOS, WEB
}

enum class ActionErrorState(
    @StringRes val messageResId: Int,
    @StringRes val actionResId: Int? = null
) {
    PACKAGE_NAME_IS_EMPTY(R.string.package_name_is_empty),
    BUNDLE_ID_IS_EMPTY(R.string.bundle_id_is_empty),
    APP_NAME_IS_EMPTY(R.string.app_name_is_empty),
    APP_STORE_ID_IS_EMPTY(R.string.app_store_id_is_empty),
    FIREBASE_API_ERROR(R.string.error_occurred, R.string.retry),
    PACKAGE_NAME_INVALID(R.string.package_name_is_invalid),
    BUNDLE_ID_INVALID(R.string.bundle_id_is_invalid),
    SHA1_INVALID(R.string.sha1_is_invalid),
    UNKNOWN_ERROR_OCCURED(R.string.error_occurred, R.string.retry)
}
