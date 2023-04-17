package com.batuhan.oauth2.domain

import android.net.Uri
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GetServiceConfiguration @Inject constructor() {

    suspend operator fun invoke(): AuthorizationServiceConfiguration? {
        return suspendCoroutine { continuation ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse("https://accounts.google.com")
            ) { serviceConfiguration, ex ->
                if (ex != null) {
                    continuation.resume(null)
                }
                if (serviceConfiguration == null) {
                    continuation.resume(null)
                }
                continuation.resume(serviceConfiguration)
            }
        }

    }
}
