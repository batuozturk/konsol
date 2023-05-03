package com.batuhan.oauth2.domain

import android.content.Intent
import android.net.Uri
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.CodeVerifierUtil
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GetAuthorizationRequestIntent @Inject constructor() {

    data class Params(
        val serviceConfiguration: AuthorizationServiceConfiguration,
        val clientId: String,
        val email: String,
        val scope: String,
        val redirectUri: String,
        val authorizationService: AuthorizationService
    )

    suspend operator fun invoke(params: Params): Result<Intent> {
        return suspendCoroutine { continuation ->
            runCatching {
                val request =
                    AuthorizationRequest.Builder(
                        params.serviceConfiguration,
                        params.clientId,
                        ResponseTypeValues.CODE,
                        Uri.parse(params.redirectUri)
                    ).setScope(params.scope).setLoginHint(params.email)
                        .setCodeVerifier(CodeVerifierUtil.generateRandomCodeVerifier()).build()
                continuation.resume(
                    Result.Success(
                        params.authorizationService.getAuthorizationRequestIntent(
                            request
                        )
                    )
                )
            }.getOrElse {
                continuation.resume(Result.Error(ExceptionType.APPAUTH_INTERNAL_ERROR, it))
            }
        }
    }
}
