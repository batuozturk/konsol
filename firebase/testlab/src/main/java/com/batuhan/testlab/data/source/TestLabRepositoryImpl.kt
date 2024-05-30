package com.batuhan.testlab.data.source

import com.batuhan.testlab.data.model.matrix.TestMatrix
import com.batuhan.testlab.data.source.result.TestLabToolResultsDataSource
import com.batuhan.testlab.data.source.testing.TestLabTestingDataSource
import javax.inject.Inject

class TestLabRepositoryImpl @Inject constructor(
    private val testLabTestingDataSource: TestLabTestingDataSource,
    private val testLabToolResultsDataSource: TestLabToolResultsDataSource
) :
    TestLabRepository {
    override suspend fun createMatrix(projectId: String, testMatrix: TestMatrix) =
        testLabTestingDataSource.createMatrix(projectId, testMatrix)

    override suspend fun cancelMatrix(projectId: String, testMatrixId: String) =
        testLabTestingDataSource.cancelMatrix(projectId, testMatrixId)

    override suspend fun getMatrix(projectId: String, testMatrixId: String) =
        testLabTestingDataSource.getMatrix(projectId, testMatrixId)

    override suspend fun getTestEnvironmentCatalog(projectId: String) =
        testLabTestingDataSource.getTestEnvironmentCatalog(projectId)

    override suspend fun getHistoryList(projectId: String) =
        testLabToolResultsDataSource.getHistoryList(projectId)

    override fun getExecutionList(
        projectId: String,
        historyId: String
    ) = testLabToolResultsDataSource.getExecutionList(projectId, historyId)

    override suspend fun getExecution(
        projectId: String,
        historyId: String,
        executionId: String
    ) = testLabToolResultsDataSource.getExecution(projectId, historyId, executionId)

    override fun getEnvironmentList(
        projectId: String,
        historyId: String,
        executionId: String
    ) = testLabToolResultsDataSource.getEnvironmentList(projectId, historyId, executionId)

    override suspend fun getEnvironment(
        projectId: String,
        historyId: String,
        executionId: String,
        environmentId: String
    ) = testLabToolResultsDataSource.getEnvironment(
        projectId,
        historyId,
        executionId,
        environmentId
    )
}
