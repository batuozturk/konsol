package com.batuhan.core.domain

import com.batuhan.core.data.OauthUser
import com.batuhan.core.data.repository.AuthRepository
import javax.inject.Inject

class DeleteAuthenticatedUser @Inject constructor(private val repository: AuthRepository) {

    data class Params(val oauthUser: OauthUser)

    suspend operator fun invoke(params: Params) = repository.deleteAuthenticatedUser(params.oauthUser)
}
