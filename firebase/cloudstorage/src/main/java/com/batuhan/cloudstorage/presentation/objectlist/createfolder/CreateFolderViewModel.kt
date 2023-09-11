package com.batuhan.cloudstorage.presentation.objectlist.createfolder

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.cloudstorage.R
import com.batuhan.core.domain.cloudstorage.UploadFile
import com.batuhan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFolderViewModel @Inject constructor(
    private val uploadFile: UploadFile,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_BUCKET_NAME = "bucketName"
        private const val KEY_PREFIX = "prefix"
        internal const val DEFAULT_MIME_TYPE = "application/octet-stream"
    }

    private val bucketName = savedStateHandle.get<String>(KEY_BUCKET_NAME)
    private val prefix = savedStateHandle.get<String?>(KEY_PREFIX).takeIf { it != null.toString() }?.let {
        Uri.decode(it)
    }

    private val _uiState = MutableStateFlow(CreateFolderUiState())
    val uiState = _uiState.asStateFlow()

    private val _createFolderEvent = Channel<CreateFolderEvent> { Channel.BUFFERED }
    val createFolderEvent = _createFolderEvent.receiveAsFlow()

    fun onBackPressed() {
        viewModelScope.launch {
            _createFolderEvent.send(CreateFolderEvent.Back)
        }
    }

    fun uploadFile() {
        var folderName = uiState.value.folderName
        if (folderName.isNullOrBlank() || folderName.isEmpty()) {
            setErrorState(CreateFolderErrorState.FOLDER_NAME_IS_EMPTY)
            return
        }
        folderName = folderName.takeIf { it.last() == '/' }?.let {
            folderName
        } ?: "$folderName/"
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = uploadFile.invoke(
                UploadFile.Params(
                    null,
                    null,
                    bucketName!!,
                    (prefix ?: "") + folderName,
                    null
                )
            )
            when (result) {
                is Result.Success -> {
                    setLoadingState(false)
                    onBackPressed()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateFolderErrorState.CREATE_FOLDER)
                }
            }
        }
    }

    fun setErrorState(errorState: CreateFolderErrorState) {
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

    fun updateFolderName(folderName: String) {
        _uiState.update {
            it.copy(folderName = folderName)
        }
    }
}

data class CreateFolderUiState(
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val errorState: CreateFolderErrorState? = null,
    val folderName: String? = null
)

enum class CreateFolderErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int?
) {
    CREATE_FOLDER(R.string.error_occurred, null),
    FOLDER_NAME_IS_EMPTY(R.string.folder_name_is_empty, null)
}

sealed class CreateFolderEvent {
    object Back : CreateFolderEvent()
}
