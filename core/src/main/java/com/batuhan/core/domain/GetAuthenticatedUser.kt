package com.batuhan.core.domain

import com.batuhan.core.data.OauthUser
import com.batuhan.core.data.repository.AuthRepository
import javax.inject.Inject

class GetAuthenticatedUser @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(): OauthUser = repository.getAuthenticatedUser()
}
