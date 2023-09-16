package com.batuhan.testlab.data.model.execution

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ExecutionEnvironment(
    @SerializedName("completionTime") val completionTime: Timestamp?,
    @SerializedName("creationTime") val creationTime: Timestamp?,
    @SerializedName("environmentId") val environmentId: String?,
    @SerializedName("environmentResult") val environmentResult: EnvironmentResult?,
    @SerializedName("dimensionValue") val dimensionValue: List<DimensionValue>?,
    @SerializedName("executionId") val executionId: String?,
    @SerializedName("shardSummaries") val shardSummaries: List<ShardSummary>?
)

@Keep
data class EnvironmentResult(
    @SerializedName("outcome") val outcome: Outcome?,
    @SerializedName("state") val state: ExecutionState?
)

@Keep
data class EnvironmentOutcome(
    @SerializedName("failureDetail") val failureDetail: FailureDetail?,
    @SerializedName("summary") val summary: String?
)

@Keep
data class FailureDetail(
    @SerializedName("crashed") val crashed: Boolean?
)

@Keep
data class ShardResult(
    @SerializedName("outcome") val outcome: EnvironmentOutcome?,
    @SerializedName("state") val state: ExecutionState?
)

@Keep
data class ShardSummary(
    @SerializedName("shardResult") val shardResult: ShardResult?
)

@Keep
data class ExecutionEnvironmentListResponse(
    @SerializedName("environments") val environments: List<ExecutionEnvironment>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)

@Keep
data class DimensionValue(
    @SerializedName("key") val key: String?,
    @SerializedName("value") val value: String?
)
