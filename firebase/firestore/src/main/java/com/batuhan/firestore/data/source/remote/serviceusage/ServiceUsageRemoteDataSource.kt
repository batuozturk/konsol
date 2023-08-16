package com.batuhan.firestore.data.source.remote.serviceusage

import javax.inject.Inject

class ServiceUsageRemoteDataSource @Inject constructor(private val serviceUsageService: ServiceUsageService) {

    suspend fun enableService(serviceName: String) = serviceUsageService.enableService(serviceName)

    suspend fun getServiceUsageOperation(operationName: String) =
        serviceUsageService.getServiceUsageOperation(operationName)

    suspend fun getServiceEnableState(serviceName: String) =
        serviceUsageService.getServiceEnableState(serviceName)
}
