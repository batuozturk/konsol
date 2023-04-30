package com.batuhan.oauth2.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batuhan.oauth2.R
import com.batuhan.theme.FConsoleTheme
import com.batuhan.theme.Orange
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

private object ConstraintParams {
    const val REF_TEXT = "text"
    const val REF_LOGO = "logo"
    const val REF_EMAIL_INPUT = "email_input"
    const val REF_SIGN_IN_BUTTON = "button"
    const val REF_QUESTION_BOX = "question_box"
    val DP_64 = 64.dp
    val DP_48 = 48.dp
    val DP_32 = 32.dp
    val DP_16 = 16.dp
}

object AuthScreenNavigationKeys {
    const val START_DESTINATION = "auth_screen"
    const val PROJECTS_SCREEN = "projects_screen"
}

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navigate: (key: String, popUpToScreen: String?, popUpInclusive: Boolean) -> Unit
) {
    val context = LocalContext.current
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
    LaunchedEffect(key1 = true, block = {
        viewModel.authEvent.collect { event ->
            when (event) {
                is AuthEvent.Success -> navigate(
                    AuthScreenNavigationKeys.PROJECTS_SCREEN,
                    AuthScreenNavigationKeys.START_DESTINATION,
                    true
                )
                is AuthEvent.LaunchIntent -> oauth2Result.launch(event.intent)
                else -> { // no-op
                }
            }
        }
    })
    val state by viewModel.authScreenState.collectAsStateWithLifecycle()
    AuthScreenContent(state, viewModel::onValueChanged) {
        viewModel.sendAuthRequest(authorizationService)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreenContent(
    state: AuthScreenState,
    onValueChanged: (String) -> Unit,
    sendAuthRequest: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        val constraint = ConstraintSet {
            val logo = createRefFor(ConstraintParams.REF_LOGO)
            val text = createRefFor(ConstraintParams.REF_TEXT)
            val emailInput = createRefFor(ConstraintParams.REF_EMAIL_INPUT)
            val signInButton = createRefFor(ConstraintParams.REF_SIGN_IN_BUTTON)
            val questionBox = createRefFor(ConstraintParams.REF_QUESTION_BOX)
            constrain(logo) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.value(250.dp)
                height = Dimension.value(250.dp)
            }
            constrain(text) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
            constrain(emailInput) {
                top.linkTo(text.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
            constrain(signInButton) {
                top.linkTo(emailInput.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
            constrain(questionBox) {
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
                start.linkTo(parent.start)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
            val chain =
                createVerticalChain(text, emailInput, signInButton, chainStyle = ChainStyle.Packed)
            constrain(chain) {
                top.linkTo(logo.bottom)
                bottom.linkTo(questionBox.top)
            }
        }
        ConstraintLayout(
            constraint,
            modifier = Modifier.padding(
                horizontal = ConstraintParams.DP_48,
                vertical = ConstraintParams.DP_32
            )
        ) {
            Image(
                modifier = Modifier.layoutId(ConstraintParams.REF_LOGO),
                painter = painterResource(R.drawable.baseline_fireplace_24),
                contentDescription = null
            )
            Text(
                textAlign = TextAlign.Center,
                text = "You can enter your email to use Firebase services like FCM, Firestore etc.",
                modifier = Modifier.padding(bottom = ConstraintParams.DP_16)
                    .layoutId(ConstraintParams.REF_TEXT)
            )
            OutlinedTextField(
                value = state.email ?: "",
                onValueChange = onValueChanged,
                label = {
                    Text(text = "Email", style = TextStyle(color = Orange))
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Orange,
                    cursorColor = Orange,
                    focusedLabelColor = Orange,
                    placeholderColor = Orange
                ),
                modifier = Modifier.padding(vertical = ConstraintParams.DP_16)
                    .layoutId(ConstraintParams.REF_EMAIL_INPUT)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { sendAuthRequest.invoke() },
                modifier = Modifier.fillMaxWidth().layoutId(ConstraintParams.REF_SIGN_IN_BUTTON),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text(text = "Sign in")
            }
            Spacer(modifier = Modifier.height(100.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable { }.layoutId(ConstraintParams.REF_QUESTION_BOX)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        Icons.Outlined.QuestionMark,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Orange)
                    )
                    Text(text = "What is FConsole")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun DefaultPreview() {
    FConsoleTheme {
        AuthScreenContent(AuthScreenState(), onValueChanged = {}, sendAuthRequest = {})
    }
}
