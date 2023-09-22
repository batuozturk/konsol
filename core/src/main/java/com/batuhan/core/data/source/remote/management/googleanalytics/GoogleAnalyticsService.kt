package com.batuhan.core.data.source.remote.management.googleanalytics

import com.batuhan.core.data.model.management.AnalyticsAccountResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleAnalyticsService {

    companion object {
        const val PATH = "v3"
    }

    @GET("$PATH/management/accounts")
    suspend fun getGoogleAnalyticsAccounts(): AnalyticsAccountResponse

    @GET("$PATH/management/accounts")
    suspend fun getAnalyticsAccounts(
        @Query("max-results") maxResults: Int = 5,
        @Query("start-index") startIndex: Int
    ): AnalyticsAccountResponse
}
