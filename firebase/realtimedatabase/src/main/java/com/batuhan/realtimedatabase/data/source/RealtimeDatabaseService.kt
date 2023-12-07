package com.batuhan.realtimedatabase.data.source

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Url

interface RealtimeDatabaseService {

    @GET
    suspend fun getDatabase(@Url url: String): JsonElement?

    @DELETE
    suspend fun deleteData(@Url url: String)

    @PATCH
    suspend fun patchData(@Url url: String, @Body patchBody: JsonObject)
}