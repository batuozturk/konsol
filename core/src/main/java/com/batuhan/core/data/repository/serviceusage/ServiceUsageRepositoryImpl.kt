package com.batuhan.core.data.repository.serviceusage

import com.batuhan.core.data.source.remote.serviceusage.ServiceUsageRemoteDataSource
import javax.inject.Inject

class ServiceUsageRepositoryImpl @Inject constructor(
    private val serviceUsageRemoteDataSource: ServiceUsageRemoteDataSource
) : ServiceUsageRepository {

    override suspend fun enableService(serviceName: String) =
        serviceUsageRemoteDataSource.enableService(serviceName)

    override suspend fun getServiceUsageOperation(operationName: String) =
        serviceUsageRemoteDataSource.getServiceUsageOperation(operationName)

    override suspend fun getServiceEnableState(serviceName: String) =
        serviceUsageRemoteDataSource.getServiceEnableState(serviceName)
}
