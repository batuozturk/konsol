package com.batuhan.core.domain.cloudstorage

import com.batuhan.core.data.repository.CloudStorageRepository
import javax.inject.Inject

class GetObjectList @Inject constructor(private val cloudStorageRepository: CloudStorageRepository) {

    data class Params(val bucketName: String, val prefix: String?)

    operator fun invoke(params: Params) =
        cloudStorageRepository.getObjectList(params.bucketName, params.prefix)
}
