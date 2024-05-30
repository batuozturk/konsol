package com.batuhan.management.domain.firebase

import com.batuhan.management.data.model.SettingsItem
import com.batuhan.management.data.model.generateSettingsList
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GenerateSettingsList @Inject constructor() {

    suspend operator fun invoke(): List<SettingsItem> {
        return suspendCoroutine {
            it.resume(generateSettingsList())
        }
    }
}
