package com.batuhan.firestore.presentation.createdatabase

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.core.data.model.Status
import com.batuhan.core.data.model.firestore.Database
import com.batuhan.core.data.model.firestore.DatabaseType
import com.batuhan.core.util.Result
import com.batuhan.firestore.R
import com.batuhan.firestore.domain.database.CreateDatabase
import com.batuhan.firestore.domain.database.GetFirestoreDatabaseOperation
import com.batuhan.firestore.domain.database.ListFirestoreLocations
import com.batuhan.firestore.presentation.createdatabase.CreateDatabaseViewModel.Companion.DATABASE_NAME_PREFIX_AFTER
import com.batuhan.firestore.presentation.createdatabase.CreateDatabaseViewModel.Companion.DATABASE_NAME_PREFIX_BEFORE
import com.batuhan.firestore.presentation.createdatabase.CreateDatabaseViewModel.Companion.STEP_ONE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDatabaseViewModel @Inject constructor(
    private val createDatabase: CreateDatabase,
    private val listFirestoreLocations: ListFirestoreLocations,
    private val getFirestoreDatabaseOperation: GetFirestoreDatabaseOperation,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        private const val DATABASE_ID = "(default)"
        internal const val DATABASE_NAME_PREFIX_BEFORE = "projects/"
        internal const val DATABASE_NAME_PREFIX_AFTER = "/databases/"
        internal const val STEP_ONE = 0
        internal const val STEP_TWO = 1
        internal const val STEP_PREVIEW = 2
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    val locations =
        listFirestoreLocations.invoke(ListFirestoreLocations.Params(projectId = projectId!!))
            .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(CreateDatabaseUiState())
    val uiState = _uiState.asStateFlow()

    private val _createDatabaseEvent = Channel<CreateDatabaseEvent> { Channel.BUFFERED }
    val createDatabaseEvent = _createDatabaseEvent.receiveAsFlow()

    fun updateStep(nextStep: Int): Boolean {
        val currentStep = uiState.value.currentStep
        when (currentStep to nextStep) {
            STEP_ONE to STEP_TWO -> {
                val databaseName = uiState.value.stepOneState.databaseName
                if (databaseName.isNullOrEmpty() || databaseName.isBlank()) {
                    setErrorState(CreateDatabaseErrorState.DATABASE_NAME_EMPTY)
                    return false
                }
            }
            STEP_TWO to STEP_PREVIEW -> {
                val locationId = uiState.value.stepTwoState.locationId
                if (locationId.isNullOrEmpty() || locationId.isBlank()) {
                    setErrorState(CreateDatabaseErrorState.LOCATION_ID_NOT_SELECTED)
                    return false
                }
            }
        }
        _uiState.update {
            it.copy(currentStep = nextStep)
        }
        return true
    }

    suspend fun addDatabase(): String? {
        setLoadingState(true)
        val result = createDatabase.invoke(
            CreateDatabase.Params(
                projectId!!,
                DATABASE_ID,
                uiState.value.getDatabase(projectId)
            )
        )
        return when (result) {
            is Result.Success -> {
                setLoadingState(false)
                result.data.name
            }
            is Result.Error -> {
                setLoadingState(false)
                setErrorState(CreateDatabaseErrorState.CREATE_DATABASE)
                null
            }
        }
    }

    fun createDatabaseOperation() {
        setLoadingState(true)
        viewModelScope.launch {
            executeOperation(::addDatabase)
        }
    }

    suspend fun executeOperation(operation: suspend () -> String?): Boolean {
        val operationName = operation.invoke()
        operationName ?: return false
        return getFirestoreDatabaseOperation(operationName)
    }

    suspend fun getFirestoreDatabaseOperation(operationName: String): Boolean {
        var isDone = false
        var error: Status? = null
        while (!isDone && error == null) {
            val result = getFirestoreDatabaseOperation.invoke(
                GetFirestoreDatabaseOperation.Params(operationName = operationName)
            )
            when (result) {
                is Result.Success -> {
                    isDone = result.data.done ?: false
                    error = result.data.error
                }
                is Result.Error -> {
                    error = Status("1000", "No connection")
                    setErrorState(CreateDatabaseErrorState.OPERATION)
                }
            }
            delay(3000L)
        }
        return error == null
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _createDatabaseEvent.send(CreateDatabaseEvent.Back)
        }
    }

    fun setErrorState(createDatabaseErrorState: CreateDatabaseErrorState) {
        _uiState.update {
            it.copy(errorState = createDatabaseErrorState)
        }
    }

    fun clearErrorState() {
        _uiState.update {
            it.copy(errorState = null)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun retryOperation(errorState: CreateDatabaseErrorState, operation: (() -> Unit)?) {
        when (errorState) {
            CreateDatabaseErrorState.CREATE_DATABASE -> createDatabaseOperation()
            CreateDatabaseErrorState.FIRESTORE_LOCATIONS -> operation?.invoke()
            else -> {
            }
        }
    }

    fun setSnackbarState(isSnackbarOpened: Boolean) {
        _uiState.update {
            it.copy(isSnackbarOpened = isSnackbarOpened)
        }
        if (!isSnackbarOpened) {
            clearErrorState()
        }
    }

    fun selectLocation(locationId: String) {
        _uiState.update {
            val updatedStepTwoState = it.stepTwoState.copy(locationId = locationId)
            it.copy(stepTwoState = updatedStepTwoState)
        }
    }

    fun updateDatabaseName(databaseName: String) {
        _uiState.update {
            val updatedStepOneState = it.stepOneState.copy(databaseName = databaseName)
            it.copy(stepOneState = updatedStepOneState)
        }
    }
}

data class CreateDatabaseUiState(
    val errorState: CreateDatabaseErrorState? = null,
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val currentStep: Int = STEP_ONE,
    val stepOneState: StepOneState = StepOneState(),
    val stepTwoState: StepTwoState = StepTwoState()
) {
    fun getDatabase(projectId: String) = Database(
        name = getDatabaseName(projectId),
        locationId = stepTwoState.locationId,
        type = DatabaseType.FIRESTORE_NATIVE
    )

    fun getDatabaseName(projectId: String) =
        DATABASE_NAME_PREFIX_BEFORE + projectId + DATABASE_NAME_PREFIX_AFTER + stepOneState.databaseName
}

data class StepOneState(
    val databaseName: String? = null
)

data class StepTwoState(
    val locationId: String? = null
)

sealed class CreateDatabaseEvent {
    object Back : CreateDatabaseEvent()
    data class SelectLocation(val projectId: String, val locationId: String?) :
        CreateDatabaseEvent()
}

enum class CreateDatabaseErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int?
) {
    CREATE_DATABASE(R.string.error_occured, R.string.retry),
    OPERATION(R.string.error_occured, null),
    DATABASE_NAME_EMPTY(R.string.database_name_empty, null),
    LOCATION_ID_NOT_SELECTED(R.string.location_id_not_selected, null),
    FIRESTORE_LOCATIONS(R.string.error_occured, R.string.retry),
}
