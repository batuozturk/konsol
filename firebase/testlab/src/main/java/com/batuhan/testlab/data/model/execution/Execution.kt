package com.batuhan.testlab.data.model.execution

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.batuhan.theme.DarkGreen
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Execution(
    @SerializedName("executionId") val executionId: String?,
    @SerializedName("state") val state: ExecutionState?,
    @SerializedName("creationTime") val creationTime: Timestamp?,
    @SerializedName("completionTime") val completionTime: Timestamp?,
    @SerializedName("outcome") val outcome: Outcome?,
    @SerializedName("specification") val specification: Specification?,
    @SerializedName("testExecutionMatrixId") val textExecutionMatrixId: String?
)

data class ExecutionListResponse(
    @SerializedName("executions") val executions: List<Execution>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)

enum class ExecutionState {
    pending,
    inProgress,
    complete;
}

data class Timestamp(
    @SerializedName("seconds") val seconds: Long?,
    @SerializedName("nanoseconds") val nanoseconds: Long?
) {
    fun getDate(): String {
        val date = Date().apply { time = this@Timestamp.seconds?.times(1000) ?: 0L }
        return SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(date)
    }
}

data class Outcome(
    @SerializedName("summary") val summary: OutcomeSummary?
)

enum class OutcomeSummary(val imageVector: ImageVector, val tint: Color) {
    success(Icons.Default.Done, DarkGreen),
    failure(Icons.Default.ErrorOutline, Color.Red),
    inconclusive(Icons.Default.ErrorOutline, Color.Gray),
    skipped(Icons.Default.ErrorOutline, Color.Gray),
    flaky(Icons.Default.ErrorOutline, Color.Gray);
}

data class Specification(
    @SerializedName("androidTest") val androidTest: AndroidTest?,
    @SerializedName("iosTest") val iosTest: IosTest?
)
