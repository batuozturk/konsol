package com.batuhan.management.data.source.remote.googleanalytics

import com.batuhan.management.data.model.AnalyticsAccountResponse
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
