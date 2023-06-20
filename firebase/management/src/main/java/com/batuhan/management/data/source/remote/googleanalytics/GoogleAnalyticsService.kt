package com.batuhan.management.data.source.remote.googleanalytics

import com.batuhan.management.data.model.AnalyticsAccountResponse
import retrofit2.http.GET

interface GoogleAnalyticsService {

    companion object {
        const val PATH = "v3"
    }

    @GET("$PATH/management/accounts")
    suspend fun getGoogleAnalyticsAccounts(): AnalyticsAccountResponse
}
