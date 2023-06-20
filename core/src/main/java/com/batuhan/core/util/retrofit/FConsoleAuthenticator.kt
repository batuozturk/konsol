package com.batuhan.core.util.retrofit

import com.batuhan.core.util.AuthStateManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FConsoleAuthenticator @Inject constructor(private val authStateManager: AuthStateManager) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val newAccessToken = authStateManager.refreshToken()
            response.request().newBuilder().addHeader("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }


}
