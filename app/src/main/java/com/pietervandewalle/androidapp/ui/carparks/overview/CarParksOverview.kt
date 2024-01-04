package com.pietervandewalle.androidapp.ui.carparks.overview

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.pietervandewalle.androidapp.ui.carparks.common.helpers.getRelativeTimeSpanString
import com.pietervandewalle.androidapp.ui.carparks.overview.components.CarParksTopAppBar
import com.pietervandewalle.androidapp.ui.common.components.DefaultOverviewListItemCard
import com.pietervandewalle.androidapp.ui.common.components.ErrorLoadingIndicatorWithRetry
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.common.components.ScrollToTopButton
import com.pietervandewalle.androidapp.ui.common.helpers.bitmapDescriptorFromVector
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import com.pietervandewalle.androidapp.ui.theme.success
import com.pietervandewalle.androidapp.ui.theme.warning

/**
 * Composable function for displaying a car parks overview.
 *
 * @param modifier The [Modifier] to apply to this composable.
 * @param onNavigateToDetail A callback that will be invoked when a car park item is clicked.
 * @param carParksOverviewViewModel The ViewModel responsible for managing car parks data.
 */
@Composable
fun CarParksOverview(modifier: Modifier = Modifier, onNavigateToDetail: (Int) -> Unit, carParksOverviewViewModel: CarParksOverviewViewModel = viewModel(factory = CarParksOverviewViewModel.Factory)) {
    val uiState by carParksOverviewViewModel.uiState.collectAsState()
    val carParksUiState = uiState.carParks

    ErrorSnackbar(isError = uiState.isError, onErrorConsumed = carParksOverviewViewModel::onErrorConsumed)

    Scaffold(
        topBar = {
            CarParksTopAppBar(onToggleMap = carParksOverviewViewModel::toggleMapView, isMapVisible = uiState.isMapViewVisible)
        },
        modifier = modifier,
    ) { innerPadding ->
        PullRefreshContainer(
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

/**
 * Composable function for handling animated tab visibility.
 *
 * @param isVisible A boolean value indicating whether the content should be visible.
 * @param isLeftTab A boolean value indicating whether it's the left tab.
 * @param content The content to be displayed within the animated visibility.
 */
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

/**
 * Composable function for displaying a map of car parks.
 *
 * @param modifier The [Modifier] to apply to this composable.
 * @param carParks A list of car parks to display on the map.
 */
@Composable
fun CarParkMap(modifier: Modifier = Modifier, carParks: List<CarPark>) {
    val ghentCoordinates = LatLng(51.0500, 3.733333)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ghentCoordinates, 12f)
    }
    val context = LocalContext.current
    GoogleMap(
        modifier = modifier.fillMaxSize().testTag("carParkMap"),
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

/**
 * Composable function for displaying a list of car parks.
 *
 * @param modifier The [Modifier] to apply to this composable.
 * @param carParks A list of car parks to display in the list.
 * @param onNavigateToDetail A lambda that will be invoked when a car park item is clicked.
 */
@Composable
fun CarParkList(modifier: Modifier = Modifier, carParks: List<CarPark>, onNavigateToDetail: (CarPark) -> Unit) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState, modifier = modifier.padding(dimensionResource(R.dimen.padding_extra_small)).testTag("carParkList")) {
        items(carParks) { carPark ->
            CarParkListItem(
                carPark = carPark,
                modifier = Modifier
                    .clickable { onNavigateToDetail(carPark) },
            )
        }
    }
    ScrollToTopButton(lazyListState = lazyListState)
}

/**
 * Composable function for displaying a single car park item in the list.
 *
 * @param modifier The [Modifier] to apply to this composable.
 * @param carPark The car park to display.
 */
@Composable
fun CarParkListItem(modifier: Modifier = Modifier, carPark: CarPark) {
    DefaultOverviewListItemCard(modifier = modifier.testTag("carParkListItem")) {
        ListItem(
            headlineContent = {
                Text(
                    carPark.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    modifier = Modifier.testTag("carParkListItemTitle"),
                )
            },
            supportingContent = { CarParkDetails(carPark = carPark) },
            trailingContent = { CarParkStatusCard(carPark = carPark) },
        )
    }
}

/**
 * Composable function for displaying car park details.
 *
 * @param carPark The car park to display details for.
 */
@Composable
private fun CarParkDetails(carPark: CarPark) {
    Column(
        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
    ) {
        Text(carPark.description, style = MaterialTheme.typography.bodyMedium)

        val elapsedTime = remember { getRelativeTimeSpanString(carPark.lastUpdate) }

        Text(
            stringResource(id = R.string.last_update, elapsedTime),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

/**
 * Determines the color for the location icon based on the car park's status.
 *
 * @param carPark The car park for which to determine the icon color.
 * @return The color for the location icon.
 */
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
