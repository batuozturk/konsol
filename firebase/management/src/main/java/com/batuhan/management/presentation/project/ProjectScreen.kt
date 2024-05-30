package com.batuhan.management.presentation.project

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.management.data.model.FeatureItem
import com.batuhan.management.data.model.FeatureItemRoute
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange

@Composable
fun ProjectScreen(
    projectName: String,
    onBackPressed: () -> Unit,
    navigate: (route: FeatureItemRoute, projectId: String) -> Unit,
    navigateToProjectSettings: (projectId: String, projectName: String) -> Unit
) {
    val viewModel = hiltViewModel<ProjectViewModel>()
    val featureList by viewModel.featureItemList.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.projectScreenEvent.collect { event ->
            when (event) {
                is ProjectScreenEvent.Back -> onBackPressed.invoke()
                is ProjectScreenEvent.Settings -> {
                    navigateToProjectSettings(event.projectId, event.projectName)
                }
                is ProjectScreenEvent.FeatureRoute -> {
                    navigate(event.route, event.projectId)
                }
            }
        }
    }
    ProjectScreenContent(
        projectName = projectName,
        featureList = featureList,
        onBackPressed = viewModel::onBackPressed,
        onSettingsClicked = viewModel::onSettingsClicked,
        onFeatureClicked = viewModel::onFeatureClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreenContent(
    projectName: String,
    featureList: List<FeatureItem>,
    onBackPressed: () -> Unit,
    onSettingsClicked: () -> Unit,
    onFeatureClicked: (FeatureItem) -> Unit
) {
    Scaffold(
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
                    Text(projectName ?: "")
                },
                actions = {
                    TopAppBarAction(onSettingsClicked = onSettingsClicked)
                }

            )
        }
    ) {
        Surface(modifier = Modifier.fillMaxSize().padding(it)) {
            LazyColumn {
                items(featureList.size) {
                    FeatureListItem(
                        featureItem = featureList[it],
                        onFeatureClicked = onFeatureClicked
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureListItem(featureItem: FeatureItem, onFeatureClicked: (FeatureItem) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onFeatureClicked.invoke(featureItem)
            }
            .border(2.dp, featureItem.tintColor, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Icon(
            imageVector = featureItem.icon,
            contentDescription = null,
            tint = featureItem.tintColor,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = featureItem.titleResId),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = featureItem.descriptionResId),
            color = Color.Black
        )
    }
}

@Composable
fun TopAppBarAction(onSettingsClicked: () -> Unit) {
    IconButton(onClick = onSettingsClicked) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = Orange
        )
    }
}

@Preview
@Composable
fun ProjectScreenPreview() {
    KonsolTheme {
        ProjectScreenContent(
            projectName = "",
            featureList = listOf(),
            onBackPressed = { /*TODO*/ },
            onSettingsClicked = {},
            onFeatureClicked = {}
        )
    }
}
