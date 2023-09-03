package com.batuhan.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.batuhan.core.data.source.remote.CloudStorageDataSource
import com.batuhan.core.data.source.remote.CloudStorageService
import com.batuhan.core.data.source.remote.GetObjectListPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CloudStorageRepositoryImpl @Inject constructor(
    private val cloudStorageService: CloudStorageService,
    private val cloudStorageDataSource: CloudStorageDataSource
) :
    CloudStorageRepository {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getObjectList(bucketName: String, prefix: String?): Flow<PagingData<String>> =
        Pager(
            config = PagingConfig(PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                GetObjectListPagingSource(cloudStorageService).apply {
                    setBucketName(bucketName)
                    setPrefix(prefix)
                }
            }
        ).flow

    override suspend fun uploadFile(
        contentLength: Long,
        contentType: String,
        bucketName: String,
        name: String,
        data: ByteArray
    ) = cloudStorageDataSource.uploadFile(contentLength, contentType, bucketName, name, data)

    override suspend fun getDefaultBucket(projectId: String) =
        cloudStorageDataSource.getDefaultBucket(projectId)
}
