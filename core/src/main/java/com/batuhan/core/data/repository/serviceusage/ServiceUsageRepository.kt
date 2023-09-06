package com.batuhan.core.data.repository.serviceusage

import com.batuhan.core.data.model.ApiService
import com.batuhan.core.data.model.Operation

interface ServiceUsageRepository {

    suspend fun enableService(serviceName: String): Operation

    suspend fun getServiceUsageOperation(operationName: String): Operation

    suspend fun getServiceEnableState(serviceName: String): ApiService
}
