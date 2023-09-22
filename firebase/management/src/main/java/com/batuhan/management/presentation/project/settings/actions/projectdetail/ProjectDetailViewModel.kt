package com.batuhan.management.presentation.project.settings.actions.projectdetail

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.data.model.DefaultResources
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.data.model.State
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.core.data.model.management.UpdateFirebaseProjectRequest
import com.batuhan.management.domain.firebase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val getProject: GetProject,
    private val updateFirebaseProject: UpdateFirebaseProject,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _updateProjectEvent = Channel<ProjectDetailEvent> { Channel.BUFFERED }
    val updateProjectEvent = _updateProjectEvent.receiveAsFlow()

    init {
        getProject()
    }

    fun getProject() {
        viewModelScope.launch {
            setLoadingState(true)
            when (val result = getProject.invoke(GetProject.Params(projectId = projectId!!))) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(projectAttributes = result.data.getAttributesList())
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setErrorState(ProjectDetailErrorState.PROJECT_INFO)
                    setLoadingState(false)
                }
            }
        }
    }

    fun setErrorState(errorState: ProjectDetailErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun updateProject() {
        val attributeList = mutableListOf<String>()
        val editableDisplayName = uiState.value.editableDisplayName
        val firebaseProject = uiState.value.projectAttributes?.getFirebaseProject()

        if (editableDisplayName != null && (editableDisplayName.isEmpty() || editableDisplayName.isBlank())) {
            setErrorState(ProjectDetailErrorState.DISPLAY_NAME_IS_EMPTY)
            return
        }

        if (editableDisplayName?.matches("[A-Za-z\\d\"'-]{4,}".toRegex()) != true) {
            setErrorState(ProjectDetailErrorState.DISPLAY_NAME_VALIDATION)
            return
        }

        if (editableDisplayName != firebaseProject?.displayName) attributeList.add(
            "displayName"
        )
        else {
            setErrorState(ProjectDetailErrorState.DISPLAY_NAME_IS_SAME)
            return
        }
        setLoadingState(true)
        viewModelScope.launch {
            val result = updateFirebaseProject.invoke(
                UpdateFirebaseProject.Params(
                    projectId!!,
                    attributeList.toFieldMask(),
                    UpdateFirebaseProjectRequest(displayName = editableDisplayName)
                )
            )
            when (result) {
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(ProjectDetailErrorState.UPDATE_PROJECT_ERROR)
                }
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            projectAttributes = result.data.getAttributesList(),
                            isEditing = false
                        )
                    }
                    setLoadingState(false)
                }
            }
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            _updateProjectEvent.send(ProjectDetailEvent.SaveChanges)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _updateProjectEvent.send(ProjectDetailEvent.Back)
        }
    }

    fun List<String>.toFieldMask(): String {
        val builder = StringBuilder()
        this.map { builder.append("$it,") }
        builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    fun updateDisplayName(displayName: String) {
        _uiState.update {
            it.copy(editableDisplayName = displayName)
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

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun retryOperation(errorState: ProjectDetailErrorState) {
        when (errorState) {
            ProjectDetailErrorState.PROJECT_INFO -> {
                getProject()
            }
            else -> {
                // no-op
            }
        }
    }

    fun navigateToSelectLocation() {
        viewModelScope.launch {
            _updateProjectEvent.send(ProjectDetailEvent.NavigateToSelectLocation(projectId!!))
        }
    }

    private fun FirebaseProject.getAttributesList(): List<Pair<Int, String>> {
        val attributeList = mutableListOf<Pair<Int, String>>()
        attributeList.add(Pair(R.string.project_id, projectId ?: "undefined"))
        attributeList.add(Pair(R.string.project_name, name ?: "undefined"))
        attributeList.add(Pair(R.string.project_display_name, displayName ?: "undefined"))
        attributeList.add(Pair(R.string.project_number, projectNumber?.toString() ?: "-1"))
        attributeList.add(Pair(R.string.project_state, state?.name ?: "undefined"))
        attributeList.add(
            Pair(
                R.string.project_hosting_site,
                resources?.hostingSite ?: "undefined"
            )
        )
        attributeList.add(Pair(R.string.project_location_id, resources?.locationId ?: "undefined"))
        attributeList.add(
            Pair(
                R.string.project_storage_bucket,
                resources?.storageBucket ?: "undefined"
            )
        )
        attributeList.add(
            Pair(
                R.string.project_rtdb_instance,
                resources?.realtimeDatabaseInstance ?: "undefined"
            )
        )
        return attributeList
    }

    private fun List<Pair<Int, String>>.getFirebaseProject(): FirebaseProject {
        return FirebaseProject(
            projectId = this[0].second,
            name = this[1].second,
            displayName = this[2].second,
            projectNumber = this[3].second.toLong(),
            state = State.valueOf(this[4].second),
            resources = DefaultResources(
                hostingSite = this[5].second,
                locationId = this[6].second,
                storageBucket = this[7].second,
                realtimeDatabaseInstance = this[8].second
            ),
            etag = null, annotations = null, prevPageToken = null
        )
    }

    fun setEditing(isEditing: Boolean) {
        _uiState.update {
            it.copy(isEditing = isEditing)
        }
        if (!isEditing) {
            _uiState.update {
                it.copy(editableDisplayName = null)
            }
        }
    }
}

sealed class ProjectDetailEvent {
    object Back : ProjectDetailEvent()
    object SaveChanges : ProjectDetailEvent()
    data class NavigateToSelectLocation(val projectId: String) : ProjectDetailEvent()
}

data class ProjectDetailUiState(
    val errorState: ProjectDetailErrorState? = null,
    val projectAttributes: List<Pair<Int, String>>? = null,
    val projectId: String? = null,
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val editableDisplayName: String? = null,
    val isEditing: Boolean = false
)

enum class ProjectDetailErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int? = null
) {
    PROJECT_INFO(R.string.error_occurred, R.string.retry),
    UPDATE_PROJECT_ERROR(R.string.error_occurred, R.string.retry),
    DISPLAY_NAME_IS_EMPTY(R.string.project_display_name),
    DISPLAY_NAME_IS_SAME(R.string.project_display_name_is_same),
    DISPLAY_NAME_VALIDATION(R.string.project_display_name_validation),
}
