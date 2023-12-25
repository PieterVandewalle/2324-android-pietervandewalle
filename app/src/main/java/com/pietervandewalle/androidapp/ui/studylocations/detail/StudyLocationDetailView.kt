package com.pietervandewalle.androidapp.ui.studylocations.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Beenhere
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.sampler.StudyLocationSampler
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.informationlist.InformationCard
import com.pietervandewalle.androidapp.ui.common.components.informationlist.InformationListItem
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

/**
 * Composable function for displaying the Study Location Detail View.
 *
 * @param modifier The modifier for this composable.
 * @param onNavigateBack The callback to navigate back.
 * @param studyLocationDetailViewModel The ViewModel for study location details.
 */
@Composable
fun StudyLocationDetailView(modifier: Modifier = Modifier, onNavigateBack: () -> Unit, studyLocationDetailViewModel: StudyLocationDetailViewModel = viewModel(factory = StudyLocationDetailViewModel.Factory)) {
    val uiState by studyLocationDetailViewModel.uiState.collectAsState()
    val studyLocationUiState = uiState.studyLocation

    ErrorSnackbar(isError = uiState.isError, onErrorConsumed = studyLocationDetailViewModel::onErrorConsumed)
    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.studylocations, canNavigateBack = true, onNavigateBack = onNavigateBack) {
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            when (studyLocationUiState) {
                is StudyLocationUiState.Loading -> LoadingIndicator()
                is StudyLocationUiState.Error ->
                    Text(
                        stringResource(id = R.string.loading_failed),
                    )
                is StudyLocationUiState.Success ->
                    StudyLocationDetail(studyLocation = studyLocationUiState.studyLocation)
            }
        }
    }
}

/**
 * Composable function for displaying the Study Location Detail.
 *
 * @param modifier The modifier for this composable.
 * @param studyLocation The study location to display.
 */
@Composable
fun StudyLocationDetail(modifier: Modifier = Modifier, studyLocation: StudyLocation) {
    val context = LocalContext.current

    InformationCard(
        modifier = modifier,
        headerTitle = studyLocation.title,
        headerContent = {
            studyLocation.imageUrl?.let {
                SubcomposeAsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.image_container_large)),
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
        },
        informationListContent = {
            InformationListItem(Icons.Filled.Groups, stringResource(R.string.number_of_study_spots), studyLocation.totalCapacity.toString())
            InformationListItem(
                Icons.Filled.Beenhere,
                stringResource(
                    R.string.reserved_study_spots,
                ),
                studyLocation.reservedAmount.toString(),
            )
            InformationListItem(Icons.Filled.LocationOn, stringResource(R.string.address), studyLocation.address)
            studyLocation.reservationTag?.let {
                InformationListItem(
                    Icons.Filled.BookmarkAdded,
                    stringResource(
                        R.string.reservation,
                    ),
                    it,
                )
            }
            studyLocation.availableTag?.let {
                InformationListItem(Icons.Filled.CheckCircle, stringResource(R.string.available), it)
            }
        },
        bottomContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))) {
                OutlinedButton(onClick = {
                    val showInBrowserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(studyLocation.readMoreUrl))
                    context.startActivity(showInBrowserIntent)
                }) {
                    Text(stringResource(R.string.read_more))
                }
                Button(onClick = {
                    val gmmIntentUri = Uri.parse(
                        "geo:${studyLocation.location.latitude},${studyLocation.location.longitude}?q=${
                            Uri.encode(
                                studyLocation.address,
                            )
                        }",
                    )
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    context.startActivity(mapIntent)
                }) {
                    Text(stringResource(R.string.navigate))
                }
            }
        },
    )
}

@Composable
@Preview(showBackground = true)
private fun StudyLocationDetailPreview() {
    AndroidAppTheme {
        StudyLocationDetail(studyLocation = StudyLocationSampler.getAll().first())
    }
}
