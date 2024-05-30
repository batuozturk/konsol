package com.batuhan.core.util

import android.content.Context
import com.batuhan.core.data.OauthUser
import com.batuhan.core.domain.*
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    private val addAuthenticatedUser: AddAuthenticatedUser,
    private val getAuthenticatedUser: GetAuthenticatedUser,
    private val deleteAuthenticatedUser: DeleteAuthenticatedUser,
    private val updateAuthenticatedUser: UpdateAuthenticatedUser,
    private val refreshToken: RefreshToken,
    @ApplicationContext private val context: Context
) {

    private var oauthUser: OauthUser? = null

    suspend fun getAuthState(): Result<OauthUser?> {
        return getAuthenticatedUser.invoke()
    }

    suspend fun deleteAuthState() {
        this.oauthUser?.let { deleteAuthenticatedUser.invoke(DeleteAuthenticatedUser.Params(it)) }
        clearAuthState()
    }

    suspend fun addAuthState(authState: AuthState) {
        val oauthUser = OauthUser(authState = authState)
        when (val result = addAuthenticatedUser.invoke(AddAuthenticatedUser.Params(oauthUser))) {
            is Result.Success -> {
                setAuthState(oauthUser.copy(id = result.data))
            }
            else -> throw Throwable()
        }
    }

    fun setAuthState(oauthUser: OauthUser) {
        this.oauthUser = oauthUser
    }

    fun clearAuthState() {
        oauthUser = null
    }

    suspend fun updateAuthState(result: Result<AuthState>): Result<Unit> {
        return when (result) {
            is Result.Error -> result
            is Result.Success -> {
                return oauthUser?.copy(authState = result.data)?.let {
                    setAuthState(it)
                    updateAuthenticatedUser.invoke(UpdateAuthenticatedUser.Params(it))
                } ?: Result.Error(ExceptionType.ROOM_DB_ERROR, Throwable())
            }
        }
    }

    suspend fun refreshToken(): String? {
        if (oauthUser == null) {
            val result = getAuthState()
            when (result) {
                is Result.Error -> {
                    throw Throwable()
                }

                is Result.Success -> {
                    result.data?.let {
                        setAuthState(it)
                    }
                }
            }
        }
        oauthUser?.authState?.takeIf { it.needsTokenRefresh }?.let {
            val params = RefreshToken.Params(it, AuthorizationService(context))
            val tokenRefreshResult = refreshToken.invoke(params)
            updateAuthState(tokenRefreshResult)
        }
        return getAccessToken()
    }

    fun getAccessToken() = oauthUser!!.authState.accessToken

    // Since Google Oauth2 service doesn't return endSessionUri,
    // there's no way that end session request by appauth won't be successful (the app crashes)
    // instead, clear authenticated user information which is written to room database

    /*suspend fun endSession(): Result<Intent?> {
        return oauthUser?.authState?.let {
            endSession.invoke(
                EndSession.Params(
                    it,
                    AuthorizationService(context)
                )
            )
        } ?: Result.Error(ExceptionType.ROOM_DB_ERROR, Throwable("No user found"))
    }*/
}
