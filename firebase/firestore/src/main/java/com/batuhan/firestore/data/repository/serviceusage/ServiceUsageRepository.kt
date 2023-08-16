package com.batuhan.firestore.data.repository.serviceusage

import com.batuhan.core.data.model.Operation
import com.batuhan.firestore.data.model.ApiService

interface ServiceUsageRepository {

    suspend fun enableService(serviceName: String): Operation

    suspend fun getServiceUsageOperation(operationName: String): Operation

    suspend fun getServiceEnableState(serviceName: String): ApiService
}
