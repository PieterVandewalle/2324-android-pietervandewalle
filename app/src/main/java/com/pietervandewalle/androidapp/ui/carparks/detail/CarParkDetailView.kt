package com.pietervandewalle.androidapp.ui.carparks.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.CarParkSampler
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.ui.carparks.common.components.CarParkStatusCard
import com.pietervandewalle.androidapp.ui.carparks.common.helpers.getRelativeTimeSpanString
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@Composable
fun CarParkDetailView(modifier: Modifier = Modifier, onNavigateBack: () -> Unit, carParkDetailViewModel: CarParkDetailViewModel = viewModel(factory = CarParkDetailViewModel.Factory)) {
    val carParkDetailState by carParkDetailViewModel.uiState.collectAsState()
    val carParkApiState = carParkDetailViewModel.carParkApiState

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.car_parking, canNavigateBack = true, onNavigateBack = onNavigateBack) {
            }
        },
    ) { innerPadding ->
        when (carParkApiState) {
            is CarParkApiState.Loading -> Column(modifier = modifier.padding(innerPadding)) { LoadingIndicator() }
            is CarParkApiState.Error -> Column(modifier = modifier.padding(innerPadding)) { Text("Couldn't load...") }
            is CarParkApiState.Success ->
                CarParkDetail(carPark = carParkDetailState.carPark, modifier = modifier.padding(innerPadding))
        }
    }
}

@Composable
fun CarParkDetail(carPark: CarPark, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // TODO use dimension resource?
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            DetailHeader(carPark = carPark)

            Spacer(modifier = Modifier.height(16.dp))

            DetailInformation(carPark)
            Button(onClick = {
                val gmmIntentUri = Uri.parse("geo:${carPark.location.latitude},${carPark.location.longitude}?q=" + Uri.encode(if (!carPark.name.contains("Park")) "Parking ${carPark.name}" else carPark.name))
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                context.startActivity(mapIntent)
            }) {
                Text("Navigeer naar deze parking")
            }
        }
    }
}

@Composable
fun DetailHeader(carPark: CarPark) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = carPark.name,
            style = MaterialTheme.typography.titleLarge,
        )
        CarParkStatusCard(carPark = carPark)
        Text(
            text = "Laatste update: ${getRelativeTimeSpanString(carPark.lastUpdate)}",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
fun DetailInformation(carPark: CarPark) {
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
        DetailItem(Icons.Filled.Timeline, "Totale capaciteit", carPark.totalCapacity.toString())
        DetailItem(Icons.Filled.CheckCircle, "Beschikbare capaciteit", carPark.availableCapacity.toString())
        DetailItem(Icons.Filled.Work, "Beheerder", carPark.operator)
        DetailItem(Icons.Filled.Co2, "Lage-emissiezone", if (carPark.isInLEZ) "Ja" else "Nee")
        DetailItem(Icons.Filled.Payments, "Gratis parkeren", if (carPark.isFree) "Ja" else "Nee")
        carPark.extraInfo?.let {
            DetailItem(Icons.Filled.Info, "Extra info", carPark.extraInfo)
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CarParkDetailPreview() {
    AndroidAppTheme {
        CarParkDetail(carPark = CarParkSampler.getAll()[1])
    }
}
