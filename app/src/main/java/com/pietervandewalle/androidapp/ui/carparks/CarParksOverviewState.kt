package com.pietervandewalle.androidapp.ui.carparks

import com.pietervandewalle.androidapp.model.CarPark

data class CarParksOverviewState(
    val carParks: List<CarPark>,
    val isMapViewVisible: Boolean = false,
)

sealed interface CarParkApiState {
    data class Success(val tasks: List<CarPark>) : CarParkApiState
    object Error : CarParkApiState
    object Loading : CarParkApiState
}
