package com.batuhan.core.domain

import com.batuhan.core.util.ExceptionType
import com.batuhan.core.util.Result
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RefreshToken @Inject constructor() {

    data class Params(val authState: AuthState, val authorizationService: AuthorizationService)

    suspend operator fun invoke(params: Params): Result<AuthState> {
        return suspendCoroutine { continuation ->
            runCatching {
                val authState = params.authState
                authState.performActionWithFreshTokens(params.authorizationService) { accessToken, _, ex ->
                    if (ex != null) {
                        continuation.resume(
                            Result.Error(
                                ExceptionType.APPAUTH_REFRESH_TOKEN_EXCEPTION,
                                ex
                            )
                        )
                    }
                    if (accessToken != null) {
                        continuation.resume(Result.Success(authState))
                    }
                }
            }.getOrElse {
                continuation.resume(Result.Error(ExceptionType.APPAUTH_INTERNAL_ERROR, it))
            }
        }
    }
}
