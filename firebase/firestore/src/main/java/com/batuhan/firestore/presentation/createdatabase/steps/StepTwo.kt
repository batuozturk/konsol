package com.batuhan.firestore.presentation.createdatabase.steps

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.batuhan.firestore.R
import com.batuhan.firestore.data.model.FirestoreLocation
import com.batuhan.firestore.presentation.createdatabase.CreateDatabaseErrorState
import com.batuhan.firestore.presentation.createdatabase.StepTwoState
import com.batuhan.theme.Orange

@Composable
fun StepTwo(
    stepTwoState: StepTwoState,
    locations: LazyPagingItems<FirestoreLocation>,
    setLocationId: (String) -> Unit,
    setErrorState: (CreateDatabaseErrorState) -> Unit
) {
    when (locations.loadState.refresh) {
        is LoadState.Error -> {
            setErrorState.invoke(CreateDatabaseErrorState.FIRESTORE_LOCATIONS)
        }
        else -> {
        }
    }

    when (locations.loadState.append) {
        is LoadState.Error -> {
            setErrorState.invoke(CreateDatabaseErrorState.FIRESTORE_LOCATIONS)
        }
        else -> {
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(stringResource(id = R.string.select_location), modifier = Modifier.fillMaxWidth().padding(8.dp))
        }
        items(locations.itemCount) { index ->
            SelectLocationItem(
                firestoreLocation = locations[index],
                isSelectedLocation = locations[index]?.locationId == stepTwoState.locationId,
                setLocationId = setLocationId
            )
        }
    }
}

@Composable
fun SelectLocationItem(
    firestoreLocation: FirestoreLocation?,
    isSelectedLocation: Boolean,
    setLocationId: (String) -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp)
            .fillMaxWidth()
            .height(60.dp)
            .border(2.dp, Orange, RoundedCornerShape(10.dp))
            .clickable {
                setLocationId.invoke(firestoreLocation?.locationId ?: return@clickable)
            }.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(firestoreLocation?.locationId ?: stringResource(id = R.string.undefined))
        if (isSelectedLocation) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = Orange
            )
        }
    }
}
