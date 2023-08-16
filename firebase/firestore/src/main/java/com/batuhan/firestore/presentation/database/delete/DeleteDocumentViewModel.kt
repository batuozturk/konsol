package com.batuhan.firestore.presentation.database.delete

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.Result
import com.batuhan.firestore.R
import com.batuhan.firestore.domain.documents.DeleteDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteDocumentViewModel @Inject constructor(
    private val deleteDocument: DeleteDocument,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_FIRESTORE_PATH = "firestorePath"
    }

    private val documentPath = Uri.decode(savedStateHandle.get<String>(KEY_FIRESTORE_PATH))

    private val _uiState = MutableStateFlow(DeleteDocumentUiState())
    val uiState = _uiState.asStateFlow()

    private val _deleteDocumentEvent = Channel<DeleteDocumentEvent> { Channel.BUFFERED }
    val deleteDocumentEvent = _deleteDocumentEvent.receiveAsFlow()

    fun onBackPressed(isSuccess: Boolean = false) {
        viewModelScope.launch {
            _deleteDocumentEvent.send(DeleteDocumentEvent.Back(isSuccess))
        }
    }

    fun setErrorState(errorState: DeleteDocumentErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun updateDocumentName(documentName: String) {
        _uiState.update {
            it.copy(documentName = documentName)
        }
    }

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) clearErrorState()
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun deleteDocument() {
        val documentName = uiState.value.documentName
        if (documentName != documentPath!!.substring(documentPath.lastIndexOf("/") + 1)) {
            setErrorState(DeleteDocumentErrorState.DOCUMENT_NAME_WRONG)
            return
        }
        setLoadingState(true)
        viewModelScope.launch {
            when (deleteDocument.invoke(DeleteDocument.Params(documentPath))) {
                is Result.Success -> {
                    setLoadingState(false)
                    onBackPressed(true)
                }
                is Result.Error -> {
                    setErrorState(DeleteDocumentErrorState.DELETE_DOCUMENT)
                }
            }
        }
    }

    fun retryOperation(errorState: DeleteDocumentErrorState) {
        when (errorState) {
            DeleteDocumentErrorState.DOCUMENT_NAME_WRONG -> {
                // no-op
            }
            DeleteDocumentErrorState.DELETE_DOCUMENT -> {
                deleteDocument()
            }
        }
    }
}

data class DeleteDocumentUiState(
    val errorState: DeleteDocumentErrorState? = null,
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val documentName: String? = null
)

sealed class DeleteDocumentEvent {
    data class Back(val isSuccess: Boolean) : DeleteDocumentEvent()
}

enum class DeleteDocumentErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int?
) {
    DOCUMENT_NAME_WRONG(R.string.document_name_wrong, null),
    DELETE_DOCUMENT(R.string.error_occured, R.string.retry),
}
