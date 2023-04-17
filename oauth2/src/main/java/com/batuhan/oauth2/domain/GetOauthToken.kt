package com.batuhan.oauth2.domain

import com.batuhan.core.util.AuthStateManager
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

    suspend operator fun invoke(params: Params): Pair<TokenResponse?, AuthorizationException?>? {
        return suspendCoroutine {
            params.authorizationService.performTokenRequest(
                params.response.createTokenExchangeRequest()
            ) { resp, ex ->
                it.resume(Pair(resp, ex))
            }
        }
    }
}
