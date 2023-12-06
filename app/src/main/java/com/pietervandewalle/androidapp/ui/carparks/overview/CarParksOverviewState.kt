package com.pietervandewalle.androidapp.ui.carparks.overview

import com.pietervandewalle.androidapp.model.CarPark

data class CarParksOverviewState(
    val carParks: CarParksUiState,
    val isRefreshing: Boolean,
    val isError: Boolean,
    val isMapViewVisible: Boolean,
)

sealed interface CarParksUiState {
    data class Success(val carParks: List<CarPark>) : CarParksUiState
    object Error : CarParksUiState
    object Loading : CarParksUiState
}
