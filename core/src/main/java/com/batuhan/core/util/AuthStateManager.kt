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

    suspend fun deleteAuthState(oauthUser: OauthUser) =
        deleteAuthenticatedUser.invoke(DeleteAuthenticatedUser.Params(oauthUser))

    suspend fun addAuthState(authState: AuthState): Result<Unit> {
        val oauthUser = OauthUser(authState = authState)
        setAuthState(oauthUser)
        return addAuthenticatedUser.invoke(AddAuthenticatedUser.Params(oauthUser))
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
        oauthUser?.authState?.takeIf { it.needsTokenRefresh }?.let {
            val params = RefreshToken.Params(it, AuthorizationService(context))
            val tokenRefreshResult = refreshToken.invoke(params)
            updateAuthState(tokenRefreshResult)
        }
        return getAccessToken()
    }

    fun getAccessToken() = oauthUser!!.authState.accessToken
}
