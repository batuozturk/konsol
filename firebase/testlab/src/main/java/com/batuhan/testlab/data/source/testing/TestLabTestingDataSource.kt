package com.batuhan.testlab.data.source.testing

import com.batuhan.testlab.data.model.matrix.EnvironmentType
import com.batuhan.testlab.data.model.matrix.TestMatrix
import javax.inject.Inject

class TestLabTestingDataSource @Inject constructor(private val testLabTestingService: TestLabTestingService) {

    suspend fun createMatrix(projectId: String, testMatrix: TestMatrix) =
        testLabTestingService.createMatrix(projectId, testMatrix)

    suspend fun cancelMatrix(projectId: String, testMatrixId: String) =
        testLabTestingService.cancelMatrix(projectId, testMatrixId)

    suspend fun getMatrix(projectId: String, testMatrixId: String) =
        testLabTestingService.getMatrix(projectId, testMatrixId)

    suspend fun getTestEnvironmentCatalog(projectId: String) =
        testLabTestingService.getTestEnvironmentCatalog(EnvironmentType.ANDROID, projectId)
}
