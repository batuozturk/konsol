package com.batuhan.oauth2.domain

import android.net.Uri
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import net.openid.appauth.AuthorizationServiceConfiguration
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GetServiceConfiguration @Inject constructor() {

    suspend operator fun invoke(): Result<AuthorizationServiceConfiguration?> {
        return suspendCoroutine { continuation ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse("https://accounts.google.com")
            ) { serviceConfiguration, ex ->
                if (ex != null) {
                    continuation.resume(
                        Result.Error(
                            ExceptionType.APPAUTH_SERVICE_CONFIG_EXCEPTION,
                            ex
                        )
                    )
                }
                if (serviceConfiguration == null) {
                    continuation.resume(
                        Result.Error(
                            ExceptionType.APPAUTH_SERVICE_CONFIG_INVALID,
                            Throwable("Authorization service config is invalid")
                        )
                    )
                }
                continuation.resume(Result.Success(serviceConfiguration))
            }
        }
    }
}
