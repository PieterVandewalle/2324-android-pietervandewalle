package com.pietervandewalle.androidapp.ui.carparks

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.CarParkSampler
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.isAlmostFull
import com.pietervandewalle.androidapp.model.isFull
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import com.pietervandewalle.androidapp.ui.theme.successContainer
import com.pietervandewalle.androidapp.ui.theme.warningContainer
import java.time.Instant
import java.time.ZoneOffset

@Composable
fun CarParksOverview(modifier: Modifier = Modifier, carParksOverviewViewModel: CarParksOverviewViewModel = viewModel(factory = CarParksOverviewViewModel.Factory)) {
    val carParksOverviewState by carParksOverviewViewModel.uiState.collectAsState()

    val carParkApiState = carParksOverviewViewModel.carParkApiState
    val carParkApiRefreshingState = carParksOverviewViewModel.carParkApiRefreshingState
    val isRefreshing = carParkApiRefreshingState is CarParkApiState.Loading

    PullRefreshContainer(isRefreshing = isRefreshing, onRefresh = carParksOverviewViewModel::refresh, modifier = modifier) {
        when (carParkApiState) {
            is CarParkApiState.Loading -> LoadingIndicator()
            is CarParkApiState.Error -> Text("Couldn't load...")
            is CarParkApiState.Success -> CarParkList(
                carParks = carParksOverviewState.carParks,
            )
        }
    }
}

@Composable
fun CarParkList(modifier: Modifier = Modifier, carParks: List<CarPark>) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState, modifier = modifier) {
        items(carParks) { carPark ->
            CarParkListItem(carPark = carPark)
        }
    }
}

@Composable
fun CarParkListItem(modifier: Modifier = Modifier, carPark: CarPark) {
    ListItem(
        shadowElevation = 2.dp,
        modifier = modifier.padding(5.dp),
        headlineContent = { Text(carPark.name, style = MaterialTheme.typography.titleMedium, maxLines = 2) },
        supportingContent = { CarParkDetails(carPark = carPark) },
        trailingContent = { CarParkStatus(cardColor = determineStatusColor(carPark), carPark = carPark) },
    )
}

@Composable
private fun CarParkDetails(carPark: CarPark) {
    Column(
        modifier = Modifier.padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(carPark.description, style = MaterialTheme.typography.bodyMedium)

        val lastUpdateMillis = carPark.lastUpdate.toInstant(ZoneOffset.UTC).toEpochMilli()
        val currentMillis = Instant.now().toEpochMilli()

        // Format elapsed time using DateUtils.getRelativeTimeSpanString
        val elapsedTime = DateUtils.getRelativeTimeSpanString(
            lastUpdateMillis,
            currentMillis,
            DateUtils.MINUTE_IN_MILLIS,
        ).toString()

        Text(
            "Laatste update: $elapsedTime",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun CarParkStatus(cardColor: Color, carPark: CarPark) {
    Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(4.dp)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(107.dp),
        ) {
            if (carPark.isOpenNow) {
                CarParkOpenStatus(carPark = carPark)
            } else {
                CarParkClosedStatus(carPark = carPark)
            }
        }
    }
}

@Composable
private fun CarParkOpenStatus(carPark: CarPark) {
    Text(
        when {
            carPark.isFull -> stringResource(R.string.full)
            carPark.isAlmostFull -> stringResource(R.string.almost_full)
            else -> stringResource(R.string.available_space)
        },
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )

    if (!carPark.isFull) {
        Text(
            "nog ${carPark.availableCapacity} plaatsen",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun CarParkClosedStatus(carPark: CarPark) {
    Text(
        if (carPark.isTemporaryClosed) "Tijdelijk gesloten" else "Gesloten",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun determineStatusColor(carPark: CarPark): Color {
    if (!carPark.isOpenNow || carPark.isFull) {
        return MaterialTheme.colorScheme.errorContainer
    }

    if (carPark.isAlmostFull) {
        return MaterialTheme.colorScheme.warningContainer
    }
    return MaterialTheme.colorScheme.successContainer
}

@Preview
@Composable
fun ArticleListPreview() {
    AndroidAppTheme {
        CarParkList(carParks = CarParkSampler.getAll())
    }
}
