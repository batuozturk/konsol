package com.batuhan.management.data.model

import androidx.annotation.Keep

@Keep
data class CreateProjectRequest(
    val projectId: String,
    val name: String?
)
