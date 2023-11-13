package com.batuhan.cloudmessaging.presentation.campaign.create

import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.core.util.PatternsCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.cloudmessaging.R
import com.batuhan.cloudmessaging.data.model.Message
import com.batuhan.cloudmessaging.data.model.MessageRequest
import com.batuhan.cloudmessaging.data.model.Notification
import com.batuhan.cloudmessaging.domain.SendMessage
import com.batuhan.cloudmessaging.presentation.campaign.create.CreateNotificationViewModel.Companion.STEP_ONE
import com.batuhan.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNotificationViewModel @Inject constructor(
    private val sendMessage: SendMessage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        internal const val STEP_ONE = 0
        internal const val STEP_TWO = 1
        internal const val STEP_THREE = 2
        internal const val STEP_FOUR = 3
        internal const val STEP_SEND = 4
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(CreateNotificationUiState())
    val uiState = _uiState.asStateFlow()

    private val _createCampaignEvent = Channel<CreateNotificationEvent> { Channel.BUFFERED }
    val createCampaignEvent = _createCampaignEvent.receiveAsFlow()

    fun sendMessage() {
        setLoadingState(true)
        viewModelScope.launch {
            val messageRequest = MessageRequest(
                message = uiState.value.getMessage()
            )
            when (sendMessage.invoke(SendMessage.Params(projectId!!, messageRequest))) {
                is Result.Success -> {
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateNotificationErrorState.SEND_MESSAGE)
                }
            }
        }
    }

    fun updateStep(updatedStep: Int): Boolean {
        val currentStep = uiState.value.currentStep
        when (currentStep to updatedStep) {
            STEP_ONE to STEP_TWO -> {
                if (uiState.value.stepOneState.campaignType == null) {
                    setErrorState(CreateNotificationErrorState.TYPE_NOT_SELECTED)
                    return false
                } else {
                    clearErrorState()
                }
            }
            STEP_TWO to STEP_THREE -> {
                val campaignType = uiState.value.stepOneState.campaignType
                val deviceToken = (uiState.value.stepTwoState as? StepTwoState.Token)?.deviceToken
                val topicName = (uiState.value.stepTwoState as? StepTwoState.Topic)?.topicName
                val isTokenEmpty =
                    (deviceToken.isNullOrEmpty() || deviceToken.isBlank()) && campaignType == CampaignType.TOKEN
                val isTopicEmpty =
                    (topicName.isNullOrEmpty() || topicName.isBlank()) && uiState.value.stepOneState.campaignType == CampaignType.TOPIC
                if (isTokenEmpty) {
                    setErrorState(CreateNotificationErrorState.TOKEN_IS_EMPTY)
                    return false
                }
                if (isTopicEmpty) {
                    setErrorState(CreateNotificationErrorState.TOPIC_IS_EMPTY)
                    return false
                }
                clearErrorState()
            }
            STEP_THREE to STEP_FOUR -> {
                val notificationTitle = uiState.value.stepThreeState.title
                if (notificationTitle.isNullOrEmpty() || notificationTitle.isBlank()) {
                    setErrorState(CreateNotificationErrorState.TITLE_IS_EMPTY)
                    return false
                }
                val notificationBody = uiState.value.stepThreeState.body
                if (notificationBody.isNullOrEmpty() || notificationBody.isBlank()) {
                    setErrorState(CreateNotificationErrorState.BODY_IS_EMPTY)
                    return false
                }
                val notificationImageUrl = uiState.value.stepThreeState.imageUrl.takeIf { it?.isNotEmpty() == true }
                if (notificationImageUrl?.isValidHttpsUrl() == false) {
                    setErrorState(CreateNotificationErrorState.IMAGE_URL_VALIDATION)
                    return false
                }
                clearErrorState()
            }
        }
        _uiState.update {
            it.copy(currentStep = updatedStep)
        }
        return true
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _createCampaignEvent.send(CreateNotificationEvent.Back)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun setErrorState(errorState: CreateNotificationErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
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
        if (!isSnackbarOpened) clearErrorState()
    }

    fun updateStepOne(stepOneState: StepOneState) {
        if (uiState.value.errorState != null) {
            clearErrorState()
            setSnackbarState(false)
        }
        _uiState.update {
            val stepTwoState =
                if (stepOneState.campaignType?.name == CampaignType.TOPIC.name) StepTwoState.Topic()
                else StepTwoState.Token()
            it.copy(stepOneState = stepOneState, stepTwoState = stepTwoState)
        }
    }

    fun retryOperation(errorState: CreateNotificationErrorState) {
        when (errorState) {
            CreateNotificationErrorState.SEND_MESSAGE -> sendMessage()
            else -> {}
        }
    }

    fun updateStepTwo(stepTwoState: StepTwoState) {
        if (uiState.value.errorState != null) {
            clearErrorState()
            setSnackbarState(false)
        }
        _uiState.update {
            it.copy(stepTwoState = stepTwoState)
        }
    }

    fun updateStepThree(stepThreeState: StepThreeState) {
        if (uiState.value.errorState != null) {
            clearErrorState()
            setSnackbarState(false)
        }
        _uiState.update {
            it.copy(stepThreeState = stepThreeState)
        }
    }

    fun String.isValidHttpsUrl(): Boolean =
        matches(PatternsCompat.WEB_URL.toRegex()) && indexOf("https") != -1
}

data class CreateNotificationUiState(
    val errorState: CreateNotificationErrorState? = null,
    val currentStep: Int = STEP_ONE,
    val stepOneState: StepOneState = StepOneState(),
    val stepTwoState: StepTwoState = StepTwoState.Unspecified,
    val stepThreeState: StepThreeState = StepThreeState(),
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false
) {
    fun getMessage(): Message {
        return Message(
            notification = Notification(
                title = stepThreeState.title,
                body = stepThreeState.body,
                imageUrl = stepThreeState.imageUrl.takeIf { it?.isNotEmpty() == true }
            ),
            topic = (stepTwoState as? StepTwoState.Topic)?.topicName,
            token = (stepTwoState as? StepTwoState.Token)?.deviceToken
        )
    }
}

data class StepOneState(
    val campaignType: CampaignType? = null
)

sealed class StepTwoState {
    data class Topic(val topicName: String? = null) : StepTwoState()
    data class Token(val deviceToken: String? = null) : StepTwoState()
    object Unspecified : StepTwoState()
}

data class StepThreeState(
    val title: String? = null,
    val body: String? = null,
    val imageUrl: String? = null
)

enum class CampaignType {
    TOPIC, TOKEN
}

sealed class CreateNotificationEvent {
    object Back : CreateNotificationEvent()
}

@Keep
enum class CreateNotificationErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    SEND_MESSAGE,
    TYPE_NOT_SELECTED(R.string.type_not_selected, null),
    TITLE_IS_EMPTY(R.string.title_is_empty, null),
    BODY_IS_EMPTY(R.string.body_is_empty, null),
    APPLICATION_NOT_SELECTED(R.string.application_not_selected, null),
    IMAGE_URL_VALIDATION(R.string.image_url_validation, null),
    TOKEN_IS_EMPTY(R.string.token_is_empty, null),
    TOPIC_IS_EMPTY(R.string.topic_is_empty, null),
}
