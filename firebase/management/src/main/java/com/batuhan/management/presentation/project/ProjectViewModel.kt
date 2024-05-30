package com.batuhan.management.presentation.project

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.management.data.model.FeatureItem
import com.batuhan.management.data.model.FeatureItemRoute
import com.batuhan.management.domain.firebase.GenerateFeatureList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val generateFeatureList: GenerateFeatureList,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var project: FirebaseProject? = null

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        private const val KEY_PROJECT_NAME = "projectName"
    }

    private var projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)
    private var projectName = savedStateHandle.get<String>(KEY_PROJECT_NAME)

    private val _projectScreenEvent = Channel<ProjectScreenEvent> { Channel.BUFFERED }
    val projectScreenEvent = _projectScreenEvent.receiveAsFlow()

    private val _featureItemList = MutableStateFlow<List<FeatureItem>>(listOf())
    val featureItemList = _featureItemList.asStateFlow()

    init {
        generateFeatureList()
    }

    fun setProject(firebaseProject: FirebaseProject) {
        project = firebaseProject
    }

    fun generateFeatureList() {
        viewModelScope.launch {
            _featureItemList.value = generateFeatureList.invoke()
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _projectScreenEvent.send(ProjectScreenEvent.Back)
        }
    }

    fun onSettingsClicked() {
        viewModelScope.launch {
            _projectScreenEvent.send(ProjectScreenEvent.Settings(projectId!!, projectName!!))
        }
    }

    fun onFeatureClicked(featureItem: FeatureItem) {
        viewModelScope.launch {
            _projectScreenEvent.send(
                ProjectScreenEvent.FeatureRoute(
                    projectId!!,
                    featureItem.route
                )
            )
        }
    }
}

sealed class ProjectScreenEvent {
    object Back : ProjectScreenEvent()
    data class FeatureRoute(
        val projectId: String,
        val route: FeatureItemRoute
    ) : ProjectScreenEvent()

    data class Settings(val projectId: String, val projectName: String) : ProjectScreenEvent()
}
