package com.batuhan.oauth2.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.oauth2.R
import com.batuhan.oauth2.presentation.pages.CloudMessagingInfoScreen
import com.batuhan.oauth2.presentation.pages.CloudStorageInfoScreen
import com.batuhan.oauth2.presentation.pages.FirestoreInfoScreen
import com.batuhan.oauth2.presentation.pages.TestLabInfoScreen
import com.batuhan.theme.KonsolFontFamily
import com.batuhan.theme.KonsolTheme
import com.batuhan.theme.Orange
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

private object ConstraintParams {
    const val REF_EMAIL_INPUT = "email_input"
    const val REF_SIGN_IN_BUTTON = "button"
    const val REF_HORIZONTAL_PAGER = "horizontal_pager"
    const val REF_PAGE_INDICATOR = "page_indicator"
    const val REF_PRIVACY_POLICY_TOS_ROW = "privacy_policy_tos"
}

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToProjectListScreen: () -> Unit,
    launchUrl: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val authorizationService: AuthorizationService by remember {
        mutableStateOf(
            AuthorizationService(
                context
            )
        )
    }
    val oauth2Result = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.let { data ->
            val resp: AuthorizationResponse? = AuthorizationResponse.fromIntent(data)
            val ex: AuthorizationException? = AuthorizationException.fromIntent(data)
            viewModel.updateAuthState(resp, ex)
            ex?.let {
                Toast.makeText(context, ex.errorDescription, Toast.LENGTH_LONG)
                    .show()
                return@rememberLauncherForActivityResult
            }
            resp?.let { response ->
                viewModel.getOauth2Token(response, authorizationService)
            }
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                authorizationService.dispose()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            authorizationService.dispose()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(true) {
        viewModel.authEvent.collect { event ->
            when (event) {
                is AuthScreenEvent.Success -> navigateToProjectListScreen.invoke()
                is AuthScreenEvent.LaunchIntent -> oauth2Result.launch(event.intent)
            }
        }
    }
    val uiState by viewModel.authScreenUiState.collectAsStateWithLifecycle()
    AuthScreenContent(
        uiState = uiState,
        onValueChanged = viewModel::updateEmail,
        sendAuthRequest = {
            viewModel.sendAuthRequest(authorizationService)
        },
        clearErrorState = viewModel::clearErrorState,
        retryOperation = viewModel::retryOperation,
        launchUrl = launchUrl
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AuthScreenContent(
    uiState: AuthScreenUiState,
    onValueChanged: (String) -> Unit,
    sendAuthRequest: () -> Unit,
    clearErrorState: () -> Unit,
    retryOperation: (AuthScreenErrorState) -> Unit,
    launchUrl: (String) -> Unit
) {
    val constraint = ConstraintSet {
        val horizontalPager = createRefFor(ConstraintParams.REF_HORIZONTAL_PAGER)
        val emailInput = createRefFor(ConstraintParams.REF_EMAIL_INPUT)
        val signInButton = createRefFor(ConstraintParams.REF_SIGN_IN_BUTTON)
        val pageIndicator = createRefFor(ConstraintParams.REF_PAGE_INDICATOR)
        val privacyPolicyAndTermsOfService =
            createRefFor(ConstraintParams.REF_PRIVACY_POLICY_TOS_ROW)
        constrain(horizontalPager) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(pageIndicator.top)
            width = Dimension.matchParent
            height = Dimension.fillToConstraints
        }
        constrain(pageIndicator) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(emailInput.top)
            width = Dimension.matchParent
            height = Dimension.wrapContent
        }
        constrain(emailInput) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(signInButton.top)
            width = Dimension.matchParent
            height = Dimension.wrapContent
        }
        constrain(signInButton) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(privacyPolicyAndTermsOfService.top)
            width = Dimension.matchParent
            height = Dimension.wrapContent
        }
        constrain(privacyPolicyAndTermsOfService) {
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }
    }
    val errorState by remember(uiState.errorState) {
        derivedStateOf { uiState.errorState }
    }

    val email by remember(uiState.email) {
        derivedStateOf { uiState.email }
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val pagerState = rememberPagerState { 4 }
    val context = LocalContext.current
    LaunchedEffect(errorState) {
        errorState?.titleResId?.let {
            val titleText = context.getString(it)
            val actionText = errorState?.actionResId?.let { resId -> context.getString(resId) }
            val result = snackbarHostState.showSnackbar(
                message = titleText,
                actionLabel = actionText,
                withDismissAction = actionText == null,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    clearErrorState.invoke()
                    snackbarHostState.currentSnackbarData?.dismiss()
                    retryOperation.invoke(errorState!!)
                }
                SnackbarResult.Dismissed -> {
                    clearErrorState.invoke()
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name_title),
                        fontFamily = KonsolFontFamily,
                        color = Orange,
                        fontSize = 32.sp
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    contentColor = Color.White,
                    containerColor = Color.Red,
                    actionColor = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    ) {
        ConstraintLayout(
            constraint,
            modifier = Modifier.fillMaxSize()
                .padding(it).padding(8.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    .layoutId(ConstraintParams.REF_HORIZONTAL_PAGER)
            ) { page ->
                when (page) {
                    0 -> FirestoreInfoScreen()
                    1 -> TestLabInfoScreen()
                    2 -> CloudStorageInfoScreen()
                    3 -> CloudMessagingInfoScreen()
                }
            }
            Row(
                Modifier.fillMaxWidth().layoutId(ConstraintParams.REF_PAGE_INDICATOR),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Orange else Orange.copy(alpha = 0.4f)
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(10.dp)

                    )
                }
            }
            OutlinedTextField(
                value = email ?: "",
                onValueChange = onValueChanged,
                label = {
                    Text(
                        text = stringResource(id = R.string.email),
                        style = TextStyle(color = Orange)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Orange,
                    unfocusedLabelColor = Orange,
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Orange,
                    cursorColor = Orange,
                    selectionColors = TextSelectionColors(
                        handleColor = Orange,
                        backgroundColor = Orange.copy(alpha = 0.4f)
                    )
                ),
                modifier = Modifier.padding(16.dp)
                    .layoutId(ConstraintParams.REF_EMAIL_INPUT)
            )
            Button(
                onClick = { sendAuthRequest.invoke() },
                modifier = Modifier.padding(horizontal = 16.dp)
                    .layoutId(ConstraintParams.REF_SIGN_IN_BUTTON),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text(text = stringResource(id = R.string.sign_in))
            }
            Row(
                modifier = Modifier.padding(top = 64.dp, bottom = 32.dp)
                    .layoutId(ConstraintParams.REF_PRIVACY_POLICY_TOS_ROW),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).clickable {
                        launchUrl.invoke("https://getkonsol.app/privacy-policy")
                    },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(id = R.string.privacy_policy))
                }

                Row(
                    modifier = Modifier.weight(1f).clickable {
                        launchUrl.invoke("https://getkonsol.app/terms-of-service")
                    },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(id = R.string.terms_of_service))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun DefaultPreview() {
    KonsolTheme {
        AuthScreenContent(
            AuthScreenUiState(),
            onValueChanged = {},
            sendAuthRequest = {},
            clearErrorState = {},
            retryOperation = {},
            launchUrl = {}
        )
    }
}
