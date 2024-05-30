package com.batuhan.management.presentation.project.settings.actions.deleteproject

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.util.Result
import com.batuhan.management.R
import com.batuhan.management.domain.googlecloud.DeleteGoogleCloudProject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteProjectViewModel @Inject constructor(
    private val deleteGoogleCloudProject: DeleteGoogleCloudProject,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        internal const val KEY_PROJECT_ID = "projectId"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(DeleteProjectUiState(projectId = projectId))
    val uiState = _uiState.asStateFlow()

    private val _deleteProjectEvent = Channel<DeleteProjectEvent> { Channel.BUFFERED }
    val deleteProjectEvent = _deleteProjectEvent.receiveAsFlow()

    fun setErrorState(errorState: DeleteProjectErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun deleteProject() {
        if (uiState.value.projectId != uiState.value.editableProjectId) {
            setErrorState(DeleteProjectErrorState.PROJECT_ID_NOT_CORRECT)
            return
        }
        setLoadingState(true)
        viewModelScope.launch {
            val result = deleteGoogleCloudProject.invoke(
                DeleteGoogleCloudProject.Params(
                    projectId!!
                )
            )
            when (result) {
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(DeleteProjectErrorState.DELETE_PROJECT)
                }
                is Result.Success -> {
                    setLoadingState(false)
                    _deleteProjectEvent.send(DeleteProjectEvent.NavigateToStartDestination)
                }
            }
        }
        viewModelScope.launch {
            _deleteProjectEvent.send(DeleteProjectEvent.NavigateToStartDestination)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _deleteProjectEvent.send(DeleteProjectEvent.Back)
        }
    }

    fun onValueChange(value: String) {
        _uiState.update {
            it.copy(editableProjectId = value, errorState = null)
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
        if (!isSnackbarOpened) {
            clearErrorState()
        }
    }
}

sealed class DeleteProjectEvent {
    object Back : DeleteProjectEvent()
    object NavigateToStartDestination : DeleteProjectEvent()
}

data class DeleteProjectUiState(
    val errorState: DeleteProjectErrorState? = null,
    val projectId: String? = null,
    val isLoading: Boolean = false,
    val editableProjectId: String? = null,
    val isSnackbarOpened: Boolean = false
)

enum class DeleteProjectErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int? = null
) {
    PROJECT_ID_NOT_CORRECT(R.string.project_id_not_correct),
    DELETE_PROJECT(R.string.error_occurred, R.string.retry)
}
