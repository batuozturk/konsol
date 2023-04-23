package com.batuhan.core.domain

import com.batuhan.core.data.OauthUser
import com.batuhan.core.data.repository.AuthRepository
import javax.inject.Inject

class AddAuthenticatedUser @Inject constructor(private val repository: AuthRepository) {

    data class Params(val user: OauthUser)

    suspend operator fun invoke(params: Params) = repository.addAuthenticatedUser(params.user)
}
