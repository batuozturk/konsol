package com.batuhan.cloudstorage.presentation.bucket.create

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.cloudstorage.R
import com.batuhan.cloudstorage.domain.CreateDefaultBucket
import com.batuhan.core.data.model.BucketObject
import com.batuhan.core.data.model.DefaultBucket
import com.batuhan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateBucketViewModel @Inject constructor(
    private val createDefaultCreateBucket: CreateDefaultBucket,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(CreateBucketUiState())
    val uiState = _uiState.asStateFlow()

    private val _createBucketEvent = Channel<CreateBucketEvent> { Channel.BUFFERED }
    val createBucketEvent = _createBucketEvent.receiveAsFlow()

    fun createDefaultCreateBucket() {
        val bucketName = uiState.value.bucketName
        if (bucketName.isNullOrBlank() || bucketName.isEmpty()) {
            setErrorState(CreateBucketErrorState.BUCKET_NAME_EMPTY)
            return
        } else {
            clearErrorState()
        }
        setLoadingState(true)
        viewModelScope.launch {
            when (
                createDefaultCreateBucket.invoke(
                    CreateDefaultBucket.Params(
                        projectId!!,
                        DefaultBucket(
                            BucketObject(bucketName),
                            null
                        )
                    )
                )
            ) {
                is Result.Success -> {
                    setLoadingState(false)
                    onBackPressed()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateBucketErrorState.CREATE_DEFAULT_BUCKET)
                }
            }
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _createBucketEvent.send(CreateBucketEvent.Back)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun setErrorState(errorState: CreateBucketErrorState) {
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

    fun retryOperation(errorState: CreateBucketErrorState) {
        when (errorState) {
            CreateBucketErrorState.CREATE_DEFAULT_BUCKET -> createDefaultCreateBucket()
            else -> {}
        }
    }

    fun updateBucketName(bucketName: String) {
        _uiState.update {
            it.copy(bucketName = bucketName)
        }
    }
}

data class CreateBucketUiState(
    val isLoading: Boolean = false,
    val errorState: CreateBucketErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val bucketName: String? = null
)

enum class CreateBucketErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    CREATE_DEFAULT_BUCKET,
    BUCKET_NAME_EMPTY(R.string.bucket_name_is_empty, null)
}

sealed class CreateBucketEvent {
    object Back : CreateBucketEvent()
}
