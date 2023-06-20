package com.batuhan.management.presentation.projectlist

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.theme.FConsoleTheme
import com.batuhan.theme.Orange

@Composable
fun ProjectListItem(
    project: FirebaseProject,
    onItemClick: (projectId: String) -> Unit,
    onInfoClick: (project: FirebaseProject) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick.invoke(project.projectId ?: "")
            }
            .height(80.dp)
            .padding(8.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = project.displayName ?: "",
            color = Color.Black,
            modifier = Modifier.weight(5f).padding(8.dp)
        )
        IconButton(
            onClick = {
                onInfoClick.invoke(project)
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}

@Preview
@Composable
fun ProjectListPreview() {
    FConsoleTheme {
        ProjectListItem(
            project = FirebaseProject(
                "Project 1",
                "project1",
                null,
                "Project 1",
                null,
                null,
                null,
                "",
                ""
            ),
            onItemClick = {},
            onInfoClick = {}
        )
    }
}
