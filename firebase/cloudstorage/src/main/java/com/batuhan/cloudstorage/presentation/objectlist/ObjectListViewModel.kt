package com.batuhan.cloudstorage.presentation.objectlist

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.cloudstorage.R
import com.batuhan.core.domain.cloudstorage.DeleteFile
import com.batuhan.core.domain.cloudstorage.GetObjectList
import com.batuhan.core.domain.cloudstorage.UploadFile
import com.batuhan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ObjectListViewModel @Inject constructor(
    private val getObjectList: GetObjectList,
    private val uploadFile: UploadFile,
    private val deleteFile: DeleteFile,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_BUCKET_NAME = "bucketName"
        private const val GCS_PATH_PREFIX = "gs://"
        internal const val DEFAULT_MIME_TYPE = "application/octet-stream"
    }

    private val prefix = MutableStateFlow<String?>(null)

    private val bucketName = Uri.decode(savedStateHandle.get<String>(KEY_BUCKET_NAME))

    @OptIn(ExperimentalCoroutinesApi::class)
    val objects = prefix.flatMapLatest { prefix ->
        getObjectList.invoke(GetObjectList.Params(bucketName, prefix))
    }

    private val _uiState = MutableStateFlow(ObjectListUiState())
    val uiState = _uiState.asStateFlow()

    private val _selectFileEvent = Channel<ObjectListEvent> { Channel.BUFFERED }
    val selectFileEvent = _selectFileEvent.receiveAsFlow()

    fun setPrefix(prefix: String?) {
        this.prefix.value = prefix
        _uiState.update {
            it.copy(prefix = prefix)
        }
    }

    fun onBackPressed() {
        if (this.prefix.value?.count { it == '/' } == 1) {
            setPrefix(null)
        } else if (this.prefix.value == null) {
            viewModelScope.launch {
                _selectFileEvent.send(ObjectListEvent.Back)
            }
        } else {
            val prefix = this.prefix.value!!
            // second last slash character index + 1
            val newPrefix = prefix.substring(0, prefix.lastIndexOf("/"))
            setPrefix(newPrefix.substring(0, newPrefix.lastIndexOf("/") + 1))
        }
    }

    fun uploadFile(contentResolver: ContentResolver, fileUri: Uri) {
        val stream = contentResolver.openInputStream(fileUri) ?: return
        val bytes = stream.readBytes()
        val contentType = contentResolver.getType(fileUri) ?: DEFAULT_MIME_TYPE
        val length = bytes.size.toLong()
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = uploadFile.invoke(
                UploadFile.Params(
                    length,
                    contentType,
                    bucketName!!,
                    (prefix.value ?: "") + (fileUri.getFileName(contentResolver) ?: return@launch),
                    bytes
                )
            )
            when (result) {
                is Result.Success -> {
                    setLoadingState(false)
                    stream.close()
                    prefix.value = prefix.value
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(ObjectListErrorState.UPLOAD_FILE)
                    stream.close()
                }
            }
        }
    }

    fun deleteFile(objectName: String) {
        uiState.value.deletedFile?.let {
            _uiState.update {
                it.copy(deletedFile = objectName)
            }
        }
        setLoadingState(true)
        viewModelScope.launch {
            when (deleteFile.invoke(DeleteFile.Params(bucketName, objectName))) {
                is Result.Success -> {
                    prefix.value = prefix.value
                    _uiState.update {
                        it.copy(deletedFile = null)
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(ObjectListErrorState.DELETE_FILE)
                }
            }
        }
    }

    fun Uri.getFileName(contentResolver: ContentResolver): String? = when (this.scheme) {
        ContentResolver.SCHEME_CONTENT -> getContentFileName(this, contentResolver)
        else -> this.path?.let(::File)?.name
    }

    private fun getContentFileName(uri: Uri, contentResolver: ContentResolver): String? =
        runCatching {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    .let(cursor::getString)
            }
        }.getOrNull()

    fun setErrorState(errorState: ObjectListErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
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

    fun retryOperation(errorState: ObjectListErrorState, operation: () -> Unit) {
        when (errorState) {
            ObjectListErrorState.CLOUD_STORAGE -> operation.invoke()
            ObjectListErrorState.DELETE_FILE -> {
                uiState.value.deletedFile?.let { deleteFile(it) }
            }
            ObjectListErrorState.UPLOAD_FILE -> {}
        }
    }

    fun setRefreshingState(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    fun navigateToCreateFolder() {
        viewModelScope.launch {
            _selectFileEvent.send(
                ObjectListEvent.CreateFolder(
                    bucketName!!,
                    Uri.encode(prefix.value)
                )
            )
        }
    }
}

data class ObjectListUiState(
    val errorState: ObjectListErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val prefix: String? = "",
    val deletedFile: String? = null
)

enum class ObjectListErrorState(@StringRes val titleResId: Int, @StringRes val actionResId: Int?) {
    CLOUD_STORAGE(R.string.error_occurred, R.string.retry),
    DELETE_FILE(R.string.error_occurred, R.string.retry),
    UPLOAD_FILE(R.string.error_occurred, null)
}

sealed class ObjectListEvent {
    object Back : ObjectListEvent()
    data class CreateFolder(val bucketName: String, val prefix: String?) : ObjectListEvent()
}
