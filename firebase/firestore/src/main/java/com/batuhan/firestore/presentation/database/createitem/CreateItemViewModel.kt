package com.batuhan.firestore.presentation.database.createitem

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.Result
import com.batuhan.firestore.R
import com.batuhan.core.data.model.firestore.Document
import com.batuhan.firestore.data.model.DocumentField
import com.batuhan.firestore.domain.documents.CreateDocument
import com.batuhan.firestore.util.checkValidation
import com.batuhan.firestore.util.toDocumentFieldMap
import com.batuhan.firestore.util.updateDocumentFieldErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateItemViewModel @Inject constructor(
    private val createDocument: CreateDocument,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_CREATE_COLLECTION = "createCollection"
    }

    private val path = Uri.decode(savedStateHandle.get<String>(KEY_NAME))
    private val createCollection = savedStateHandle.get<Boolean>(KEY_CREATE_COLLECTION) ?: false

    private val _uiState =
        MutableStateFlow(CreateItemUiState(createCollection = createCollection))
    val uiState = _uiState.asStateFlow()

    private val _createItemEvent = Channel<CreateItemEvent> { Channel.BUFFERED }
    val createItemEvent = _createItemEvent.receiveAsFlow()

    fun createDocument() {
        val collectionIdInput =
            uiState.value.collectionId.takeIf { it?.isNotEmpty() ?: false && it?.isNotBlank() ?: false }
        if (collectionIdInput == null && createCollection) {
            setErrorState(CreateItemErrorState.COLLECTION_ID_EMPTY)
            return
        }
        viewModelScope.launch {
            val splittedPath =
                if (createCollection) arrayOf(path, collectionIdInput)
                else path!!.splitPath()
            setLoadingState(true)
            val documentPath = splittedPath[0]
            val collectionId = splittedPath[1]
            val documentId =
                uiState.value.documentId.takeIf { it?.isNotEmpty() ?: false || it?.isNotBlank() ?: false }
            val result = createDocument.invoke(
                CreateDocument.Params(
                    documentPath ?: return@launch,
                    collectionId ?: return@launch,
                    documentId,
                    Document(fields = uiState.value.valuesList.toDocumentFieldMap())
                )
            )
            when (result) {
                is Result.Success -> {
                    setLoadingState(false)
                    onBackPressed(true)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateItemErrorState.CREATE_DOCUMENT)
                }
            }
        }
    }

    fun editDocumentField(
        field: DocumentField,
        fieldIndex: Int? = null,
        parentFieldIndex: Int? = null
    ) {
        val parentField = parentFieldIndex?.let { uiState.value.valuesList[it] }
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
                currentEditedFieldIndex = null,
                isBottomSheetOpened = false
            )
        }
    }

    fun onSave() {
        val currentEditedField = uiState.value.currentEditedField ?: return
        val currentEditedFieldParentIndex = uiState.value.currentEditedFieldParentIndex
        val parentDocumentField =
            currentEditedFieldParentIndex?.let { uiState.value.valuesList[it] }
        val currentEditedFieldIndex = uiState.value.currentEditedFieldIndex
        editDocumentFieldList(
            currentEditedField,
            parentDocumentField,
            currentEditedFieldIndex,
            currentEditedFieldParentIndex
        )
    }

    fun editDocumentFieldList(
        field: DocumentField,
        parentDocumentField: DocumentField? = null,
        fieldIndex: Int?,
        parentDocumentFieldIndex: Int? = null
    ) {
        _uiState.update {
            val valuesList = it.valuesList.toMutableList()
            if (parentDocumentField != null && parentDocumentFieldIndex != null) {
                editParentFieldList(field, parentDocumentField, fieldIndex)
                    ?.let { updatedParent ->
                        valuesList[parentDocumentFieldIndex] = updatedParent
                    } ?: run {
                    // no-op
                }
            } else {
                val isDuplicateAttributeName =
                    valuesList.any { it.attributeName == field.attributeName }
                val attributeIndex =
                    valuesList.indexOfFirst { it.attributeName == field.attributeName }
                val isSameIndex = attributeIndex == fieldIndex
                if (isDuplicateAttributeName && !isSameIndex) {
                    valuesList[attributeIndex] = field
                } else {
                    if ((fieldIndex ?: valuesList.size) >= valuesList.size) {
                        valuesList.add(field)
                    } else {
                        valuesList[fieldIndex!!] = field
                    }
                }
            }

            if (uiState.value.errorState != null) {
                return
            }
            clearEditedField()
            it.copy(
                valuesList = valuesList
            )
        }
    }

    fun editParentFieldList(
        field: DocumentField,
        parentDocumentField: DocumentField,
        fieldIndex: Int?
    ): DocumentField? {
        return when (parentDocumentField) {
            is DocumentField.ArrayValue -> {
                val parentList = parentDocumentField.values.toMutableList()
                if ((fieldIndex ?: parentList.size) >= parentList.size) {
                    parentList.add(field)
                } else {
                    parentList[fieldIndex!!] = field
                }

                parentDocumentField.copy(values = parentList)
            }
            is DocumentField.MapValue -> {
                val parentList = parentDocumentField.values.toMutableList()

                if ((fieldIndex ?: parentList.size) >= parentList.size) {
                    parentList.add(field)
                } else {
                    parentList[fieldIndex!!] = field
                }
                parentDocumentField.copy(values = parentList)
            }
            else -> null
        }
    }

    fun removeDocumentFieldList(
        fieldIndex: Int?,
        parentFieldIndex: Int? = null
    ) {
        fieldIndex ?: return
        _uiState.update {
            val valuesList = it.valuesList.toMutableList()
            if (parentFieldIndex != null) {
                val parentDocumentField = it.valuesList[parentFieldIndex]
                removeParentFieldList(parentDocumentField, fieldIndex)
                    ?.let { updatedParent ->
                        valuesList[parentFieldIndex] = updatedParent
                    }
            } else {
                valuesList.removeAt(fieldIndex)
            }

            it.copy(valuesList = valuesList)
        }
    }

    fun removeParentFieldList(
        parentField: DocumentField,
        fieldIndex: Int
    ): DocumentField? {
        return when (parentField) {
            is DocumentField.ArrayValue -> {
                val parentList = parentField.values.toMutableList()
                parentList.removeAt(fieldIndex)
                parentField.copy(values = parentList)
            }
            is DocumentField.MapValue -> {
                val parentList = parentField.values.toMutableList()
                parentList.removeAt(fieldIndex)
                parentField.copy(values = parentList)
            }
            else -> null
        }
    }

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) clearErrorState()
    }

    fun setErrorState(createItemErrorState: CreateItemErrorState) {
        _uiState.update {
            it.copy(errorState = createItemErrorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun onBackPressed(needsRefresh: Boolean = false) {
        viewModelScope.launch {
            _createItemEvent.send(CreateItemEvent.Back(needsRefresh = needsRefresh))
        }
    }

    fun setBottomSheetState(isBottomSheetOpened: Boolean) {
        _uiState.update {
            it.copy(isBottomSheetOpened = isBottomSheetOpened)
        }
        if (!isBottomSheetOpened) setDocumentFieldEdited(false)
    }

    fun updateCollectionId(collectionId: String) {
        _uiState.update {
            it.copy(collectionId = collectionId)
        }
    }

    fun updateDocumentId(documentId: String) {
        _uiState.update {
            it.copy(documentId = documentId)
        }
    }

    fun retryOperation(errorState: CreateItemErrorState) {
        when (errorState) {
            CreateItemErrorState.CREATE_DOCUMENT -> createDocument()
            else -> {
                // no-op
            }
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun setDocumentFieldEdited(isDocumentFieldEditing: Boolean) {
        _uiState.update {
            it.copy(isDocumentFieldEditing = isDocumentFieldEditing)
        }
    }

    fun String.splitPath(): Array<String> {
        val slashIndex = lastIndexOf("/")
        val path = substring(0, slashIndex)
        val collectionId = substring(slashIndex + 1)
        return arrayOf(path, collectionId)
    }
}

data class CreateItemUiState(
    val errorState: CreateItemErrorState? = null,
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val valuesList: List<DocumentField> = listOf(),
    val currentEditedField: DocumentField? = null,
    val currentEditedFieldParentIndex: Int? = null,
    val currentEditedFieldIndex: Int? = null,
    val isBottomSheetOpened: Boolean = false,
    val createCollection: Boolean = false,
    val collectionId: String? = null,
    val documentId: String? = null,
    val isDocumentFieldEditing: Boolean = false
)

sealed class CreateItemEvent {
    data class Back(val needsRefresh: Boolean) : CreateItemEvent()
}

enum class CreateItemErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int?
) {
    CREATE_DOCUMENT(R.string.error_occured, R.string.retry),
    FIELD_IS_EMPTY(R.string.error_occured, R.string.retry),
    COLLECTION_ID_EMPTY(R.string.collection_id_empty, null)
}
