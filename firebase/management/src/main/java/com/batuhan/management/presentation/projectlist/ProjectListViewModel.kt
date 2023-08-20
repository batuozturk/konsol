package com.batuhan.management.presentation.projectlist

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.util.AuthStateManager
import com.batuhan.management.R
import com.batuhan.management.domain.firebase.GetProjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectListViewModel @Inject constructor(
    private val getProjects: GetProjects,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    val projects: Flow<PagingData<FirebaseProject>> =
        getProjects.invoke().cachedIn(viewModelScope)

    private val projectsScreenEvent = Channel<ProjectsScreenEvent> { Channel.BUFFERED }
    val projectsScreenEventFlow = projectsScreenEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState = _uiState.asStateFlow()

    fun endSession() {
        viewModelScope.launch {
            authStateManager.deleteAuthState()
            projectsScreenEvent.send(ProjectsScreenEvent.Logout)
        }
    }

    fun onRefresh(operation: () -> Unit) {
        viewModelScope.launch {
            setRefreshingState(true)
            operation.invoke()
            delay(3000L)
            setRefreshingState(false)
        }
    }

    fun createProject() {
        viewModelScope.launch {
            projectsScreenEvent.send(ProjectsScreenEvent.CreateProject)
        }
    }

    fun openProject(project: FirebaseProject) {
        viewModelScope.launch {
            projectsScreenEvent.send(ProjectsScreenEvent.Project(project))
        }
    }

    fun setRefreshingState(isRefreshing: Boolean) {
        _uiState.update {
            it.copy(isRefreshing = isRefreshing)
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

    fun setSelectedProject(selectedProject: FirebaseProject) {
        _uiState.update {
            it.copy(selectedProject = selectedProject)
        }
    }

    fun setErrorState(errorState: ProjectListErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun retryOperation(errorState: ProjectListErrorState, operation: (() -> Unit)?) {
        when (errorState) {
            ProjectListErrorState.PROJECT_LIST -> {
                onRefresh {
                    operation?.invoke()
                }
            }
        }
    }

    fun setBottomSheetState(isBottomSheetOpened: Boolean) {
        _uiState.update {
            it.copy(isBottomSheetOpened = isBottomSheetOpened)
        }
    }
}

data class ProjectListUiState(
    val isRefreshing: Boolean = false,
    val errorState: ProjectListErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val selectedProject: FirebaseProject? = null,
    val isBottomSheetOpened: Boolean = false
)

enum class ProjectListErrorState(@StringRes val titleResId: Int, @StringRes val actionResId: Int?) {
    PROJECT_LIST(R.string.error_occurred, R.string.retry)
}

sealed class ProjectsScreenEvent {
    data class Project(val project: FirebaseProject) : ProjectsScreenEvent()
    object CreateProject : ProjectsScreenEvent()
    object Logout : ProjectsScreenEvent()
}
