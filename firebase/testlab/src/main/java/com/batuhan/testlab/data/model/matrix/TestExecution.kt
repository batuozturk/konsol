package com.batuhan.testlab.data.model.matrix

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TestExecution(
    @SerializedName("id") val id: String?,
    @SerializedName("matrixId") val matrixId: String?,
    @SerializedName("projectId") val projectId: String?,
    @SerializedName("testSpecification") val testSpecification: TestSpecification?,
    @SerializedName("shard") val shard: Shard?,
    @SerializedName("environment") val environment: Environment?,
    @SerializedName("state") val state: TestState?,
    @SerializedName("toolResultsStep") val toolResultsStep: ToolResultsStep?,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("testDetails") val testDetails: TestDetails?
)

@Keep
data class Environment(
    @SerializedName("androidDevice") val androidDevice: AndroidDevice?
)

@Keep
enum class TestState {
    TEST_STATE_UNSPECIFIED, VALIDATING, PENDING, RUNNING,
    FINISHED, ERROR, UNSUPPORTED_ENVIRONMENT, INCOMPATIBLE_ENVIRONMENT,
    INCOMPATIBLE_ARCHITECTURE, CANCELLED, INVALID
}

@Keep
data class ToolResultsStep(
    @SerializedName("projectId") val projectId: String?,
    @SerializedName("historyId") val historyId: String?,
    @SerializedName("executionId") val executionId: String?,
    @SerializedName("stepId") val stepId: String?
)

@Keep
data class TestDetails(
    @SerializedName("progressMessages") val progressMessages: List<String>?,
    @SerializedName("errorMessage") val errorMessage: String?
)
