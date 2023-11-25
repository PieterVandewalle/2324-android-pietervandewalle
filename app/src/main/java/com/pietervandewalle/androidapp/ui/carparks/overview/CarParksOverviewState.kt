package com.pietervandewalle.androidapp.ui.carparks.overview

import com.pietervandewalle.androidapp.model.CarPark

data class CarParksOverviewState(
    val carParks: List<CarPark>,
    val isMapViewVisible: Boolean = false,
)

sealed interface CarParksApiState {
    data class Success(val carParks: List<CarPark>) : CarParksApiState
    object Error : CarParksApiState
    object Loading : CarParksApiState
}
