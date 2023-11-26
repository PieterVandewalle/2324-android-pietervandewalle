package com.pietervandewalle.androidapp.ui.studylocations.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Beenhere
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.ui.carparks.detail.DetailItem
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar

@Composable
fun StudyLocationDetailView(modifier: Modifier = Modifier, onNavigateBack: () -> Unit, studyLocationDetailViewModel: StudyLocationDetailViewModel = viewModel(factory = StudyLocationDetailViewModel.Factory)) {
    val studyLocationDetailState by studyLocationDetailViewModel.uiState.collectAsState()
    val studyLocationApiState = studyLocationDetailViewModel.studyLocationApiState

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.studylocations, canNavigateBack = true, onNavigateBack = onNavigateBack) {
            }
        },
    ) { innerPadding ->
        when (studyLocationApiState) {
            is StudyLocationApiState.Loading -> Column(modifier = modifier.padding(innerPadding)) { LoadingIndicator() }
            is StudyLocationApiState.Error -> Column(modifier = modifier.padding(innerPadding)) { Text("Couldn't load...") }
            is StudyLocationApiState.Success ->
                StudyLocationDetail(modifier = modifier.padding(innerPadding), studyLocation = studyLocationDetailState.studyLocation)
        }
    }
}

@Composable
fun StudyLocationDetail(modifier: Modifier = Modifier, studyLocation: StudyLocation) {
    val context = LocalContext.current

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // TODO use dimension resource?
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            StudyLocationHeader(studyLocation = studyLocation)

            Spacer(modifier = Modifier.height(16.dp))

            StudyLocationInformation(studyLocation = studyLocation)

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                OutlinedButton(onClick = {
                    val showInBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(studyLocation.readMoreUrl))
                    context.startActivity(showInBrowserIntent)
                }) {
                    Text("Lees meer")
                }
                Button(onClick = {
                    val gmmIntentUri = Uri.parse("geo:${studyLocation.location.latitude},${studyLocation.location.longitude}?q=${Uri.encode(studyLocation.address)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    context.startActivity(mapIntent)
                }) {
                    Text("Navigeer naar locatie")
                }
            }
        }
    }
}

@Composable
fun StudyLocationHeader(studyLocation: StudyLocation) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = studyLocation.title,
            style = MaterialTheme.typography.titleLarge,
        )
        studyLocation.imageUrl?.let {
            SubcomposeAsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                },
            )
        }
        Text(
            text = studyLocation.label,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun StudyLocationInformation(studyLocation: StudyLocation) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = "Informatie",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        DetailItem(Icons.Filled.Groups, "Aantal plaatsen", studyLocation.totalCapacity.toString())
        DetailItem(Icons.Filled.Beenhere, "Gereserveerde plaatsen", studyLocation.reservedAmount.toString())
        DetailItem(Icons.Filled.LocationOn, "Adres", studyLocation.address)
        studyLocation.reservationTag?.let {
            DetailItem(Icons.Filled.BookmarkAdded, "Reservatie", it)
        }
        studyLocation.availableTag?.let {
            DetailItem(Icons.Filled.CheckCircle, "Beschikbaar", it)
        }
    }
}
