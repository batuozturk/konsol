package com.batuhan.testlab.data.model.execution

import com.google.gson.annotations.SerializedName

data class ExecutionEnvironment(
    @SerializedName("completionTime") val completionTime: Timestamp?,
    @SerializedName("creationTime") val creationTime: Timestamp?,
    @SerializedName("environmentId") val environmentId: String?,
    @SerializedName("environmentResult") val environmentResult: EnvironmentResult?,
    @SerializedName("dimensionValue") val dimensionValue: List<DimensionValue>?,
    @SerializedName("executionId") val executionId: String?,
    @SerializedName("shardSummaries") val shardSummaries: List<ShardSummary>?
)

data class EnvironmentResult(
    @SerializedName("outcome") val outcome: Outcome?,
    @SerializedName("state") val state: ExecutionState?
)

data class EnvironmentOutcome(
    @SerializedName("failureDetail") val failureDetail: FailureDetail?,
    @SerializedName("summary") val summary: String?
)

data class FailureDetail(
    @SerializedName("crashed") val crashed: Boolean?
)

data class ShardResult(
    @SerializedName("outcome") val outcome: EnvironmentOutcome?,
    @SerializedName("state") val state: ExecutionState?
)

data class ShardSummary(
    @SerializedName("shardResult") val shardResult: ShardResult?
)

data class ExecutionEnvironmentListResponse(
    @SerializedName("environments") val environments: List<ExecutionEnvironment>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)

data class DimensionValue(
    @SerializedName("key") val key: String?,
    @SerializedName("value") val value: String?
)
