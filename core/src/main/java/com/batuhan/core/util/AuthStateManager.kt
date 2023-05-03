package com.batuhan.core.util

import com.batuhan.core.data.OauthUser
import com.batuhan.core.domain.AddAuthenticatedUser
import com.batuhan.core.domain.DeleteAuthenticatedUser
import com.batuhan.core.domain.GetAuthenticatedUser
import net.openid.appauth.AuthState
import javax.inject.Inject

class AuthStateManager @Inject constructor(
    private val addAuthenticatedUser: AddAuthenticatedUser,
    private val getAuthenticatedUser: GetAuthenticatedUser,
    private val deleteAuthenticatedUser: DeleteAuthenticatedUser
) {

    suspend fun getAuthState(): Result<OauthUser> {
        return getAuthenticatedUser.invoke()
    }

    suspend fun deleteAuthState(oauthUser: OauthUser) =
        deleteAuthenticatedUser.invoke(DeleteAuthenticatedUser.Params(oauthUser))

    suspend fun addAuthState(authState: AuthState): Result<Unit> {
        val oauthUser = OauthUser(authState = authState)
        return addAuthenticatedUser.invoke(AddAuthenticatedUser.Params(oauthUser))
    }
}
