package com.pietervandewalle.androidapp.ui.carparks.overview

import com.pietervandewalle.androidapp.model.CarPark

/**
 * Represents the state of the car parks overview screen.
 *
 * @property carParks The current state of car parks.
 * @property isRefreshing A boolean indicating whether the data is being refreshed.
 * @property isError A boolean indicating whether there is an error in loading data.
 * @property isMapViewVisible A boolean indicating whether the map view is currently visible.
 */
data class CarParksOverviewState(
    val carParks: CarParksUiState,
    val isRefreshing: Boolean,
    val isError: Boolean,
    val isMapViewVisible: Boolean,
)

/**
 * Sealed interface representing different states for car parks data.
 */
sealed interface CarParksUiState {
    /**
     * Represents a successful state with a list of car parks.
     *
     * @property carParks The list of car parks.
     */
    data class Success(val carParks: List<CarPark>) : CarParksUiState

    /**
     * Represents an error state.
     */
    object Error : CarParksUiState

    /**
     * Represents a loading state.
     */
    object Loading : CarParksUiState
}
