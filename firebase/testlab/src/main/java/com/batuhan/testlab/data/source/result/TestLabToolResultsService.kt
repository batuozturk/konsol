package com.batuhan.testlab.data.source.result

import com.batuhan.testlab.data.model.*
import com.batuhan.testlab.data.model.execution.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TestLabToolResultsService {

    companion object {
        private const val PATH_VERSION = "toolresults/v1beta3"
    }

    @GET("$PATH_VERSION/projects/{projectId}/histories")
    suspend fun getHistoryList(@Path("projectId") projectId: String): HistoryListResponse

    @GET("$PATH_VERSION/projects/{projectId}/histories/{historyId}/executions")
    suspend fun getExecutionList(
        @Path("projectId") projectId: String,
        @Path("historyId") historyId: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("pageToken") pageToken: String? = null
    ): ExecutionListResponse

    @GET("$PATH_VERSION/projects/{projectId}/histories/{historyId}/executions/{executionId}")
    suspend fun getExecution(
        @Path("projectId") projectId: String,
        @Path("historyId") historyId: String,
        @Path("executionId") executionId: String
    ): Execution

    @GET("$PATH_VERSION/projects/{projectId}/histories/{historyId}/executions/{executionId}/environments")
    suspend fun getEnvironmentList(
        @Path("projectId") projectId: String,
        @Path("historyId") historyId: String,
        @Path("executionId") executionId: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("pageToken") pageToken: String? = null
    ): ExecutionEnvironmentListResponse

    @GET("$PATH_VERSION/projects/{projectId}/histories/{historyId}/executions/{executionId}/environments/{environmentId}")
    suspend fun getEnvironment(
        @Path("projectId") projectId: String,
        @Path("historyId") historyId: String,
        @Path("executionId") executionId: String,
        @Path("environmentId") environmentId: String
    ): ExecutionEnvironment
}
