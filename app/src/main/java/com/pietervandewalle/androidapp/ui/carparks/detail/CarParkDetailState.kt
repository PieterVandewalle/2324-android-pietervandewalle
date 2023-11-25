package com.pietervandewalle.androidapp.ui.carparks.detail

import com.pietervandewalle.androidapp.model.CarPark

data class CarParkDetailState(
    val carPark: CarPark,
)

sealed interface CarParkApiState {
    data class Success(val carPark: CarPark) : CarParkApiState
    object Error : CarParkApiState
    object Loading : CarParkApiState
}
