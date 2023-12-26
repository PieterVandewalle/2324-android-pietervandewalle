package com.pietervandewalle.androidapp.ui.carparks.detail

import com.pietervandewalle.androidapp.model.CarPark


/**
 * Represents the state of a car park detail screen.
 *
 * @param carPark The state of the car park to display.
 */
data class CarParkDetailState(
    val carPark: CarParkUiState,
)

/**
 * Represents the UI state of a car park.
 */
sealed interface CarParkUiState {
    /**
     * Represents a successful state with car park data.
     *
     * @param carPark The car park data to display.
     */
    data class Success(val carPark: CarPark) : CarParkUiState


    /**
     * Represents an error state where an error occurred while fetching car park data.
     */
    object Error : CarParkUiState

    /**
     * Represents a loading state while fetching car park data.
     */
    object Loading : CarParkUiState
}
