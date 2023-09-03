package com.batuhan.testlab.presentation.creatematrix

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuhan.core.domain.GetDefaultBucket
import com.batuhan.core.domain.UploadFile
import com.batuhan.core.util.Result
import com.batuhan.testlab.R
import com.batuhan.testlab.data.model.devicecatalog.AndroidModel
import com.batuhan.testlab.data.model.devicecatalog.Locale
import com.batuhan.testlab.data.model.devicecatalog.Orientation
import com.batuhan.testlab.data.model.execution.*
import com.batuhan.testlab.data.model.matrix.*
import com.batuhan.testlab.domain.testing.CreateMatrix
import com.batuhan.testlab.domain.testing.GetTestEnvironmentCatalog
import com.batuhan.testlab.presentation.creatematrix.CreateMatrixViewModel.Companion.STEP_ONE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateMatrixViewModel @Inject constructor(
    private val createMatrix: CreateMatrix,
    private val getTestEnvironmentCatalog: GetTestEnvironmentCatalog,
    private val uploadFile: UploadFile,
    private val getDefaultBucket: GetDefaultBucket,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PROJECT_ID = "projectId"
        internal const val STEP_ONE = 0
        internal const val STEP_TWO = 1
        internal const val STEP_THREE = 2
        internal const val STEP_FOUR = 4
        internal const val STEP_CREATE_MATRIX = 3
        internal const val GCS_PATH_PREFIX = "gs://"
        internal const val DEFAULT_MIME_TYPE = "application/octet-stream"
    }

    private val projectId = savedStateHandle.get<String>(KEY_PROJECT_ID)

    private val _uiState = MutableStateFlow(CreateMatrixUiState())
    val uiState = _uiState.asStateFlow()

    private val _createMatrixEvent = Channel<CreateMatrixEvent> { Channel.BUFFERED }
    val createMatrixEvent = _createMatrixEvent.receiveAsFlow()

    private var bucketName: String? = null

    init {
        getDefaultBucket()
        getTestEnvironmentCatalog()
    }

    fun getDefaultBucket() {
        viewModelScope.launch {
            when (val result = getDefaultBucket.invoke(GetDefaultBucket.Params(projectId!!))) {
                is Result.Success -> {
                    result.data.bucket?.name?.let {
                        bucketName = it.substring(it.lastIndexOf("/") + 1)
                    }
                }
                is Result.Error -> {
                    setErrorState(CreateMatrixErrorState.DEFAULT_BUCKET)
                }
            }
        }
    }

    fun getTestEnvironmentCatalog() {
        setLoadingState(true)
        viewModelScope.launch {
            when (
                val result =
                    getTestEnvironmentCatalog.invoke(GetTestEnvironmentCatalog.Params(projectId!!))
            ) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            androidDeviceList = result.data.androidDeviceCatalog?.models
                                ?.filter { model -> model.supportedVersionIds?.isNotEmpty() == true }
                                ?: listOf(),
                            filteredAndroidDeviceList = result.data.androidDeviceCatalog?.models
                                ?.filter { model -> model.supportedVersionIds?.isNotEmpty() == true }
                                ?: listOf(),
                            locales = result.data.androidDeviceCatalog?.runtimeConfiguration?.locales
                                ?: listOf(),
                            filteredLocales = result.data.androidDeviceCatalog?.runtimeConfiguration?.locales
                                ?: listOf(),
                            orientations = result.data.androidDeviceCatalog?.runtimeConfiguration?.orientations
                                ?: listOf()
                        )
                    }
                    setLoadingState(false)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateMatrixErrorState.ENVIRONMENT_CATALOG)
                }
            }
        }
    }

    fun createMatrix() {
        if (uiState.value.stepThreeState.androidDeviceList.isNullOrEmpty()) {
            setErrorState(CreateMatrixErrorState.DEVICE_NOT_SELECTED)
            return
        } else {
            clearErrorState()
        }
        setLoadingState(true)
        viewModelScope.launch {
            val matrix = uiState.value.getTestMatrix(projectId!!, bucketName!!)
            when (createMatrix.invoke(CreateMatrix.Params(projectId, matrix))) {
                is Result.Success -> {
                    setLoadingState(false)
                    onBackPressed()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateMatrixErrorState.CREATE_MATRIX)
                }
            }
        }
    }

    fun uploadFile(contentResolver: ContentResolver, fileUri: Uri) {
        val stream = contentResolver.openInputStream(fileUri) ?: return
        val bytes = stream.readBytes()
        val file = fileUri.path?.let { File(it) } ?: return
        val contentType = contentResolver.getType(fileUri) ?: DEFAULT_MIME_TYPE
        val length = bytes.size.toLong()
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = uploadFile.invoke(
                UploadFile.Params(
                    length,
                    contentType,
                    bucketName!!,
                    file.name,
                    bytes
                )
            )
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        val isTestAppEditing = it.stepTwoState.isTestAppEditing
                        val stepTwoState = if (isTestAppEditing) {
                            it.stepTwoState.copy(
                                testGcsPath = result.data.name?.createGCSPath(
                                    bucketName!!
                                )
                            )
                        } else {
                            it.stepTwoState.copy(
                                gcsPath = result.data.name?.createGCSPath(
                                    bucketName!!
                                )
                            )
                        }
                        it.copy(stepTwoState = stepTwoState)
                    }
                    setLoadingState(false)
                    stream.close()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setErrorState(CreateMatrixErrorState.UPLOAD_FILE)
                    stream.close()
                }
            }
        }
    }

    fun updateStep(updatedStep: Int): Boolean {
        val currentStep = uiState.value.currentStep
        when (currentStep to updatedStep) {
            STEP_ONE to STEP_TWO -> {
                if (!uiState.value.stepOneState.testTimeout.isValidTimeout()) {
                    setErrorState(CreateMatrixErrorState.TEST_TIMEOUT_INVALID)
                    return false
                } else if (
                    uiState.value.stepOneState.testType is TestType.Unspecified
                ) {
                    setErrorState(CreateMatrixErrorState.TEST_TYPE_NOT_SELECTED)
                    return false
                } else {
                    clearErrorState()
                }
            }
            STEP_TWO to STEP_THREE -> {
                if (uiState.value.stepTwoState.gcsPath.isNullOrEmpty() || uiState.value.stepTwoState.gcsPath.isNullOrBlank()) {
                    setErrorState(CreateMatrixErrorState.APP_NOT_SELECTED)
                    return false
                } else if ((
                    uiState.value.stepTwoState.testGcsPath.isNullOrEmpty() ||
                        uiState.value.stepTwoState.testGcsPath.isNullOrBlank()
                    ) &&
                    uiState.value.stepOneState.testType is TestType.AndroidInstrumentationTest
                ) {
                    setErrorState(CreateMatrixErrorState.TEST_APP_NOT_SELECTED)
                    return false
                } else {
                    clearErrorState()
                }
            }
        }
        _uiState.update {
            it.copy(currentStep = updatedStep)
        }
        return true
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _createMatrixEvent.send(CreateMatrixEvent.Back)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun setErrorState(errorState: CreateMatrixErrorState) {
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
            it.copy(stepOneState = stepOneState)
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

    fun retryOperation(errorState: CreateMatrixErrorState) {
        when (errorState) {
            CreateMatrixErrorState.CREATE_MATRIX -> createMatrix()
            CreateMatrixErrorState.ENVIRONMENT_CATALOG, CreateMatrixErrorState.DEFAULT_BUCKET -> {
                getDefaultBucket()
                getTestEnvironmentCatalog()
            }
            else -> {}
        }
    }

    fun updateSelectedItem(androidModel: AndroidDevice?) {
        _uiState.update {
            val stepThreeState = it.stepThreeState.copy(selectedModel = androidModel)
            it.copy(stepThreeState = stepThreeState)
        }
    }

    fun updateLocaleFilter(localeFilterInput: String) {
        _uiState.update {
            val filteredLocales =
                if (localeFilterInput.isEmpty() || localeFilterInput.isBlank()) it.locales
                else it.locales.filter {
                    it.name?.startsWith(
                        localeFilterInput,
                        ignoreCase = true
                    ) == true
                }
            it.copy(localeFilterInput = localeFilterInput, filteredLocales = filteredLocales)
        }
    }

    fun updateDeviceFilter(deviceFilterInput: String) {
        _uiState.update {
            val filteredAndroidDeviceList =
                if (deviceFilterInput.isEmpty() || deviceFilterInput.isBlank()) it.androidDeviceList
                else it.androidDeviceList.filter {
                    it.name?.startsWith(
                        deviceFilterInput,
                        ignoreCase = true
                    ) == true
                }
            it.copy(
                deviceFilterInput = deviceFilterInput,
                filteredAndroidDeviceList = filteredAndroidDeviceList
            )
        }
    }

    fun clearApp(isTestApp: Boolean = false) {
        _uiState.update {
            val stepTwoState =
                if (isTestApp) it.stepTwoState.copy(testGcsPath = null) else it.stepTwoState.copy(
                    gcsPath = null
                )
            it.copy(stepTwoState = stepTwoState)
        }
    }

    fun setBottomSheetState(isBottomSheetOpened: Boolean) {
        _uiState.update {
            it.copy(isBottomSheetOpened = isBottomSheetOpened)
        }
    }

    fun addDevice(
        androidModel: AndroidDevice
    ) {
        val deviceList =
            uiState.value.stepThreeState.androidDeviceList?.toMutableList() ?: mutableListOf()
        deviceList.add(androidModel)
        _uiState.update {
            val stepThreeState =
                it.stepThreeState.copy(androidDeviceList = deviceList, selectedModel = null)
            it.copy(stepThreeState = stepThreeState)
        }
        updateLocaleFilter("")
        updateDeviceFilter("")
    }

    fun removeDevice(index: Int) {
        val deviceList =
            uiState.value.stepThreeState.androidDeviceList?.toMutableList() ?: mutableListOf()
        deviceList.removeAt(index)
        _uiState.update {
            val stepThreeState = it.stepThreeState.copy(androidDeviceList = deviceList)
            it.copy(stepThreeState = stepThreeState)
        }
    }

    fun browseFilesOnCloudStorage(isTestApp: Boolean) {
        setTestAppIsEditing(isTestApp)
        viewModelScope.launch {
            _createMatrixEvent.send(CreateMatrixEvent.SelectFile(bucketName!!))
        }
    }

    fun setTestAppIsEditing(isTestApp: Boolean) {
        _uiState.update {
            val stepTwoState = it.stepTwoState.copy(isTestAppEditing = isTestApp)
            it.copy(stepTwoState = stepTwoState)
        }
    }

    fun String?.isValidTimeout() =
        !this.isNullOrEmpty() && !this.isBlank() && (this.toIntOrNull() ?: -1) in 0 until 5000
}

fun String.createGCSPath(bucketName: String): String {
    return "${CreateMatrixViewModel.GCS_PATH_PREFIX}$bucketName/$this"
}

data class CreateMatrixUiState(
    val errorState: CreateMatrixErrorState? = null,
    val currentStep: Int = STEP_ONE,
    val androidDeviceList: List<AndroidModel> = listOf(),
    val filteredAndroidDeviceList: List<AndroidModel> = listOf(),
    val locales: List<Locale> = listOf(),
    val filteredLocales: List<Locale> = listOf(),
    val orientations: List<Orientation> = listOf(),
    val deviceFilterInput: String = "",
    val localeFilterInput: String = "",
    val stepOneState: StepOneState = StepOneState(),
    val stepTwoState: StepTwoState = StepTwoState(),
    val stepThreeState: StepThreeState = StepThreeState(),
    val isLoading: Boolean = false,
    val isSnackbarOpened: Boolean = false,
    val isBottomSheetOpened: Boolean = false
) {

    fun getTestMatrix(projectId: String, bucketName: String): TestMatrix {
        return TestMatrix(
            projectId = projectId,
            testSpecification = TestSpecification(
                testTimeout = stepOneState.testTimeout + "s",
                disableVideoRecording = stepOneState.disableVideoRecording,
                disablePerformanceMetrics = stepOneState.disablePerformanceMetrics,
                testSetup = TestSetup(
                    account = if (stepOneState.accountTestEnabled) Account(GoogleAuto()) else null,
                    filesToPush = listOf(
                        DeviceFile(
                            null,
                            regularFile = RegularFile(
                                FileReference(stepTwoState.gcsPath),
                                devicePath = "/storage"
                            )
                        )
                    ),
                    directoriesToPull = listOf("/storage")
                ),
                androidInstrumentationTest = stepOneState.testType.takeIf { it is TestType.AndroidInstrumentationTest }
                    ?.let {
                        AndroidInstrumentationTest(
                            testApk = FileReference(
                                stepTwoState.testGcsPath
                            ),
                            appApk = stepTwoState.gcsPath.takeIf { it?.endsWith(".apk") ?: false }
                                ?.let {
                                    FileReference(it)
                                },
                            appBundle = stepTwoState.gcsPath.takeIf {
                                it?.endsWith(".aab") ?: false
                            }?.let {
                                AppBundle(bundleLocation = FileReference(it))
                            },
                            appPackageId = stepTwoState.packageId,
                            testPackageId = stepTwoState.testPackageId
                        )
                    },
                androidRoboTest = stepOneState.testType.takeIf { it is TestType.AndroidRoboTest }
                    ?.let {
                        AndroidRoboTest(
                            appApk = stepTwoState.gcsPath.takeIf { it?.endsWith(".apk") ?: false }
                                ?.let {
                                    FileReference(it)
                                },
                            appBundle = stepTwoState.gcsPath.takeIf {
                                it?.endsWith(".aab") ?: false
                            }?.let {
                                AppBundle(bundleLocation = FileReference(it))
                            },
                            appPackageId = stepTwoState.packageId
                        )
                    },
                androidTestLoop = stepOneState.testType.takeIf { it is TestType.AndroidTestLoop }
                    ?.let {
                        AndroidTestLoop(
                            appApk = stepTwoState.gcsPath.takeIf { it?.endsWith(".apk") ?: false }
                                ?.let {
                                    FileReference(it)
                                },
                            appBundle = stepTwoState.gcsPath.takeIf {
                                it?.endsWith(".aab") ?: false
                            }?.let {
                                AppBundle(bundleLocation = FileReference(it))
                            },
                            appPackageId = stepTwoState.packageId
                        )
                    }
            ),
            environmentMatrix = EnvironmentMatrix(
                androidDeviceList = AndroidDeviceList(
                    androidDevices = stepThreeState.androidDeviceList?.map {
                        it.copy(
                            localeString = it.locale?.id,
                            orientationString = it.orientation?.id
                        )
                    }
                )
            ),
            resultStorage = ResultStorage(
                googleCloudStorage = GoogleCloudStorage(
                    gcsPath = "konsol-result-${System.currentTimeMillis()}".createGCSPath(bucketName)
                )
            )
        )
    }
}

data class StepTwoState(
    val gcsPath: String? = null,
    val testGcsPath: String? = null,
    val packageId: String? = null,
    val testPackageId: String? = null,
    val isTestAppEditing: Boolean = false
)

data class StepOneState(
    val testTimeout: String? = null,
    val disableVideoRecording: Boolean = false,
    val disablePerformanceMetrics: Boolean = false,
    val accountTestEnabled: Boolean = false,
    val testType: TestType = TestType.Unspecified()
)

data class StepThreeState(
    val androidDeviceList: List<AndroidDevice>? = null,
    val selectedModel: AndroidDevice? = null
)

sealed class TestType(open val value: String = "") {
    data class AndroidRoboTest(override val value: String = "Android Robo Test") : TestType(value)
    data class AndroidInstrumentationTest(override val value: String = "Android Instrumentation Test") :
        TestType(value)

    data class AndroidTestLoop(override val value: String = "Android Test Loop") : TestType(value)
    data class Unspecified(override val value: String = "") : TestType(value)
}

sealed class CreateMatrixEvent {
    object Back : CreateMatrixEvent()
    data class SelectFile(val bucketName: String) : CreateMatrixEvent()
}

enum class CreateMatrixErrorState(
    @StringRes val titleResId: Int = R.string.error_occurred,
    @StringRes val actionResId: Int? = R.string.retry
) {
    CREATE_MATRIX,
    APP_NOT_SELECTED(R.string.app_not_selected, null),
    TEST_APP_NOT_SELECTED(R.string.test_app_not_selected, null),
    TEST_TYPE_NOT_SELECTED(R.string.test_type_not_selected, null),
    TEST_TIMEOUT_INVALID(R.string.test_timeout_invalid, null),
    TEST_PACKAGE_ID_INVALID(R.string.test_package_id_invalid, null),
    PACKAGE_ID_INVALID(R.string.package_id_invalid, null),
    DEVICE_NOT_SELECTED(R.string.device_not_selected, null),
    UPLOAD_FILE,
    DEFAULT_BUCKET(R.string.error_occurred_default_bucket, null),
    ENVIRONMENT_CATALOG,
}
