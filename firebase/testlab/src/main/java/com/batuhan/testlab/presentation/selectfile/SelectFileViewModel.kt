package com.batuhan.testlab.presentation.selectfile

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.domain.GetObjectList
import com.batuhan.testlab.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectFileViewModel @Inject constructor(
    private val getObjectList: GetObjectList,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_BUCKET_NAME = "bucketName"
        private const val GCS_PATH_PREFIX = "gs://"
    }

    private val prefix = MutableStateFlow<String?>(null)

    private val bucketName = Uri.decode(savedStateHandle.get<String>(KEY_BUCKET_NAME))

    @OptIn(ExperimentalCoroutinesApi::class)
    val objects = prefix.flatMapLatest {
        getObjectList.invoke(GetObjectList.Params(bucketName, it))
    }

    private val _uiState = MutableStateFlow(SelectFileUiState())
    val uiState = _uiState.asStateFlow()

    private val _selectFileEvent = Channel<SelectFileEvent> { Channel.BUFFERED }
    val selectFileEvent = _selectFileEvent.receiveAsFlow()

    fun setPrefix(prefix: String?) {
        this.prefix.value = prefix
    }

    fun onBackPressed() {
        if (this.prefix.value?.count { it == '/' } == 1) {
            setPrefix(null)
        } else if (this.prefix.value == null) {
            viewModelScope.launch {
                _selectFileEvent.send(SelectFileEvent.Back)
            }
        } else {
            val prefix = this.prefix.value!!
            val newPrefix = prefix.substring(0, prefix.lastIndexOf("/"))
            setPrefix(newPrefix)
        }
    }

    fun setSelectedFile(path: String) {
        _uiState.update {
            it.copy(
                selectedPath = path.createGCSPath(),
                selectedFileName = path.getFilenameFromPath()
            )
        }
    }

    fun clearSelectedFile() {
        _uiState.update {
            it.copy(
                selectedPath = null,
                selectedFileName = null
            )
        }
    }

    fun onSave() {
        val selectedPath = uiState.value.selectedPath
        if (selectedPath == null) {
            setErrorState(SelectFileErrorState.FILE_NOT_SELECTED)
            return
        } else if (!selectedPath.isBundleOrApk()) {
            setErrorState(SelectFileErrorState.WRONG_FILE_FORMAT)
            return
        }
        clearErrorState()
        viewModelScope.launch {
            _selectFileEvent.send(SelectFileEvent.Success(selectedPath))
        }
    }

    fun setErrorState(errorState: SelectFileErrorState) {
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

    fun setRefreshState(operation: () -> Unit) {
        viewModelScope.launch {
            clearErrorState()
            setRefreshingState(true)
            operation.invoke()
            delay(3000L)
            setRefreshingState(false)
        }
    }

    fun retryOperation(errorState: SelectFileErrorState, operation: () -> Unit) {
        when (errorState) {
            SelectFileErrorState.CLOUD_STORAGE -> operation.invoke()
            else -> {
                // no-op
            }
        }
    }

    fun setRefreshingState(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    fun String.createGCSPath(): String {
        return "$GCS_PATH_PREFIX$bucketName/$this"
    }

    fun String.getFilenameFromPath(): String {
        return this.substring(if (this.lastIndexOf("/") == -1) 0 else this.lastIndexOf("/") + 1)
    }

    fun String.isBundleOrApk(): Boolean {
        return this.contains(".apk") || this.contains(".aab")
    }
}

data class SelectFileUiState(
    val errorState: SelectFileErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedPath: String? = null,
    val selectedFileName: String? = null
)

enum class SelectFileErrorState(@StringRes val titleResId: Int, @StringRes val actionResId: Int?) {

    FILE_NOT_SELECTED(R.string.file_not_selected, null),
    WRONG_FILE_FORMAT(R.string.wrong_file_format, null),
    CLOUD_STORAGE(R.string.error_occurred, R.string.retry)
}

sealed class SelectFileEvent {
    object Back : SelectFileEvent()
    data class Success(val gcsPath: String) : SelectFileEvent()
}
