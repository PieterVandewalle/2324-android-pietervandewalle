package com.pietervandewalle.androidapp.ui.carparks.detail

import com.pietervandewalle.androidapp.model.CarPark

data class CarParkDetailState(
    val carPark: CarParkUiState,
)

sealed interface CarParkUiState {
    data class Success(val carPark: CarPark) : CarParkUiState
    object Error : CarParkUiState
    object Loading : CarParkUiState
}
