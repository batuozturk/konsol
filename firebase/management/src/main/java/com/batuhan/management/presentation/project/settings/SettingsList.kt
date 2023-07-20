package com.batuhan.management.presentation.project.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.batuhan.management.data.model.SettingsItem
import com.batuhan.theme.Orange

@Composable
fun SettingsList(modifier: Modifier, actions: List<SettingsItem>, onClick: (SettingsItem) -> Unit) {
    LazyColumn(
        modifier = modifier
    ) {
        items(actions.size) {
            SettingsListItem(settingsItem = actions[it], onClick = onClick)
        }
    }
}

@Composable
fun SettingsListItem(settingsItem: SettingsItem, onClick: (settingsItem: SettingsItem) -> Unit) {
    Row(
        modifier = Modifier.padding(10.dp).fillMaxWidth().height(height = 60.dp)
            .clickable {
                onClick.invoke(settingsItem)
            }
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = settingsItem.icon,
            tint = Orange,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = stringResource(id = settingsItem.titleResId)
        )
    }
}
