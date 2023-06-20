package com.batuhan.management.presentation.projectlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.util.AuthStateManager
import com.batuhan.management.domain.firebase.GetProjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val getProjects: GetProjects,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    val projects: Flow<PagingData<FirebaseProject>> =
        getProjects.invoke().cachedIn(viewModelScope)

    private val projectsScreenEvent = Channel<ProjectsScreenEvent> { Channel.BUFFERED }
    val projectsScreenEventFlow = projectsScreenEvent.receiveAsFlow()

    fun endSession() {
        viewModelScope.launch {
            authStateManager.deleteAuthState()
            projectsScreenEvent.send(ProjectsScreenEvent.Logout)
        }
    }

    fun createProject() {
        viewModelScope.launch {
            projectsScreenEvent.send(ProjectsScreenEvent.CreateProject)
        }
    }

    fun openProjectInfo(project: FirebaseProject) {
        viewModelScope.launch {
            projectsScreenEvent.send(ProjectsScreenEvent.ProjectDetail(project))
        }
    }

    fun closeAlert() {
        viewModelScope.launch {
            projectsScreenEvent.send(ProjectsScreenEvent.CloseAlert)
        }
    }

    fun openAlert(message: String, throwable: Throwable) {
        viewModelScope.launch {
            projectsScreenEvent.send(ProjectsScreenEvent.OpenAlert(message, throwable))
        }
    }
}

sealed class ProjectsScreenEvent {

    data class ProjectDetail(val project: FirebaseProject) : ProjectsScreenEvent()
    data class Project(val project: FirebaseProject) : ProjectsScreenEvent()
    data class OpenAlert(val errorMessage: String, val throwable: Throwable) : ProjectsScreenEvent()

    object CreateProject : ProjectsScreenEvent()
    object CloseAlert : ProjectsScreenEvent()
    object Logout : ProjectsScreenEvent()
}
