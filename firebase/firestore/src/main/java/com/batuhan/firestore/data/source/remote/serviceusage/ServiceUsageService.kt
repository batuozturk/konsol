package com.batuhan.firestore.data.source.remote.serviceusage

import com.batuhan.core.data.model.Operation
import com.batuhan.firestore.data.model.ApiService
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceUsageService {

    companion object {
        private const val PATH = "v1"
    }

    @POST("$PATH/{serviceName}:enable")
    suspend fun enableService(@Path("serviceName", encoded = true) serviceName: String): Operation

    @GET("$PATH/{operationName}")
    suspend fun getServiceUsageOperation(
        @Path("operationName", encoded = true) operationName: String
    ): Operation

    @GET("$PATH/{serviceName}")
    suspend fun getServiceEnableState(
        @Path("serviceName", encoded = true) serviceName: String
    ): ApiService
}
