package com.batuhan.cloudmessaging.data.repository

import com.batuhan.cloudmessaging.data.model.Message
import com.batuhan.cloudmessaging.data.model.MessageRequest

interface CloudMessagingRepository {

    suspend fun sendMessage(projectId: String, messageRequest: MessageRequest): Message
}
