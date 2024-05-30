package com.batuhan.realtimedatabase.data.source

import com.batuhan.realtimedatabase.data.model.DatabaseInstance
import com.batuhan.realtimedatabase.data.model.DatabaseInstanceResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RealtimeDatabaseInstanceService {

    companion object {
        private const val PATH_VERSION = "v1beta"
    }

    @POST("$PATH_VERSION/projects/{projectId}/locations/{locationId}/instances")
    suspend fun createDatabaseInstance(
        @Path("projectId") projectId: String,
        @Path("locationId") locationId: String,
        @Query("databaseId") databaseId: String,
        @Body databaseInstance: DatabaseInstance
    ): DatabaseInstance

    @GET("$PATH_VERSION/projects/{projectId}/locations/-/instances")
    suspend fun getDatabaseInstances(
        @Path("projectId") projectId: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("pageSize") pageSize: Int = 20
    ): DatabaseInstanceResponse
}
