package com.batuhan.oauth2.domain

import com.batuhan.core.util.AuthStateManager
import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GetOauthToken @Inject constructor(authStateManager: AuthStateManager) {

    data class Params(
        val response: AuthorizationResponse,
        val authorizationService: AuthorizationService
    )

    suspend operator fun invoke(params: Params): Result<Pair<TokenResponse?, AuthorizationException?>?> {
        return suspendCoroutine { continuation ->
            runCatching {
                params.authorizationService.performTokenRequest(
                    params.response.createTokenExchangeRequest()
                ) { resp, ex ->
                    ex?.let {
                        continuation.resume(Result.Error(ExceptionType.APPAUTH_TOKEN_REQUEST_EXCEPTION, it))
                    }
                    continuation.resume(Result.Success(Pair(resp, ex)))
                }
            }.getOrElse {
                continuation.resume(Result.Error(ExceptionType.APPAUTH_INTERNAL_ERROR, it))
            }
        }
    }
}
