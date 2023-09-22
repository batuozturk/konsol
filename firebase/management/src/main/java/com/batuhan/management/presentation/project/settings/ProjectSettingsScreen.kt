package com.batuhan.management.presentation.project.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.core.data.model.management.*
import com.batuhan.management.data.model.SettingsItem
import com.batuhan.management.data.model.SettingsItemRoute
import com.batuhan.theme.Orange

@Composable
fun ProjectSettingsScreen(
    projectId: String,
    projectName: String,
    navigate: (SettingsItemRoute) -> Unit,
    onBackPressed: () -> Unit
) {
    val viewModel = hiltViewModel<ProjectSettingsViewModel>()
    val actions by viewModel.actions.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.projectSettingsEvent.collect { event ->
            when (event) {
                is ProjectSettingsEvent.Back -> onBackPressed.invoke()
                is ProjectSettingsEvent.Route -> navigate(event.settingsItemRoute)
            }
        }
    }
    ProjectSettingsScreenContent(
        actions = actions,
        projectName = projectName,
        handleAction = viewModel::handleAction,
        onBackPressed = viewModel::onBackPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSettingsScreenContent(
    projectName: String,
    actions: List<SettingsItem>,
    handleAction: (SettingsItem) -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            tint = Orange,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(projectName)
                }
            )
        }
    ) {
        SettingsList(modifier = Modifier.padding(it), actions = actions, onClick = handleAction)
    }
}
