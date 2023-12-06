package com.pietervandewalle.androidapp.ui.carparks.overview

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.sampler.CarParkSampler
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.isAlmostFull
import com.pietervandewalle.androidapp.model.isFull
import com.pietervandewalle.androidapp.ui.carparks.common.components.CarParkStatusCard
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.common.helpers.bitmapDescriptorFromVector
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import com.pietervandewalle.androidapp.ui.theme.success
import com.pietervandewalle.androidapp.ui.theme.warning
import java.time.Instant
import java.time.ZoneOffset

@Composable
fun CarParksOverview(modifier: Modifier = Modifier, onNavigateToDetail: (String) -> Unit, carParksOverviewViewModel: CarParksOverviewViewModel = viewModel(factory = CarParksOverviewViewModel.Factory)) {
    val carParksOverviewState by carParksOverviewViewModel.uiState.collectAsState()

    val carParksApiState = carParksOverviewViewModel.carParksApiState
    val carParksApiRefreshingState = carParksOverviewViewModel.carParksApiRefreshingState
    val isRefreshing = carParksApiRefreshingState is CarParksApiState.Loading

    Scaffold(
        topBar = {
            CarParksTopAppBar(onToggleMap = carParksOverviewViewModel::toggleMapView, isMapVisible = carParksOverviewState.isMapViewVisible)
        },
    ) { innerPadding ->
        PullRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = carParksOverviewViewModel::refresh,
            modifier = modifier.padding(innerPadding),
        ) {
            when (carParksApiState) {
                is CarParksApiState.Loading -> LoadingIndicator()
                is CarParksApiState.Error -> Text("Couldn't load...")
                is CarParksApiState.Success -> {
                    AnimatedTabVisibility(
                        isVisible = !carParksOverviewState.isMapViewVisible,
                        isLeftTab = true,
                    ) {
                        CarParkList(carParks = carParksOverviewState.carParks, onNavigateToDetail = { onNavigateToDetail(it.name) })
                    }
                    AnimatedTabVisibility(
                        isVisible = carParksOverviewState.isMapViewVisible,
                        isLeftTab = false,
                    ) {
                        CarParkMap(carParks = carParksOverviewState.carParks, modifier = Modifier.zIndex(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedTabVisibility(isVisible: Boolean, isLeftTab: Boolean, content: @Composable () -> Unit) {
    val animationDurationMillis = 500
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth * if (isLeftTab) -1 else 1 },
            animationSpec = tween(durationMillis = animationDurationMillis, easing = LinearOutSlowInEasing),
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth * if (isLeftTab) -1 else 1 },
            animationSpec = tween(durationMillis = animationDurationMillis, easing = LinearOutSlowInEasing),
        ),
    ) {
        content()
    }
}

@Composable
fun CarParkMap(modifier: Modifier = Modifier, carParks: List<CarPark>) {
    val ghentCoordinates = LatLng(51.0500, 3.733333)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ghentCoordinates, 12f)
    }
    val context = LocalContext.current
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        carParks.forEach { carPark ->
            MarkerInfoWindowContent(
                state = MarkerState(position = LatLng(carPark.location.latitude, carPark.location.longitude)),
                icon = bitmapDescriptorFromVector(
                    context,
                    R.drawable.parking_location_marker,
                    determineLocationIconColor(carPark = carPark),
                ),

            ) {
                CarParkListItem(carPark = carPark, isBackgroundVisible = false)
            }
        }
    }
}

@Composable
fun CarParkList(modifier: Modifier = Modifier, carParks: List<CarPark>, onNavigateToDetail: (CarPark) -> Unit) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState, modifier = modifier) {
        items(carParks) { carPark ->
            CarParkListItem(
                carPark = carPark,
                modifier = modifier.clickable { onNavigateToDetail(carPark) }.padding(
                    dimensionResource(R.dimen.padding_extra_small),
                ),
            )
        }
    }
}

@Composable
fun CarParkListItem(modifier: Modifier = Modifier, carPark: CarPark, isBackgroundVisible: Boolean = true) {
    ListItem(
        shadowElevation = 2.dp,
        colors = ListItemDefaults.colors(containerColor = if (isBackgroundVisible) MaterialTheme.colorScheme.background else Color.Transparent),
        modifier = modifier,
        headlineContent = { Text(carPark.name, style = MaterialTheme.typography.titleMedium, maxLines = 2) },
        supportingContent = { CarParkDetails(carPark = carPark) },
        trailingContent = { CarParkStatusCard(carPark = carPark) },
    )
}

@Composable
private fun CarParkDetails(carPark: CarPark) {
    Column(
        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_medium)),
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
fun determineLocationIconColor(carPark: CarPark): Color {
    if (!carPark.isOpenNow || carPark.isFull) {
        return MaterialTheme.colorScheme.error
    }

    if (carPark.isAlmostFull) {
        return MaterialTheme.colorScheme.warning
    }
    return MaterialTheme.colorScheme.success
}

@Preview
@Composable
fun ArticleListItemPreview() {
    AndroidAppTheme {
        CarParkListItem(carPark = CarParkSampler.getAll().first())
    }
}
