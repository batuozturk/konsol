package com.batuhan.testlab.data.model.matrix

import com.google.gson.annotations.SerializedName

data class TestMatrix(
    @SerializedName("testMatrixId") val testMatrixId: String? = null,
    @SerializedName("projectId") val projectId: String?,
    @SerializedName("testSpecification") val testSpecification: TestSpecification?,
    @SerializedName("environmentMatrix") val environmentMatrix: EnvironmentMatrix?,
    @SerializedName("testExecutions") val testExecution: List<TestExecution>? = null,
    @SerializedName("testState") val testState: TestState? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("resultStorage") val resultStorage: ResultStorage? = null
)
