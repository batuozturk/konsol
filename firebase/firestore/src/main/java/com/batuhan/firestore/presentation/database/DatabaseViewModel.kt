package com.batuhan.firestore.presentation.database

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.*
import com.batuhan.firestore.domain.documents.GetDocument
import com.batuhan.firestore.domain.documents.ListCollectionIds
import com.batuhan.firestore.domain.documents.ListDocuments
import com.batuhan.firestore.domain.documents.PatchDocument
import com.batuhan.firestore.util.checkValidation
import com.batuhan.firestore.util.toDocumentFieldMap
import com.batuhan.firestore.util.updateDocumentFieldErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(
    private val listDocuments: ListDocuments,
    private val getDocument: GetDocument,
    private val listCollectionIds: ListCollectionIds,
    private val patchDocument: PatchDocument,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_NAME = "name"
        private const val DOCUMENTS_PATH = "/documents"
    }

    private val root = Uri.decode(savedStateHandle.get<String>(KEY_NAME)) + DOCUMENTS_PATH

    private val path = MutableStateFlow(root)

    private val _uiState = MutableStateFlow(DatabaseUiState())
    val uiState = _uiState.asStateFlow()

    private val _databaseEvent = Channel<DatabaseEvent> { Channel.BUFFERED }
    val databaseEvent = _databaseEvent.receiveAsFlow()

    private val createCollection get() = !uiState.value.isCollectionClicked

    fun isRootPath() = root == path.value

    @OptIn(ExperimentalCoroutinesApi::class)
    val collectionIds = path.flatMapLatest {
        if (!uiState.value.isCollectionClicked) {
            setLoadingState(true)
            listCollectionIds.invoke(ListCollectionIds.Params(it))
        } else {
            flowOf(PagingData.empty())
        }
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val documents = path.flatMapLatest {
        if (uiState.value.isCollectionClicked) {
            setLoadingState(true)
            val splittedPath = it.splitPath()
            val name = splittedPath[0]
            val collectionId = splittedPath[1]
            listDocuments.invoke(ListDocuments.Params(name, collectionId))
        } else {
            flowOf(PagingData.empty())
        }
    }.cachedIn(viewModelScope)

    val document = MutableStateFlow<Document?>(null)

    init {
        setPath(root)
        getDocument()
    }

    fun getDocument() {
        viewModelScope.launch {
            setLoadingState(true)
            if (!isRootPath() && !uiState.value.isCollectionClicked) {
                when (val result = getDocument.invoke(GetDocument.Params(path.value))) {
                    is Result.Success -> {
                        setLoadingState(false)
                        document.value = result.data
                    }
                    is Result.Error -> {
                        setErrorState(DatabaseErrorState.GET_DOCUMENT)
                        setLoadingState(false)
                    }
                }
            }
        }
    }

    fun onBackPressed() {
        if (path.value != root) {
            val newDocumentPath = path.value.splitPath()[0]
            setCollectionClicked(!uiState.value.isCollectionClicked)
            setPath(newDocumentPath)
            setSnackbarState(false)
            if (uiState.value.isCollectionClicked) {
                document.value = null
            } else {
                getDocument()
            }
        } else {
            viewModelScope.launch {
                _databaseEvent.send(DatabaseEvent.Back)
            }
        }
    }

    fun onCollectionClicked(collectionPath: String) {
        setCollectionClicked(true)
        val newPath = path.value + "/" + collectionPath
        setPath(newPath)
        document.value = null
    }

    fun onDocumentClicked(document: Document) {
        setCollectionClicked(false)
        setPath(document.name ?: return)
        setLoadingState(true)
        setDocument(document)
    }

    fun setDocument(newDocument: Document) {
        document.value = newDocument
        _uiState.update {
            it.copy(currentDocument = newDocument)
        }
    }

    fun setPath(newPath: String) {
        _uiState.update {
            it.copy(currentPath = newPath)
        }
        path.value = newPath
    }

    fun onDocumentRefreshed() {
        setDocumentRefreshed(true)
        getDocument()
    }

    fun setDocumentRefreshed(isDocumentRefreshed: Boolean) {
        _uiState.update {
            it.copy(isDocumentRefreshed = isDocumentRefreshed)
        }
    }

    fun setCollectionClicked(isCollectionClicked: Boolean) {
        _uiState.update {
            it.copy(isCollectionClicked = isCollectionClicked)
        }
    }

    fun setErrorState(databaseErrorState: DatabaseErrorState) {
        _uiState.update {
            it.copy(errorState = databaseErrorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun setRefreshingState(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    fun onRefresh(operation: () -> Unit) {
        viewModelScope.launch {
            setRefreshingState(true)
            operation.invoke()
            path.value = path.value
            delay(3000L)
            setRefreshingState(false)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun navigateToCreateDocumentScreen() {
        viewModelScope.launch {
            _databaseEvent.send(
                DatabaseEvent.NavigateToCreateDocumentScreen(
                    Uri.encode(path.value),
                    createCollection
                )
            )
        }
    }

    fun navigateToDeleteDocumentScreen() {
        viewModelScope.launch {
            _databaseEvent.send(
                DatabaseEvent.NavigateToDeleteDocumentScreen(
                    Uri.encode(path.value),
                    createCollection
                )
            )
        }
    }

    fun retryOperation(
        errorState: DatabaseErrorState,
        documentRefresh: () -> Unit,
        collectionIdsRefresh: () -> Unit
    ) {
        when (errorState) {
            DatabaseErrorState.LIST_COLLECTION_IDS, DatabaseErrorState.GET_DOCUMENT -> {
                getDocument()
                collectionIdsRefresh.invoke()
            }
            DatabaseErrorState.LIST_DOCUMENTS -> documentRefresh.invoke()
            DatabaseErrorState.PATCH_DOCUMENT -> patchDocument()
        }
    }

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) clearErrorState()
    }

    fun patchDocument(isDeleted: Boolean = false) {
        viewModelScope.launch {
            val documentFieldList =
                document.value?.fields?.toDocumentFieldList()?.toMutableList() ?: return@launch
            val currentEditedField = uiState.value.currentEditedField ?: return@launch
            val currentEditedFieldParentIndex = uiState.value.currentEditedFieldParentIndex
            val parentDocumentField =
                currentEditedFieldParentIndex?.let { documentFieldList[it] }
            val currentEditedFieldIndex = uiState.value.currentEditedFieldIndex
            var attributeName: String? = null
            parentDocumentField?.let {
                when (it) {
                    is DocumentField.ArrayValue -> {
                        val updatedList = it.values.toMutableList()
                        currentEditedFieldIndex?.let {
                            updatedList[currentEditedFieldIndex] = currentEditedField
                        } ?: run {
                            updatedList.add(currentEditedField)
                        }
                        documentFieldList[currentEditedFieldParentIndex] =
                            it.copy(values = updatedList)
                        attributeName = it.attributeName
                    }
                    is DocumentField.MapValue -> {
                        val updatedList = it.values.toMutableList()
                        currentEditedFieldIndex?.let {
                            updatedList[currentEditedFieldIndex] = currentEditedField
                        } ?: run {
                            updatedList.add(currentEditedField)
                        }
                        documentFieldList[currentEditedFieldParentIndex] =
                            it.copy(values = updatedList)
                        attributeName = it.attributeName
                    }
                    else -> {}
                }
            } ?: run {
                currentEditedFieldIndex?.let {
                    documentFieldList[currentEditedFieldIndex] = currentEditedField
                } ?: run {
                    documentFieldList.add(currentEditedField)
                }
                attributeName = currentEditedField.attributeName
            }
            val document = document.value?.copy(fields = documentFieldList.toDocumentFieldMap())
                ?: return@launch
            attributeName ?: return@launch
            val documentMask = DocumentMask(fieldPaths = listOf(attributeName!!))
            setLoadingState(true)
            when (patchDocument.invoke(PatchDocument.Params(documentMask, document))) {
                is Result.Success -> {
                    setLoadingState(false)
                    this@DatabaseViewModel.document.value = document
                    clearEditedField()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(DatabaseErrorState.PATCH_DOCUMENT)
                }
            }
        }
    }

    fun setDocumentFieldEdited(isDocumentFieldEditing: Boolean) {
        _uiState.update {
            it.copy(isDocumentFieldEditing = isDocumentFieldEditing)
        }
    }

    fun editDocumentField(
        field: DocumentField,
        fieldIndex: Int? = null,
        parentFieldIndex: Int? = null
    ) {
        val parentField =
            parentFieldIndex?.let { document.value?.fields?.toDocumentFieldList()?.get(it) }
        val isArrayField = parentField is DocumentField.ArrayValue
        var updatedField: DocumentField =
            if ((field.attributeName.isEmpty() || field.attributeName.isBlank()) && !isArrayField) {
                field.updateDocumentFieldErrorState(
                    errorState = DocumentField.DocumentFieldErrorState.ATTRIBUTE_NAME_EMPTY
                )!!
            } else {
                field.updateDocumentFieldErrorState(null)!!
            }
        if (updatedField.errorState == null) {
            updatedField = updatedField.checkValidation()!!
        }
        _uiState.update {
            it.copy(
                currentEditedField = updatedField,
                currentEditedFieldParentIndex = parentFieldIndex,
                currentEditedFieldIndex = fieldIndex,
                isBottomSheetOpened = true
            )
        }
    }

    fun clearEditedField() {
        _uiState.update {
            it.copy(
                currentEditedField = null,
                currentEditedFieldParentIndex = null,
                currentEditedFieldIndex = null
            )
        }
    }

    fun setBottomSheetState(isBottomSheetOpened: Boolean) {
        _uiState.update {
            it.copy(isBottomSheetOpened = isBottomSheetOpened)
        }
        if (!isBottomSheetOpened) setDocumentFieldEdited(false)
    }

    fun removeDocumentField(
        fieldIndex: Int?,
        parentFieldIndex: Int? = null
    ) {
        fieldIndex ?: return
        var documentRemoved: DocumentField?
        var attributeName: String?
        viewModelScope.launch {
            val documentFieldList =
                document.value?.fields?.toDocumentFieldList()?.toMutableList() ?: return@launch
            if (parentFieldIndex != null) {
                val parentDocumentField = documentFieldList[parentFieldIndex]
                removeParentFieldList(parentDocumentField, fieldIndex)
                    .let { pair ->
                        documentFieldList[parentFieldIndex] = pair.first!!
                        documentRemoved = pair.second
                        attributeName = pair.first!!.attributeName
                    }
            } else {
                // validation
                documentRemoved = documentFieldList.removeAt(fieldIndex)
                attributeName = documentRemoved!!.attributeName
            }

            val document = document.value?.copy(fields = documentFieldList.toDocumentFieldMap())
                ?: return@launch
            attributeName ?: return@launch
            val documentMask = DocumentMask(fieldPaths = listOf(attributeName!!))
            setLoadingState(true)
            val result = patchDocument.invoke(PatchDocument.Params(documentMask, document))
            when (result) {
                is Result.Success -> {
                    setLoadingState(false)
                    this@DatabaseViewModel.document.value = document
                    clearEditedField()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(DatabaseErrorState.PATCH_DOCUMENT)
                }
            }
        }
    }

    fun removeParentFieldList(
        parentField: DocumentField,
        fieldIndex: Int
    ): Pair<DocumentField?, DocumentField?> {
        return when (parentField) {
            is DocumentField.ArrayValue -> {
                val parentList = parentField.values.toMutableList()
                val removedDocumentField = parentList.removeAt(fieldIndex)
                parentField.copy(values = parentList) to removedDocumentField
            }
            is DocumentField.MapValue -> {
                val parentList = parentField.values.toMutableList()
                val removedDocumentField = parentList.removeAt(fieldIndex)
                parentField.copy(values = parentList) to removedDocumentField
            }
            else -> null to null
        }
    }

    fun String.splitPath(): Array<String> {
        val lastSlash = lastIndexOf("/")
        val firstSubstring = substring(0, lastSlash)
        val collectionId = substring(lastSlash + 1, length)
        return arrayOf(firstSubstring, collectionId)
    }
}

data class DatabaseUiState(
    val errorState: DatabaseErrorState? = null,
    val isRefreshing: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val isCollectionClicked: Boolean = false,
    val isDocumentRefreshed: Boolean = false,
    val currentPath: String? = null,
    val currentDocument: Document? = null,
    val isLoading: Boolean = false,
    val isBottomSheetOpened: Boolean = false,
    val currentEditedField: DocumentField? = null,
    val currentEditedFieldParentIndex: Int? = null,
    val currentEditedFieldIndex: Int? = null,
    val isDocumentFieldEditing: Boolean = false
)

enum class DatabaseErrorState(@StringRes val titleResId: Int, @StringRes val actionResId: Int?) {
    LIST_COLLECTION_IDS(R.string.error_occured, R.string.retry),
    LIST_DOCUMENTS(R.string.error_occured, R.string.retry),
    GET_DOCUMENT(R.string.error_occured, R.string.retry),
    PATCH_DOCUMENT(R.string.error_occured, R.string.retry),
}

sealed class DatabaseEvent {
    object Back : DatabaseEvent()
    data class NavigateToCreateDocumentScreen(val path: String, val isCollection: Boolean) :
        DatabaseEvent()

    data class NavigateToDeleteDocumentScreen(val path: String, val isCollection: Boolean) :
        DatabaseEvent()
}
