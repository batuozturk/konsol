package com.batuhan.management.data.model

data class FirebaseProjectTemplate(
    var isCreatingFromScratch: Boolean = false,
    var createProjectRequest: CreateProjectRequest? = null,
    var analyticsAccountId: String? = null,
    var locationId: String? = null
)
