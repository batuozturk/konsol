package com.batuhan.cloudmessaging.data.source

import com.batuhan.cloudmessaging.data.model.MessageRequest
import javax.inject.Inject

class CloudMessagingDataSource @Inject constructor(
    private val cloudMessagingService: CloudMessagingService
) {

    suspend fun sendMessage(projectId: String, messageRequest: MessageRequest) =
        cloudMessagingService.sendMessage(projectId, messageRequest)
}
