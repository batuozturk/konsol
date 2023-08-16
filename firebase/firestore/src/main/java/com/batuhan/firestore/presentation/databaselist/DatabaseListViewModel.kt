package com.batuhan.firestore.presentation.databaselist

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.data.model.Status
import com.batuhan.core.util.Result
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.ApiServiceState
import com.batuhan.firestore.data.model.Database
import com.batuhan.firestore.data.model.DatabaseType
import com.batuhan.firestore.domain.database.GetFirestoreDatabaseOperation
import com.batuhan.firestore.domain.database.ListDatabases
import com.batuhan.firestore.domain.database.PatchDatabase
import com.batuhan.firestore.domain.serviceusage.EnableService
import com.batuhan.firestore.domain.serviceusage.GetServiceEnableState
import com.batuhan.firestore.domain.serviceusage.GetServiceUsageOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DatabaseListViewModel @Inject constructor(
    private val listDatabases: ListDatabases,
    private val patchDatabase: PatchDatabase,
    private val getFirestoreDatabaseOperation: GetFirestoreDatabaseOperation,
    private val enableService: EnableService,
    private val getServiceUsageOperation: GetServiceUsageOperation,
    private val getServiceEnableState: GetServiceEnableState,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        private const val FIRESTORE_SERVICE = "services/firestore.googleapis.com"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(DatabaseListUiState())
    val uiState = _uiState.asStateFlow()

    private val _databaseList = MutableStateFlow(listOf<Database>())
    val databaseList = _databaseList.asStateFlow()

    private val _databaseListEvent = Channel<DatabaseListEvent> { Channel.BUFFERED }
    val databaseListEvent = _databaseListEvent.receiveAsFlow()

    init {
        listDatabases()
    }

    private fun listDatabases(delayEnabled: Boolean = false) {
        setLoadingState(true)
        viewModelScope.launch {
            Log.d("projectId", projectId!!)
            val result = listDatabases.invoke(ListDatabases.Params(projectId))
            when (result) {
                is Result.Success -> {
                    result.data.databases?.let {
                        setLoadingState(false)
                        _databaseList.value = it
                    }
                }
                is Result.Error -> {
                    val throwable = result.throwable as? HttpException
                    if (throwable?.response()?.errorBody()?.string()
                        ?.contains("enable") == true
                    ) {
                        getServiceEnableState()
                    } else {
                        setErrorState(DatabaseListErrorState.LIST_DATABASES)
                        setLoadingState(false)
                    }
                }
            }
        }
    }

    fun getServiceEnableState() {
        viewModelScope.launch {
            val result =
                getServiceEnableState.invoke(
                    GetServiceEnableState.Params(
                        "projects/$projectId/$FIRESTORE_SERVICE"
                    )
                )
            when (result) {
                is Result.Success -> {
                    if (result.data.state != ApiServiceState.ENABLED) {
                        setErrorState(DatabaseListErrorState.ENABLE_SERVICE)
                    } else {
                        listDatabases()
                    }
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(DatabaseListErrorState.ENABLE_SERVICE)
                }
            }
        }
    }

    fun patchDatabaseOperation() {
        viewModelScope.launch {
            setLoadingState(true)
            val isSuccessful = executeFirestoreDatabaseOperation(::patchDatabase)
            if (isSuccessful) {
                clearSelectedDatabase()
                setSnackbarState(false)
                listDatabases()
            } else {
                setLoadingState(false)
            }
        }
    }

    suspend fun patchDatabase(): Pair<String?, Boolean?> {
        val database = uiState.value.selectedDatabase ?: return Pair(null, false)
        return when (val result = patchDatabase.invoke(PatchDatabase.Params("type", database))) {
            is Result.Success -> {
                Pair(result.data.name, result.data.done)
            }
            is Result.Error -> {
                setLoadingState(false)
                setErrorState(DatabaseListErrorState.PATCH_DATABASE)
                Pair(null, false)
            }
        }
    }

    fun enableServiceOperation() {
        viewModelScope.launch {
            setLoadingState(true)
            val isSuccessful = executeServiceUsageOperation(::enableService)
            if (isSuccessful) {
                setSnackbarState(false)
                listDatabases(true)
            } else {
                setLoadingState(false)
            }
        }
    }

    suspend fun enableService(): Pair<String?, Boolean?> {
        val result =
            enableService.invoke(
                EnableService.Params("projects/$projectId/$FIRESTORE_SERVICE")
            )
        return when (result) {
            is Result.Success -> {
                Pair(result.data.name, result.data.done)
            }
            is Result.Error -> {
                setLoadingState(false)
                setErrorState(DatabaseListErrorState.ENABLE_SERVICE)
                Pair(null, false)
            }
        }
    }

    suspend fun executeServiceUsageOperation(operation: suspend () -> Pair<String?, Boolean?>): Boolean {
        val pair = operation.invoke()
        return if (pair.second == true) true
        else {
            pair.first ?: return false
            getServiceUsageOperation(pair.first!!)
        }
    }

    suspend fun executeFirestoreDatabaseOperation(operation: suspend () -> Pair<String?, Boolean?>): Boolean {
        val pair = operation.invoke()
        return if (pair.second == true) true
        else {
            pair.first ?: return false
            getFirestoreDatabaseOperation(pair.first!!)
        }
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
                    setErrorState(DatabaseListErrorState.OPERATION)
                }
            }
            delay(3000L)
        }
        return error == null
    }

    suspend fun getServiceUsageOperation(operationName: String): Boolean {
        var isDone = false
        var error: Status? = null
        while (!isDone && error == null) {
            val result = getServiceUsageOperation.invoke(
                GetServiceUsageOperation.Params(operationName = operationName)
            )
            when (result) {
                is Result.Success -> {
                    isDone = result.data.done ?: false
                    error = result.data.error
                }
                is Result.Error -> {
                    error = Status("1000", "No connection")
                    setErrorState(DatabaseListErrorState.OPERATION)
                }
            }
            delay(3000L)
        }
        return error == null
    }

    fun setSelectedDatabase(database: Database) {
        _uiState.update {
            it.copy(selectedDatabase = database.copy(type = DatabaseType.FIRESTORE_NATIVE))
        }
    }

    fun clearSelectedDatabase() {
        _uiState.update {
            it.copy(selectedDatabase = null)
        }
    }

    fun setErrorState(databaseListErrorState: DatabaseListErrorState) {
        _uiState.update {
            it.copy(errorState = databaseListErrorState)
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

    fun createDatabase() {
        viewModelScope.launch {
            _databaseListEvent.send(DatabaseListEvent.CreateDatabase(projectId!!))
        }
    }

    fun navigateToDatabaseScreen(name: String) {
        viewModelScope.launch {
            _databaseListEvent.send(DatabaseListEvent.NavigateToDatabaseScreen(name))
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _databaseListEvent.send(DatabaseListEvent.Back)
        }
    }

    fun retryOperation(errorState: DatabaseListErrorState) {
        when (errorState) {
            DatabaseListErrorState.LIST_DATABASES -> listDatabases()
            DatabaseListErrorState.DATASTORE_CONFIG -> {
                patchDatabaseOperation()
            }
            DatabaseListErrorState.PATCH_DATABASE -> {
                patchDatabaseOperation()
            }
            DatabaseListErrorState.ENABLE_SERVICE -> {
                enableServiceOperation()
            }
            else -> {
                // no-op
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
}

data class DatabaseListUiState(
    val errorState: DatabaseListErrorState? = null,
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val selectedDatabase: Database? = null
)

sealed class DatabaseListEvent {
    object Back : DatabaseListEvent()
    data class NavigateToDatabaseScreen(val name: String) : DatabaseListEvent()
    data class CreateDatabase(val projectId: String) : DatabaseListEvent()
}

enum class DatabaseListErrorState(
    @StringRes val titleResId: Int,
    @StringRes val actionResId: Int?
) {
    LIST_DATABASES(R.string.error_occured, R.string.retry),
    DATASTORE_CONFIG(R.string.datastore_config, R.string.take_action),
    PATCH_DATABASE(R.string.error_occured, R.string.retry),
    OPERATION(R.string.error_occured, null),
    ENABLE_SERVICE(R.string.enable_firestore_service, R.string.enable)
}
