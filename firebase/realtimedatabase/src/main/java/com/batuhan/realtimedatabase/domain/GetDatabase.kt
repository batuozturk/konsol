package com.batuhan.realtimedatabase.domain

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import com.batuhan.realtimedatabase.data.repository.RealtimeDatabaseInstanceRepository
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import javax.inject.Inject

class GetDatabase @Inject constructor(private val repository: RealtimeDatabaseInstanceRepository) {

    data class Params(val url: String)

    suspend operator fun invoke(params: Params): Result<Map<String, JsonElement?>?> {
        return runCatching {
            val result = repository.getDatabase(params.url)
            val map = if (result !is JsonNull) {
                val propertyToBeCopied = result as JsonObject
                propertyToBeCopied.asMap()
            } else {
                emptyMap<String, JsonElement?>()
            }
            Result.Success(map)
        }.getOrElse {
            Result.Error(ExceptionType.FIREBASE_API_EXCEPTION, it)
        }
    }
}
