package com.batuhan.cloudmessaging.data.source

import com.batuhan.cloudmessaging.data.model.Message
import com.batuhan.cloudmessaging.data.model.MessageRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface CloudMessagingService {

    companion object {
        private const val PATH_VERSION = "v1"
    }

    @POST("$PATH_VERSION/projects/{projectId}/messages:send")
    suspend fun sendMessage(
        @Path("projectId") projectId: String,
        @Body messageRequest: MessageRequest
    ): Message
}
