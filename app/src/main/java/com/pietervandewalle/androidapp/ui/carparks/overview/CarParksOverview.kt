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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.pietervandewalle.androidapp.ui.common.components.DefaultOverviewListItemCard
import com.pietervandewalle.androidapp.ui.common.components.ErrorLoadingIndicatorWithRetry
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.common.helpers.bitmapDescriptorFromVector
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import com.pietervandewalle.androidapp.ui.theme.success
import com.pietervandewalle.androidapp.ui.theme.warning
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarParksOverview(modifier: Modifier = Modifier, onNavigateToDetail: (Int) -> Unit, carParksOverviewViewModel: CarParksOverviewViewModel = viewModel(factory = CarParksOverviewViewModel.Factory)) {
    val uiState by carParksOverviewViewModel.uiState.collectAsState()
    val carParksUiState = uiState.carParks

    ErrorSnackbar(isError = uiState.isError, onErrorConsumed = carParksOverviewViewModel::onErrorConsumed)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CarParksTopAppBar(onToggleMap = carParksOverviewViewModel::toggleMapView, isMapVisible = uiState.isMapViewVisible, scrollBehavior = scrollBehavior)
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        PullRefreshContainer(
            topAppBarState = scrollBehavior.state,
            isRefreshing = uiState.isRefreshing,
            onRefresh = carParksOverviewViewModel::refresh,
            modifier = Modifier.padding(innerPadding),
        ) {
            when (carParksUiState) {
                is CarParksUiState.Loading -> LoadingIndicator()
                is CarParksUiState.Error -> ErrorLoadingIndicatorWithRetry(onRetry = carParksOverviewViewModel::refresh)
                is CarParksUiState.Success -> {
                    AnimatedTabVisibility(
                        isVisible = !uiState.isMapViewVisible,
                        isLeftTab = true,
                    ) {
                        CarParkList(carParks = carParksUiState.carParks, onNavigateToDetail = { onNavigateToDetail(it.id) })
                    }
                    AnimatedTabVisibility(
                        isVisible = uiState.isMapViewVisible,
                        isLeftTab = false,
                    ) {
                        CarParkMap(carParks = carParksUiState.carParks, modifier = Modifier.zIndex(1f))
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
                CarParkListItem(carPark = carPark)
            }
        }
    }
}

@Composable
fun CarParkList(modifier: Modifier = Modifier, carParks: List<CarPark>, onNavigateToDetail: (CarPark) -> Unit) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState, modifier = modifier.padding(dimensionResource(R.dimen.padding_extra_small))) {
        items(carParks) { carPark ->
            CarParkListItem(
                carPark = carPark,
                modifier = Modifier
                    .clickable { onNavigateToDetail(carPark) },
            )
        }
    }
}

@Composable
fun CarParkListItem(modifier: Modifier = Modifier, carPark: CarPark) {
    DefaultOverviewListItemCard(modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(
                    carPark.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                )
            },
            supportingContent = { CarParkDetails(carPark = carPark) },
            trailingContent = { CarParkStatusCard(carPark = carPark) },
        )
    }
}

@Composable
private fun CarParkDetails(carPark: CarPark) {
    Column(
        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
    ) {
        Text(carPark.description, style = MaterialTheme.typography.bodyMedium)
        // TODO this should be a use case?
        val lastUpdateMillis = carPark.lastUpdate.toInstant(ZoneOffset.UTC).toEpochMilli()
        val currentMillis = Instant.now().toEpochMilli()

        // Format elapsed time using DateUtils.getRelativeTimeSpanString
        val elapsedTime = DateUtils.getRelativeTimeSpanString(
            lastUpdateMillis,
            currentMillis,
            DateUtils.MINUTE_IN_MILLIS,
        ).toString()

        Text(
            stringResource(id = R.string.last_update, elapsedTime),
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
private fun CarParkListItemPreview() {
    AndroidAppTheme {
        CarParkListItem(carPark = CarParkSampler.getOneNotFull())
    }
}
