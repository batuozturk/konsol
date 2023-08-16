package com.batuhan.core.util.retrofit

import com.batuhan.core.util.AuthStateManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FConsoleInterceptor @Inject constructor(
    private val authStateManager: AuthStateManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val newAccessToken = authStateManager.refreshToken()
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $newAccessToken")
                    .build()
            )
        }
    }
}
