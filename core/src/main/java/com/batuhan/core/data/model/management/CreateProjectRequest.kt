package com.batuhan.core.data.model.management

import androidx.annotation.Keep

@Keep
data class CreateProjectRequest(
    val projectId: String,
    val name: String?
)
