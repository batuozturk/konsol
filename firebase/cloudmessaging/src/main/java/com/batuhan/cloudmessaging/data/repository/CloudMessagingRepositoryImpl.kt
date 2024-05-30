package com.batuhan.cloudmessaging.data.repository

import com.batuhan.cloudmessaging.data.model.MessageRequest
import com.batuhan.cloudmessaging.data.source.CloudMessagingDataSource
import javax.inject.Inject

class CloudMessagingRepositoryImpl @Inject constructor(
    private val cloudMessagingDataSource: CloudMessagingDataSource
) :
    CloudMessagingRepository {

    override suspend fun sendMessage(projectId: String, messageRequest: MessageRequest) =
        cloudMessagingDataSource.sendMessage(projectId, messageRequest)
}
