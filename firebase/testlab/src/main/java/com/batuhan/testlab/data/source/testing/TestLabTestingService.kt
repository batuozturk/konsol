package com.batuhan.testlab.data.source.testing

import com.batuhan.testlab.data.model.devicecatalog.TestEnvironmentCatalog
import com.batuhan.testlab.data.model.matrix.EnvironmentType
import com.batuhan.testlab.data.model.matrix.TestMatrix
import com.batuhan.testlab.data.model.matrix.TestState
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TestLabTestingService {

    companion object {
        private const val PATH_VERSION = "v1"
    }

    @POST("$PATH_VERSION/projects/{projectId}/testMatrices")
    suspend fun createMatrix(
        @Path("projectId") projectId: String,
        @Body testMatrix: TestMatrix
    ): TestMatrix

    @POST("$PATH_VERSION/projects/{projectId}/testMatrices/{testMatrixId}:cancel")
    suspend fun cancelMatrix(
        @Path("projectId") projectId: String,
        @Path("testMatrixId") textMatrixId: String
    ): TestState

    @GET("$PATH_VERSION/projects/{projectId}/testMatrices/{testMatrixId}")
    suspend fun getMatrix(
        @Path("projectId") projectId: String,
        @Path("testMatrixId") textMatrixId: String
    ): TestMatrix

    @GET("$PATH_VERSION/testEnvironmentCatalog/{environmentType}")
    suspend fun getTestEnvironmentCatalog(
        @Path("environmentType") environmentType: EnvironmentType,
        @Query("projectId") projectId: String
    ): TestEnvironmentCatalog
}
