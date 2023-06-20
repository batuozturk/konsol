package com.batuhan.management.presentation.createproject

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.batuhan.core.util.Result
import com.batuhan.core.util.UiState
import com.batuhan.management.R
import com.batuhan.management.data.model.AnalyticsAccount
import com.batuhan.management.data.model.CreateProjectRequest
import com.batuhan.management.data.model.Status
import com.batuhan.management.domain.*
import com.batuhan.management.domain.firebase.*
import com.batuhan.management.domain.googleanalytics.GetGoogleAnalyticsAccounts
import com.batuhan.management.domain.googlecloud.CreateGoogleCloudProject
import com.batuhan.management.domain.googlecloud.GetGoogleCloudOperation
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_FOUR
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_ONE
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_SAVE_PROJECT
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_THREE
import com.batuhan.management.presentation.createproject.CreateProjectViewModel.Companion.STEP_TWO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val getAvailableProjects: GetAvailableProjects,
    private val getGoogleAnalyticsAccounts: GetGoogleAnalyticsAccounts,
    private val addGoogleAnalytics: AddGoogleAnalytics,
    private val addFirebase: AddFirebase,
    private val createGoogleCloudProject: CreateGoogleCloudProject,
    private val finalizeLocation: FinalizeLocation,
    private val getAvailableLocations: GetAvailableLocations,
    private val getGoogleCloudOperation: GetGoogleCloudOperation,
    private val getFirebaseOperation: GetFirebaseOperation
) :
    ViewModel() {

    companion object {
        internal const val STEP_ONE = 1
        internal const val STEP_TWO = 2
        internal const val STEP_THREE = 3
        internal const val STEP_FOUR = 4
        internal const val STEP_SAVE_PROJECT = 5
        internal const val STEP_SUCCESS = 6
    }

    val availableProjects = getAvailableProjects.invoke().cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(CreateProjectUiState())
    val uiState = _uiState.asStateFlow()

    private var projectName = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val availableLocations = projectName.asSharedFlow().flatMapLatest {
        if (it.isNotEmpty()) {
            val project = getProjectId(it)
            getAvailableLocations.invoke(GetAvailableLocations.Params(project))
                .cachedIn(viewModelScope)
        } else {
            flowOf(PagingData.empty())
        }
    }

    private val _analyticsAccounts = MutableStateFlow<List<AnalyticsAccount>>(listOf())
    val analyticsAccounts = _analyticsAccounts.asStateFlow()

    fun updateStep(nextStep: Int) {
        removeErrorState()
        val currentStep = uiState.value.currentStep
        when (currentStep to nextStep) {
            STEP_ONE to STEP_TWO -> stepOne(nextStep)
            STEP_TWO to STEP_THREE -> stepTwo(nextStep)
            STEP_THREE to STEP_FOUR -> stepThree(nextStep)
            STEP_FOUR to STEP_SAVE_PROJECT -> stepFour(nextStep)
            else -> {
                _uiState.update {
                    it.copy(currentStep = nextStep)
                }
            }
        }
    }

    fun stepOne(nextStep: Int) {
        if (uiState.value.stepOneState.isCreatingFromScratch == null) {
            setErrorState(
                CreateProjectErrorState.CREATION_TYPE_NOT_SELECTED
            )
        } else {
            _uiState.update {
                it.copy(
                    currentStep = nextStep,
                    stepTwoState = StepTwoState()
                )
            }
        }
    }

    fun stepTwo(nextStep: Int) {
        val isCreatingFromScratch = uiState.value.stepOneState.isCreatingFromScratch
        val projectId = uiState.value.stepTwoState.projectId
        val projectName = uiState.value.stepTwoState.projectName
        if (isCreatingFromScratch == false && uiState.value.stepTwoState.projectId == null) {
            setErrorState(CreateProjectErrorState.GCP_NOT_SELECTED)
            return
        } else if (isCreatingFromScratch == true && projectName == null) {
            setErrorState(CreateProjectErrorState.PROJECT_NAME_EMPTY)
            return
        } else if (isCreatingFromScratch == true && projectId == null) {
            setErrorState(CreateProjectErrorState.ID_EMPTY)
            return
        }
        if (isCreatingFromScratch == false) {
            _uiState.update {
                it.copy(
                    currentStep = nextStep,
                    stepThreeState = StepThreeState()
                )
            }
        } else {
            setLoadingState(true)
            viewModelScope.launch {
                val isSuccessful =
                    executeGoogleCloudOperation(uiState.value, ::createGoogleCloudProject)
                if (isSuccessful) {
                    removeLoadingState()
                    removeErrorState()
                    _uiState.update {
                        it.copy(
                            currentStep = nextStep,
                            stepThreeState = StepThreeState()
                        )
                    }
                }
            }
        }
    }

    fun stepThree(nextStep: Int) {
        val stepThreeState = uiState.value.stepThreeState
        if (stepThreeState.isGoogleAnalyticsEnabled && stepThreeState.googleAnalyticsAccountId == null) {
            setErrorState(CreateProjectErrorState.ANALYTICS_ACCOUNT_NOT_SELECTED)
        } else {
            getAvailableLocations()
            _uiState.update {
                it.copy(
                    currentStep = nextStep,
                    stepFourState = StepFourState()
                )
            }
        }
    }

    fun stepFour(nextStep: Int) {
        if (uiState.value.stepFourState.locationId == null) {
            setErrorState(CreateProjectErrorState.LOCATION_NOT_SELECTED)
        } else {
            _uiState.update {
                it.copy(currentStep = nextStep)
            }
        }
    }

    fun setErrorState(errorState: CreateProjectErrorState) {
        _uiState.update {
            it.copy(errorState = errorState)
        }
    }

    fun removeErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun removeLoadingState() {
        _uiState.update {
            it.copy(isLoading = false)
        }
    }

    fun getProjectId(projectId: String): String {
        return if (uiState.value.stepOneState.isCreatingFromScratch == true) "projects/$projectId" else projectId
    }

    fun retryOperation(errorState: CreateProjectErrorState) {
        removeErrorState()
        viewModelScope.launch {
            when (errorState) {
                CreateProjectErrorState.ADD_FIREBASE -> {
                    saveProject(resumeStep = 0)
                }
                CreateProjectErrorState.ADD_ANALYTICS -> {
                    saveProject(resumeStep = 1)
                }
                CreateProjectErrorState.FINALIZE_LOCATION -> {
                    saveProject(resumeStep = 2)
                }
                CreateProjectErrorState.GCP_PROJECT_QUOTA_FULL,
                CreateProjectErrorState.GOOGLE_CLOUD_ERROR,
                CreateProjectErrorState.ID_DUPLICATE -> {
                    stepTwo(STEP_THREE)
                }
                CreateProjectErrorState.FIREBASE_ERROR -> {
                }
                else -> {}
            }
        }
    }

    // region Step One
    fun saveFirstStep(isCreatingFromScratch: Boolean) {
        removeErrorState()
        _uiState.update {
            val updatedFirstStep =
                it.stepOneState.copy(isCreatingFromScratch = isCreatingFromScratch)
            if (isCreatingFromScratch) {
                it.copy(
                    stepTwoState = StepTwoState(),
                    stepOneState = updatedFirstStep
                )
            } else {
                it.copy(stepOneState = updatedFirstStep)
            }
        }
    }

    // endregion

    // region Step Two
    fun saveSecondStep(projectId: String?, name: String?) {
        removeErrorState()
        _uiState.update {
            val updatedSecondStep =
                it.stepTwoState.copy(projectName = name, projectId = projectId)
            it.copy(stepTwoState = updatedSecondStep)
        }
    }

    fun updateProjectName(projectName: String) {
        _uiState.update {
            val projectId = it.stepTwoState.projectId
            if (projectName.isBlank() || projectName.isEmpty() || projectName.contains(" ")) {
                setErrorState(
                    CreateProjectErrorState.PROJECT_NAME_EMPTY
                )
            } else if (projectId.isNullOrEmpty() || projectId.isBlank() || projectId.contains(" ")) {
                setErrorState(
                    CreateProjectErrorState.ID_EMPTY
                )
            } else removeErrorState()

            val updatedSecondStep =
                it.stepTwoState.copy(projectName = projectName)
            it.copy(stepTwoState = updatedSecondStep)
        }
    }

    fun updateProjectId(projectId: String) {
        _uiState.update {
            val projectName = it.stepTwoState.projectName
            if (projectName.isNullOrEmpty() || projectName.isBlank() || projectName.contains(" ")) {
                setErrorState(
                    CreateProjectErrorState.PROJECT_NAME_EMPTY
                )
            } else if (projectId.isBlank() || projectId.isEmpty() || projectId.contains(" ")) {
                setErrorState(
                    CreateProjectErrorState.ID_EMPTY
                )
            } else removeErrorState()

            val updatedSecondStep =
                it.stepTwoState.copy(projectId = projectId)
            it.copy(stepTwoState = updatedSecondStep)
        }
    }

    suspend fun executeGoogleCloudOperation(
        uiState: CreateProjectUiState,
        operation: suspend (uiState: CreateProjectUiState) -> String?
    ): Boolean {
        val operationIdResult = operation.invoke(uiState)
        operationIdResult ?: run {
            removeLoadingState()
            setErrorState(CreateProjectErrorState.GOOGLE_CLOUD_ERROR)
            return false
        }
        val isSuccess = getGoogleCloudOperation(operationId = operationIdResult)
        if (!isSuccess) {
            removeLoadingState()
            setErrorState(CreateProjectErrorState.GOOGLE_CLOUD_ERROR)
            return false
        }
        return true
    }

    suspend fun createGoogleCloudProject(uiState: CreateProjectUiState): String? {
        val secondStep = uiState.stepTwoState
        val createProjectRequest =
            CreateProjectRequest(
                secondStep.projectId ?: return null, // setErrorState
                secondStep.projectName
            )
        if (uiState.stepOneState.isCreatingFromScratch == true) {
            val result = createGoogleCloudProject.invoke(
                CreateGoogleCloudProject.Params(createProjectRequest)
            )
            return when (result) {
                is Result.Success -> {
                    result.data.name
                }
                is Result.Error -> {
                    setErrorState(CreateProjectErrorState.GOOGLE_CLOUD_ERROR)
                    null
                }
            }
        }
        return null
    }

    suspend fun getGoogleCloudOperation(
        operationId: String
    ): Boolean {
        var isDone = false
        var error: Status? = null
        while (error == null && !isDone) {
            val result =
                getGoogleCloudOperation.invoke(GetGoogleCloudOperation.Params(operationId))
            when (result) {
                is Result.Success -> {
                    isDone = result.data.done ?: false
                    error = result.data.error
                }
                is Result.Error -> {
                    error = Status(message = "Internal Error", code = "1000")
                }
            }
            delay(3000)
        }
        return if (isDone && error == null) {
            true
        } else if (error?.message != "Internal Error") {
            false
        } else false
    }

    // end-region

    // region Step Three
    fun saveThirdStep(analyticsAccountId: String) {
        removeErrorState()
        _uiState.update {
            val updatedThirdStep =
                it.stepThreeState.copy(googleAnalyticsAccountId = analyticsAccountId)
            it.copy(stepThreeState = updatedThirdStep)
        }
    }

    fun setGoogleAnalyticsEnabled(isGoogleAnalyticsEnabled: Boolean) {
        removeErrorState()
        _uiState.update {
            val updatedThirdStep = if (isGoogleAnalyticsEnabled) {
                it.stepThreeState.copy(isGoogleAnalyticsEnabled = true)
            } else {
                it.stepThreeState.copy(
                    isGoogleAnalyticsEnabled = false,
                    googleAnalyticsAccountId = null
                )
            }
            it.copy(stepThreeState = updatedThirdStep)
        }
    }

    fun getAnalyticsAccounts() {
        viewModelScope.launch {
            val result = getGoogleAnalyticsAccounts.invoke()
            when (result) {
                is Result.Success -> {
                    result.data.items?.let {
                        _analyticsAccounts.value = it
                    }
                }
                is Result.Error -> {
                    setErrorState(
                        CreateProjectErrorState.NO_CONNECTION
                    )
                }
            }
        }
    }

    // end-region

    // region Step Four
    fun setFourthStep(locationId: String) {
        removeErrorState()
        _uiState.update {
            val updatedFourthStep = it.stepFourState.copy(locationId = locationId)
            it.copy(stepFourState = updatedFourthStep)
        }
    }

    fun getAvailableLocations() {
        viewModelScope.launch {
            projectName.emit(uiState.value.stepTwoState.projectId ?: "")
        }
    }

    // end-region

    // region Step Save Project
    fun saveProject(resumeStep: Int = 0) {
        var step = resumeStep
        viewModelScope.launch {
            setLoadingState(true)
            if (step == 0) {
                val addFirebaseOperationResult = executeOperation(uiState.value, ::addFirebase)
                if (!addFirebaseOperationResult) return@launch
                step++
            }
            if (step == 1) {
                if (uiState.value.stepThreeState.isGoogleAnalyticsEnabled) {
                    val addGoogleAnalyticsOperationResult =
                        executeOperation(uiState.value, ::addGoogleAnalytics)
                    if (!addGoogleAnalyticsOperationResult) return@launch
                }
                step++
            }

            if (step == 2) {
                val operationResult = executeOperation(uiState.value, ::finalizeLocation)
                if (!operationResult) return@launch
                step++
            }

            removeLoadingState()

            _uiState.update {
                it.copy(currentStep = STEP_SUCCESS)
            }
        }
    }

    suspend fun executeOperation(
        uiState: CreateProjectUiState,
        operation: suspend (uiState: CreateProjectUiState) -> String?
    ): Boolean {
        val operationIdResult = operation.invoke(uiState)
        operationIdResult ?: return false
        val isSuccess = getFirebaseOperation(operationId = operationIdResult)
        if (!isSuccess) {
            return false
        }
        return true
    }

    suspend fun addFirebase(state: CreateProjectUiState): String? {
        var projectId = state.stepTwoState.projectId ?: return null
        projectId = getProjectId(projectId)
        val result = addFirebase.invoke(
            AddFirebase.Params(
                projectId
            )
        )
        return when (result) {
            is Result.Success -> {
                removeErrorState()
                result.data.name
            }
            is Result.Error -> {
                setErrorState(CreateProjectErrorState.ADD_FIREBASE)
                null
            }
        }
    }

    suspend fun addGoogleAnalytics(state: CreateProjectUiState): String? {
        val googleAnalyticsAccountId = state.stepThreeState.googleAnalyticsAccountId ?: return null
        var projectId = state.stepTwoState.projectId ?: return null
        projectId = getProjectId(projectId)

        val result = addGoogleAnalytics.invoke(
            AddGoogleAnalytics.Params(
                googleAnalyticsAccountId,
                projectId
            )
        )
        return when (result) {
            is Result.Success -> {
                removeErrorState()
                result.data.name
            }

            is Result.Error -> {
                setErrorState(CreateProjectErrorState.ADD_ANALYTICS)
                null
            }
        }
    }

    suspend fun finalizeLocation(state: CreateProjectUiState): String? {
        var projectId = state.stepTwoState.projectId ?: return null
        projectId = getProjectId(projectId)
        val result = finalizeLocation.invoke(
            FinalizeLocation.Params(
                projectId,
                state.stepFourState.locationId ?: return null
            )
        )
        return when (result) {
            is Result.Success -> {
                result.data.name
            }
            is Result.Error -> {
                setErrorState(CreateProjectErrorState.FINALIZE_LOCATION)
                null
            }
        }
    }

    suspend fun getFirebaseOperation(operationId: String): Boolean {
        var isDone = false
        var error: Status? = null
        while (error == null && !isDone) {
            val result =
                getFirebaseOperation.invoke(GetFirebaseOperation.Params(operationId))
            when (result) {
                is Result.Success -> {
                    isDone = result.data.done ?: false
                    error = result.data.error
                }
                is Result.Error -> {
                    setErrorState(CreateProjectErrorState.FIREBASE_ERROR)
                }
            }
            delay(3000)
        }
        return if (isDone && error == null) {
            true
        } else {
            setErrorState(CreateProjectErrorState.FIREBASE_ERROR)
            false
        }
    }
}

data class CreateProjectUiState(
    override val isLoading: Boolean = false,
    override val isError: Boolean = false,
    val currentStep: Int = STEP_ONE,
    val stepOneState: StepOneState = StepOneState(),
    val stepTwoState: StepTwoState = StepTwoState(),
    val stepThreeState: StepThreeState = StepThreeState(),
    val stepFourState: StepFourState = StepFourState(),
    val errorState: CreateProjectErrorState? = null
) : UiState()

data class StepOneState(
    val isCreatingFromScratch: Boolean? = null
)

data class StepTwoState(
    val projectId: String? = null,
    val projectName: String? = null
)

data class StepThreeState(
    val isGoogleAnalyticsEnabled: Boolean = false,
    val googleAnalyticsAccountId: String? = null
)

data class StepFourState(
    val locationId: String? = null
)

enum class CreateProjectErrorState(
    @StringRes val messageResId: Int = R.string.error_occurred,
    val currentStep: Int? = null

) {
    ADD_FIREBASE(currentStep = STEP_SAVE_PROJECT),
    ADD_ANALYTICS(currentStep = STEP_SAVE_PROJECT),
    FINALIZE_LOCATION(currentStep = STEP_SAVE_PROJECT),
    GCP_PROJECT_QUOTA_FULL(currentStep = STEP_TWO),
    ID_DUPLICATE(currentStep = STEP_TWO),
    ID_EMPTY(R.string.project_id_empty, STEP_TWO),
    PROJECT_NAME_EMPTY(R.string.project_name_empty, STEP_TWO),
    NO_CONNECTION(R.string.no_connection),
    GOOGLE_CLOUD_ERROR(currentStep = STEP_TWO),
    FIREBASE_ERROR(currentStep = STEP_SAVE_PROJECT),
    CREATION_TYPE_NOT_SELECTED(R.string.creation_type_not_selected, STEP_ONE),
    GCP_NOT_SELECTED(R.string.project_not_selected, STEP_TWO),
    ANALYTICS_ACCOUNT_NOT_SELECTED(R.string.analytics_account_not_selected, STEP_THREE),
    LOCATION_NOT_SELECTED(R.string.location_not_selected, STEP_FOUR)
}
