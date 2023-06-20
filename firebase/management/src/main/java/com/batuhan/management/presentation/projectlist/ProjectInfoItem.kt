package com.batuhan.management.presentation.projectlist

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.batuhan.theme.Orange

@Composable
fun ProjectInfoItem(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = title, color = Color.Black)
        Text(text = value, color = Color.Black)
    }
}
