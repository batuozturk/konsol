package com.batuhan.cloudmessaging.domain

import com.batuhan.cloudmessaging.data.model.Message
import com.batuhan.cloudmessaging.data.model.MessageRequest
import com.batuhan.cloudmessaging.data.repository.CloudMessagingRepository
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import javax.inject.Inject

class SendMessage @Inject constructor(private val cloudMessagingRepository: CloudMessagingRepository) {

    data class Params(val projectId: String, val messageRequest: MessageRequest)

    suspend operator fun invoke(params: Params): Result<Message> {
        return runCatching {
            Result.Success(
                cloudMessagingRepository.sendMessage(
                    params.projectId,
                    params.messageRequest
                )
            )
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
