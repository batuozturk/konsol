package com.batuhan.management.presentation.project.settings.actions.webapps

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.management.R
import com.batuhan.management.data.model.WebApp
import com.batuhan.theme.Orange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebApps(onBackPressed: () -> Unit, createWebApp: (String) -> Unit) {
    val viewModel = hiltViewModel<WebAppsViewModel>()
    val webApps = viewModel.apps.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = {
            if (it != null) {
                val contentResolver = context.contentResolver
                viewModel.getConfigFile(contentResolver, it, uiState.selectedWebApp?.appId!!)
            }
        }
    )
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { false }
        )
    LaunchedEffect(key1 = true) {
        viewModel.webAppsEvent.collect { event ->
            when (event) {
                is WebAppsEvent.Back -> onBackPressed.invoke()
                is WebAppsEvent.CreateWebApp -> createWebApp.invoke(event.projectId)
                is WebAppsEvent.SaveConfigFileToDirectory -> {
                    launcher.launch("web-config.json")
                }
                is WebAppsEvent.WebAppInfo -> {
                    viewModel.setBottomSheetOpened(true)
                }
                is WebAppsEvent.CloseBottomSheet -> {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }.invokeOnCompletion {
                        viewModel.setBottomSheetOpened(false)
                        viewModel.clearWebAppInfo()
                    }
                }
            }
        }
    }
    WebAppsContent(
        uiState = uiState,
        webApps = webApps,
        sheetState = bottomSheetState,
        onBackPressed = viewModel::onBackPressed,
        createWebApp = viewModel::createWebApp,
        onInfoClicked = viewModel::onInfoClicked,
        onRefresh = viewModel::setRefreshState,
        onConfigFileClicked = viewModel::onConfigFileClicked,
        hideBottomSheet = viewModel::hideBottomSheet,
        setSnackbarState = viewModel::setSnackbarState,
        setErrorState = viewModel::setErrorState,
        retryOperation = viewModel::retryOperation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebAppsContent(
    uiState: WebAppsUiState,
    webApps: LazyPagingItems<WebApp>,
    sheetState: SheetState,
    hideBottomSheet: () -> Unit,
    onBackPressed: () -> Unit,
    onInfoClicked: (WebApp) -> Unit,
    createWebApp: () -> Unit,
    onRefresh: (() -> Unit) -> Unit,
    onConfigFileClicked: () -> Unit,
    setSnackbarState: (Boolean) -> Unit,
    setErrorState: (WebAppsErrorState) -> Unit,
    retryOperation: (WebAppsErrorState, (() -> Unit)?) -> Unit
) {
    val isRefreshing by remember(uiState.isRefreshing) {
        derivedStateOf { uiState.isRefreshing }
    }
    val isLoading by remember(uiState.isLoading) {
        derivedStateOf { uiState.isLoading }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }
    val isFileSaved by remember(uiState.isFileSaved) {
        derivedStateOf { uiState.isFileSaved }
    }
    val isSnackbarOpened by remember(uiState.isSnackbarOpened) {
        derivedStateOf { uiState.isSnackbarOpened }
    }
    val selectedApp by remember(uiState.selectedWebApp) {
        derivedStateOf { uiState.selectedWebApp }
    }
    val isBottomSheetOpened by remember(uiState.isBottomSheetOpened) {
        derivedStateOf { uiState.isBottomSheetOpened }
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
    LaunchedEffect(isSnackbarOpened) {
        errorState?.titleResId?.takeIf { isSnackbarOpened }?.let {
            val title = context.getString(it)
            val actionTitle = errorState?.actionResId?.let { resId -> context.getString(resId) }
            when (
                snackbarHostState.showSnackbar(
                    title,
                    actionLabel = actionTitle,
                    withDismissAction = actionTitle == null,
                    duration = SnackbarDuration.Indefinite
                )
            ) {
                SnackbarResult.ActionPerformed -> {
                    retryOperation.invoke(errorState!!) {
                        webApps.refresh()
                    }
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                SnackbarResult.Dismissed -> {
                    retryOperation.invoke(errorState!!, null)
                    setSnackbarState.invoke(false)
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    shape = RoundedCornerShape(10.dp),
                    snackbarData = it,
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    actionColor = Color.White
                )
            }
        },
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
                    Text(stringResource(id = R.string.web_apps))
                },
                actions = {
                    if (errorState != null) {
                        IconButton(
                            onClick = {
                                setSnackbarState.invoke(true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                    IconButton(onClick = createWebApp) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            tint = Orange,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                setSnackbarState.invoke(false)
                snackbarHostState.currentSnackbarData?.dismiss()
                onRefresh.invoke {
                    webApps.refresh()
                }
            },
            modifier = Modifier.fillMaxSize().padding(it)
        ) {
            when (webApps.loadState.refresh) {
                is LoadState.Error -> {
                    setErrorState.invoke(WebAppsErrorState.WEB_APPS)
                }
                else -> {
                }
            }

            when (webApps.loadState.append) {
                is LoadState.Error -> {
                    setErrorState.invoke(WebAppsErrorState.WEB_APPS)
                }
                else -> {
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(webApps.itemCount) { index ->
                    webApps[index]?.let { app ->
                        WebAppItem(
                            webApp = app,
                            onInfoSelected = {
                                onInfoClicked.invoke(app)
                            }
                        )
                    }
                }
            }
        }
    }
    if (isBottomSheetOpened) {
        val list = generateWebInfoItem(selectedApp!!)
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = sheetState,
            dragHandle = {},
            onDismissRequest = {
                hideBottomSheet.invoke()
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        hideBottomSheet.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Orange
                    )
                }
                Button(
                    onClick = onConfigFileClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .height(16.dp)
                                .aspectRatio(1f),
                            strokeWidth = 2.dp
                        )
                    }
                    Text(
                        (if (isLoading) "Downloading " else "Download ") + "web-config.json",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
            LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
                items(list.size) {
                    WebAppInfoItem(pair = list[it])
                }
            }
        }
    }
}

fun generateWebInfoItem(webApp: WebApp): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()
    list.add(Pair("App ID", webApp.appId ?: "undefined"))
    list.add(Pair("Display Name", webApp.displayName ?: "undefined"))
    list.add(Pair("Project ID", webApp.projectId ?: "undefined"))
    list.add(Pair("API Key ID", webApp.apiKeyId ?: "undefined"))
    list.add(Pair("State", webApp.state?.name ?: "undefined"))
    return list
}

@Composable
fun WebAppItem(
    webApp: WebApp,
    onInfoSelected: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = webApp.displayName ?: "undefined")
            IconButton(onClick = onInfoSelected) {
                Icon(imageVector = Icons.Default.Info, tint = Orange, contentDescription = null)
            }
        }
    }
}

@Composable
fun WebAppInfoItem(
    pair: Pair<String, String>
) {
    Column(
        Modifier.fillMaxWidth().padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp)).padding(10.dp)
    ) {
        Text(text = pair.first)
        Text(text = pair.second)
    }
}
