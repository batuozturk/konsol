package com.batuhan.management.data.source.remote.googleanalytics

import javax.inject.Inject

class GoogleAnalyticsRemoteDataSource @Inject constructor(private val googleAnalyticsService: GoogleAnalyticsService) {

    suspend fun getGoogleAnalyticsAccounts() = googleAnalyticsService.getGoogleAnalyticsAccounts()
}
