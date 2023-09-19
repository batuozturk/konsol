package com.batuhan.testlab.data.model.matrix

import androidx.annotation.Keep
import com.batuhan.testlab.data.model.execution.AndroidInstrumentationTest
import com.batuhan.testlab.data.model.execution.AndroidRoboTest
import com.batuhan.testlab.data.model.execution.AndroidTestLoop
import com.google.gson.annotations.SerializedName

@Keep
data class TestSpecification(
    @SerializedName("testTimeout") val testTimeout: String?,
    @SerializedName("disableVideoRecording") val disableVideoRecording: Boolean?,
    @SerializedName("disablePerformanceMetrics") val disablePerformanceMetrics: Boolean?,
    @SerializedName("testSetup") val testSetup: TestSetup?,
    @SerializedName("androidInstrumentationTest") val androidInstrumentationTest: AndroidInstrumentationTest?,
    @SerializedName("androidRoboTest") val androidRoboTest: AndroidRoboTest?,
    @SerializedName("androidTestLoop") val androidTestLoop: AndroidTestLoop?
)
