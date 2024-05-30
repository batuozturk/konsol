package com.batuhan.cloudstorage.presentation.bucket.create

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.batuhan.cloudstorage.R
import com.batuhan.cloudstorage.domain.CreateDefaultBucket
import com.batuhan.core.data.model.BucketObject
import com.batuhan.core.data.model.DefaultBucket
import com.batuhan.core.domain.firestore.ListDatabases
import com.batuhan.core.domain.management.GetAvailableLocations
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
class CreateBucketViewModel @Inject constructor(
    private val createDefaultCreateBucket: CreateDefaultBucket,
    private val getAvailableLocations: GetAvailableLocations,
    private val listDatabases: ListDatabases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        internal const val FIRESTORE_LOCATION_NAM5 = "nam5"
        internal const val FIRESTORE_LOCATION_EUR3 = "eur3"
        internal const val FIRESTORE_LOCATION_EUROPE_WEST = "europe-west"
        internal const val FIRESTORE_LOCATION_US_CENTRAL = "us-central"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(CreateBucketUiState())
    val uiState = _uiState.asStateFlow()

    private val _createBucketEvent = Channel<CreateBucketEvent> { Channel.BUFFERED }
    val createBucketEvent = _createBucketEvent.receiveAsFlow()

    val locations = getAvailableLocations.invoke(GetAvailableLocations.Params("projects/" + projectId!!))
        .cachedIn(viewModelScope)

    init {
        listDatabases()
    }

    fun listDatabases() {
        viewModelScope.launch {
            val result = listDatabases.invoke(ListDatabases.Params(projectId!!))

            when (result) {
                is Result.Error -> {
                    // no-op
                }
                is Result.Success -> {
                    val locationId = result.data.databases?.get(0)?.locationId?.getLocationId()
                    _uiState.update {
                        it.copy(
                            selectedLocationId = locationId,
                            isFirestoreLocationSelected = locationId != null
                        )
                    }
                }
            }
        }
    }

    fun createDefaultCreateBucket() {
        val bucketName = uiState.value.bucketName
        val locationId = uiState.value.selectedLocationId
        if (bucketName.isNullOrBlank() || bucketName.isEmpty()) {
            setErrorState(CreateBucketErrorState.BUCKET_NAME_EMPTY)
            return
        } else if (locationId.isNullOrBlank() || locationId.isEmpty()) {
            setErrorState(CreateBucketErrorState.LOCATION_NOT_SELECTED)
            return
        } else {
            clearErrorState()
        }
        setLoadingState(true)
        viewModelScope.launch {
            when (
                createDefaultCreateBucket.invoke(
                    CreateDefaultBucket.Params(
                        projectId!!,
                        DefaultBucket(
                            name = bucketName,
                            location = locationId
                        )
                    )
                )
            ) {
                is Result.Success -> {
                    setLoadingState(false)
                    onBackPressed()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateBucketErrorState.CREATE_DEFAULT_BUCKET)
                }
            }
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _createBucketEvent.send(CreateBucketEvent.Back)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun setErrorState(errorState: CreateBucketErrorState) {
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

    fun retryOperation(errorState: CreateBucketErrorState) {
        when (errorState) {
            CreateBucketErrorState.CREATE_DEFAULT_BUCKET -> createDefaultCreateBucket()
            else -> {}
        }
    }

    fun updateBucketName(bucketName: String) {
        _uiState.update {
            it.copy(bucketName = bucketName)
        }
    }

    fun updateLocation(locationId: String) {
        _uiState.update {
            it.copy(selectedLocationId = locationId)
        }
    }

    fun String.getLocationId(): String {
        return when (this) {
            FIRESTORE_LOCATION_NAM5 -> FIRESTORE_LOCATION_US_CENTRAL
            FIRESTORE_LOCATION_EUR3 -> FIRESTORE_LOCATION_EUROPE_WEST
            else -> this
        }
    }
}

data class CreateBucketUiState(
    val isLoading: Boolean = false,
    val errorState: CreateBucketErrorState? = null,
    val isSnackbarOpened: Boolean = false,
    val bucketName: String? = null,
    val selectedLocationId: String? = null,
    val isFirestoreLocationSelected: Boolean = false
)

enum class CreateBucketErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    CREATE_DEFAULT_BUCKET,
    BUCKET_NAME_EMPTY(R.string.bucket_name_is_empty, null),
    LOCATION_NOT_SELECTED(R.string.location_not_selected, null),
}

sealed class CreateBucketEvent {
    object Back : CreateBucketEvent()
}
