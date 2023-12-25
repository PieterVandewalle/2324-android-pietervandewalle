package com.pietervandewalle.androidapp.ui.carparks.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.sampler.CarParkSampler
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.ui.carparks.common.components.CarParkStatusCard
import com.pietervandewalle.androidapp.ui.carparks.common.helpers.getRelativeTimeSpanString
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.informationlist.InformationCard
import com.pietervandewalle.androidapp.ui.common.components.informationlist.InformationListItem
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarParkDetailView(modifier: Modifier = Modifier, onNavigateBack: () -> Unit, carParkDetailViewModel: CarParkDetailViewModel = viewModel(factory = CarParkDetailViewModel.Factory)) {
    val uiState by carParkDetailViewModel.uiState.collectAsState()
    val carParkUiState = uiState.carPark

    ErrorSnackbar(isError = uiState.isError, onErrorConsumed = carParkDetailViewModel::onErrorConsumed)

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.car_parking, canNavigateBack = true, onNavigateBack = onNavigateBack) {
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            when (carParkUiState) {
                is CarParkUiState.Loading -> LoadingIndicator()
                is CarParkUiState.Error ->
                    Text(
                        stringResource(id = R.string.loading_failed),
                    )
                is CarParkUiState.Success ->
                    CarParkDetail(
                        carPark = carParkUiState.carPark,
                    )
            }
        }
    }
}

@Composable
fun CarParkDetail(carPark: CarPark, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    InformationCard(
        modifier = modifier,
        headerTitle = carPark.name,
        headerContent = {
            CarParkStatusCard(carPark = carPark)
            Text(
                text = stringResource(
                    R.string.last_update,
                    getRelativeTimeSpanString(carPark.lastUpdate),
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        },
        informationListContent = {
            InformationListItem(
                Icons.Filled.Timeline,
                stringResource(R.string.total_capacity),
                carPark.totalCapacity.toString(),
            )
            InformationListItem(
                Icons.Filled.CheckCircle,
                stringResource(R.string.available_capacity),
                carPark.availableCapacity.toString(),
            )
            InformationListItem(Icons.Filled.Work, stringResource(R.string.operator), carPark.operator)
            InformationListItem(
                Icons.Filled.Co2,
                stringResource(R.string.low_emission_zone),
                if (carPark.isInLEZ) stringResource(R.string.yes) else stringResource(R.string.no),
            )
            InformationListItem(
                Icons.Filled.Payments,
                stringResource(R.string.free_parking),
                if (carPark.isFree) stringResource(R.string.yes) else stringResource(R.string.no),
            )
            carPark.extraInfo?.let {
                InformationListItem(Icons.Filled.Info, stringResource(R.string.extra_info), carPark.extraInfo)
            }
        },
        bottomContent = {
            Button(onClick = {
                val gmmIntentUri = Uri.parse(
                    "geo:${carPark.location.latitude},${carPark.location.longitude}?q=" + Uri.encode(
                        if (!carPark.name.contains("Park")) "Parking ${carPark.name}" else carPark.name,
                    ),
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                context.startActivity(mapIntent)
            }) {
                Text(stringResource(R.string.navigate))
            }
        },
    )
}

@Composable
@Preview(showBackground = true)
private fun CarParkDetailPreview() {
    AndroidAppTheme {
        CarParkDetail(carPark = CarParkSampler.getOneAlmostFull())
    }
}
