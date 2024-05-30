package com.batuhan.management.presentation.project.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.core.util.Result
import com.batuhan.core.data.model.management.*
import com.batuhan.management.data.model.SettingsItem
import com.batuhan.management.data.model.SettingsItemRoute
import com.batuhan.management.domain.firebase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectSettingsViewModel @Inject constructor(
    private val getProject: GetProject,
    private val generateSettingsList: GenerateSettingsList,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val KEY_PROJECT_ID = "projectId"
    }

    private var projectId = savedStateHandle.get<String>(KEY_PROJECT_ID) ?: ""

    private val _project: MutableStateFlow<FirebaseProject?> = MutableStateFlow(null)
    val project = _project.asStateFlow()

    private val _actions: MutableStateFlow<List<SettingsItem>> = MutableStateFlow(listOf())
    val actions = _actions.asStateFlow()

    private val _projectSettingsEvent = Channel<ProjectSettingsEvent> { Channel.BUFFERED }
    val projectSettingsEvent = _projectSettingsEvent.receiveAsFlow()

    init {
        generateSettingsList()
        getProject()
    }

    fun getProject() {
        viewModelScope.launch {
            when (val result = getProject.invoke(GetProject.Params(projectId))) {
                is Result.Success -> {
                    _project.value = result.data
                }
                is Result.Error -> {
                }
            }
        }
    }

    fun generateSettingsList() {
        viewModelScope.launch {
            _actions.value = generateSettingsList.invoke()
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _projectSettingsEvent.send(ProjectSettingsEvent.Back)
        }
    }

    fun handleAction(settingsItem: SettingsItem) {
        viewModelScope.launch {
            _projectSettingsEvent.send(ProjectSettingsEvent.Route(settingsItem.route))
        }
    }
}

sealed class ProjectSettingsEvent {
    object Back : ProjectSettingsEvent()
    data class Route(val settingsItemRoute: SettingsItemRoute) : ProjectSettingsEvent()
}
