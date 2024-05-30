package com.batuhan.management.domain.firebase

import com.batuhan.management.data.model.FeatureItem
import com.batuhan.management.data.model.generateFeatureList
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GenerateFeatureList @Inject constructor() {

    suspend operator fun invoke(): List<FeatureItem> {
        return suspendCoroutine {
            it.resume(generateFeatureList())
        }
    }
}
