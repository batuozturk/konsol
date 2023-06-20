package com.batuhan.management.presentation.projectlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batuhan.core.data.model.FirebaseProject
import com.batuhan.management.R
import com.batuhan.theme.FConsoleTheme
import com.batuhan.theme.Orange

@Composable
fun ProjectInfoBottomSheet(project: FirebaseProject?, onDismiss: () -> Unit) {
    val attributesList = project?.getAttributesList() ?: listOf()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.project_info_title),
                color = Color.Black,
                fontSize = 20.sp,
                modifier = Modifier.weight(8f)
            )
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .weight(1f),
                onClick = {
                    onDismiss.invoke()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Orange
                )
            }
        }
        LazyColumn {
            items(attributesList.size) {
                ProjectInfoItem(
                    title = stringResource(id = attributesList[it].first),
                    value = attributesList[it].second
                )
            }
        }
    }
}

private fun FirebaseProject.getAttributesList(): List<Pair<Int, String>> {
    val attributeList = mutableListOf<Pair<Int, String>>()
    attributeList.add(Pair(R.string.project_id, projectId ?: "undefined"))
    attributeList.add(Pair(R.string.project_name, name ?: "undefined"))
    attributeList.add(Pair(R.string.project_display_name, displayName ?: "undefined"))
    attributeList.add(Pair(R.string.project_number, projectNumber?.toString() ?: "undefined"))
    attributeList.add(Pair(R.string.project_state, state?.name ?: "undefined"))
    attributeList.add(Pair(R.string.project_hosting_site, resources?.hostingSite ?: "undefined"))
    attributeList.add(Pair(R.string.project_location_id, resources?.locationId ?: "undefined"))
    attributeList.add(
        Pair(
            R.string.project_storage_bucket,
            resources?.storageBucket ?: "undefined"
        )
    )
    attributeList.add(
        Pair(
            R.string.project_rtdb_instance,
            resources?.realtimeDatabaseInstance ?: "undefined"
        )
    )
    return attributeList
}

@Preview
@Composable
fun ProjectInfoBottomSheetPreview() {
    FConsoleTheme {
        ProjectInfoBottomSheet(project = null, {})
    }
}
