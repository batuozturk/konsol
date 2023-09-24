package com.batuhan.cloudstorage.presentation.bucket

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.cloudstorage.R
import com.batuhan.cloudstorage.domain.CreateDefaultBucket
import com.batuhan.core.data.model.ApiServiceState
import com.batuhan.core.data.model.BucketObject
import com.batuhan.core.data.model.DefaultBucket
import com.batuhan.core.data.model.Status
import com.batuhan.core.domain.cloudstorage.AddFirebase
import com.batuhan.core.domain.cloudstorage.GetDefaultBucket
import com.batuhan.core.domain.serviceusage.EnableService
import com.batuhan.core.domain.serviceusage.GetServiceEnableState
import com.batuhan.core.domain.serviceusage.GetServiceUsageOperation
import com.batuhan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class BucketViewModel @Inject constructor(
    private val getDefaultBucket: GetDefaultBucket,
    private val createDefaultBucket: CreateDefaultBucket,
    private val getServiceEnableState: GetServiceEnableState,
    private val enableService: EnableService,
    private val getServiceUsageOperation: GetServiceUsageOperation,
    private val addFirebase: AddFirebase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        private const val CLOUD_STORAGE_SERVICE = "services/firebasestorage.googleapis.com"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(BucketUiState())
    val uiState = _uiState.asStateFlow()

    private val _bucketEvent = Channel<BucketEvent> { Channel.BUFFERED }
    val bucketEvent = _bucketEvent.receiveAsFlow()

    fun addFirebase(bucketName: String) {
        setLoadingState(true)
        viewModelScope.launch {
            when (val result = addFirebase.invoke(AddFirebase.Params(bucketName))) {
                is Result.Success -> {
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setErrorState(BucketErrorState.GET_DEFAULT_BUCKET)
                    setLoadingState(false)
                }
            }
        }
    }

    fun getDefaultBucket(firebaseIsAdded: Boolean = true) {
        setLoadingState(true)
        viewModelScope.launch {
            when (val result = getDefaultBucket.invoke(GetDefaultBucket.Params(projectId!!))) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(defaultBucket = result.data)
                    }
                    if (!firebaseIsAdded) {
                        addFirebase(result.data.bucket?.name!!)
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    val throwable = result.throwable as? HttpException
                    if (throwable?.response()?.errorBody()?.string()
                        ?.contains("enable") == true
                    ) {
                        getServiceEnableState()
                    } else if (throwable?.code() == 404) {
                        setLoadingState(false)
                    } else {
                        setErrorState(BucketErrorState.GET_DEFAULT_BUCKET)
                        setLoadingState(false)
                    }
                }
            }
        }
    }

    suspend fun executeServiceUsageOperation(operation: suspend () -> Pair<String?, Boolean?>): Boolean {
        val pair = operation.invoke()
        return if (pair.second == true) true
        else {
            pair.first ?: return false
            getServiceUsageOperation(pair.first!!)
        }
    }

    fun enableServiceOperation() {
        viewModelScope.launch {
            setLoadingState(true)
            val isSuccessful = executeServiceUsageOperation(::enableService)
            if (isSuccessful) {
                setSnackbarState(false)
                getDefaultBucket(firebaseIsAdded = false)
            } else {
                setLoadingState(false)
            }
        }
    }

    suspend fun enableService(): Pair<String?, Boolean?> {
        val result =
            enableService.invoke(
                EnableService.Params("projects/$projectId/$CLOUD_STORAGE_SERVICE")
            )
        return when (result) {
            is Result.Success -> {
                Pair(result.data.name, result.data.done)
            }
            is Result.Error -> {
                setLoadingState(false)
                setErrorState(BucketErrorState.ENABLE_SERVICE)
                Pair(null, false)
            }
        }
    }

    suspend fun getServiceUsageOperation(operationName: String): Boolean {
        var isDone = false
        var error: Status? = null
        while (!isDone && error == null) {
            val result = getServiceUsageOperation.invoke(
                GetServiceUsageOperation.Params(operationName = operationName)
            )
            when (result) {
                is Result.Success -> {
                    isDone = result.data.done ?: false
                    error = result.data.error
                }
                is Result.Error -> {
                    error = Status("1000", "No connection")
                    setErrorState(BucketErrorState.OPERATION)
                }
            }
            delay(3000L)
        }
        return error == null
    }

    fun getServiceEnableState() {
        viewModelScope.launch {
            val result =
                getServiceEnableState.invoke(
                    GetServiceEnableState.Params(
                        "projects/$projectId/$CLOUD_STORAGE_SERVICE"
                    )
                )
            when (result) {
                is Result.Success -> {
                    if (result.data.state != ApiServiceState.ENABLED) {
                        setErrorState(BucketErrorState.ENABLE_SERVICE)
                    } else {
                        getDefaultBucket()
                    }
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(BucketErrorState.ENABLE_SERVICE)
                }
            }
        }
    }

    fun createDefaultBucket() {
        viewModelScope.launch {
            _bucketEvent.send(BucketEvent.CreateBucket(projectId!!))
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _bucketEvent.send(BucketEvent.Back)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun setErrorState(errorState: BucketErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) clearErrorState()
    }

    fun retryOperation(errorState: BucketErrorState) {
        when (errorState) {
            BucketErrorState.GET_DEFAULT_BUCKET -> getDefaultBucket()
            BucketErrorState.ENABLE_SERVICE -> enableServiceOperation()
            else -> {}
        }
    }

    fun navigateToObjectListScreen() {
        var bucket = uiState.value.defaultBucket?.bucket?.name ?: return
        bucket = bucket.substring(bucket.lastIndexOf("/") + 1)
        viewModelScope.launch {
            _bucketEvent.send(BucketEvent.ObjectList(bucket))
        }
    }
}

data class BucketUiState(
    val isLoading: Boolean = true,
    val errorState: BucketErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val defaultBucket: DefaultBucket? = null
)

enum class BucketErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    GET_DEFAULT_BUCKET,
    ENABLE_SERVICE(R.string.cloud_storage_is_not_enabled, R.string.enable),
    OPERATION(actionResId = null)
}

sealed class BucketEvent {
    object Back : BucketEvent()
    data class CreateBucket(val projectId: String) : BucketEvent()
    data class ObjectList(val bucketName: String) : BucketEvent()
}
