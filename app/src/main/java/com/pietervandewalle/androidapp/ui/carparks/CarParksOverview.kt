package com.pietervandewalle.androidapp.ui.carparks

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import java.time.ZonedDateTime

@Composable
fun CarParksOverview(modifier: Modifier = Modifier, carParksOverviewViewModel: CarParksOverviewViewModel = viewModel()) {
    val carParksOverviewState by carParksOverviewViewModel.uiState.collectAsState()

    CarParkList(modifier = modifier, carParks = carParksOverviewState.carParks)
}

@Composable
fun CarParkList(modifier: Modifier = Modifier, carParks: List<CarPark>) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState, modifier = modifier) {
        items(carParks) { article ->
            CarParkListItem(carPark = article)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarParkListItem(modifier: Modifier = Modifier, carPark: CarPark) {
    ListItem(
        shadowElevation = 2.dp,
        modifier = modifier
            .padding(5.dp),
        headlineText = {
            Text(carPark.name, style = MaterialTheme.typography.titleMedium)
        },
        supportingText = {
            Column(
                modifier = Modifier.padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    carPark.description,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    "Laatste update: " + DateUtils.getRelativeTimeSpanString(carPark.lastUpdate.atZone(ZonedDateTime.now().zone).toInstant().toEpochMilli()),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        trailingContent = {
            Card(
                colors = CardDefaults.cardColors(containerColor = determineColor(carPark.isOpenNow, isAlmostFull = carPark.isAlmostFull, isFull = carPark.isFull)),
            ) {
                Column(modifier = Modifier.padding(8.dp).width(100.dp)) {
                    if (carPark.isOpenNow) {
                        Text(
                            if (carPark.isFull) {
                                stringResource(R.string.full)
                            } else if (carPark.isAlmostFull) {
                                stringResource(R.string.almost_full)
                            } else {
                                stringResource(
                                    R.string.available_space,
                                )
                            },

                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        if (carPark.availableCapacity != 0) {
                            Text(
                                carPark.availableCapacity.toString() + " plaatsen vrij",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    } else {
                        Text(
                            if (carPark.isTemporaryClosed) "Tijdelijk gesloten" else "Gesloten",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        },
    )
}

@Composable
fun determineColor(isOpen: Boolean, isAlmostFull: Boolean, isFull: Boolean): Color {
    if (!isOpen || isFull) {
        return MaterialTheme.colorScheme.errorContainer
    }

    if (isAlmostFull) {
        return Color.hsl(42f, 0.78f, 0.92f)
    }
    return Color.Green
}

@Preview
@Composable
fun ArticleListPreview() {
    AndroidAppTheme {
        CarParkList(carParks = CarParkSampler.getAll())
    }
}
