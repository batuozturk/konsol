package com.batuhan.core.data.source.remote.management.googleanalytics

import javax.inject.Inject

class GoogleAnalyticsRemoteDataSource @Inject constructor(private val googleAnalyticsService: GoogleAnalyticsService) {

    suspend fun getGoogleAnalyticsAccounts() = googleAnalyticsService.getGoogleAnalyticsAccounts()
}
