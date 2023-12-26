package com.pietervandewalle.androidapp.ui.carparks.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.WhileUiSubscribed
import com.pietervandewalle.androidapp.core.Result
import com.pietervandewalle.androidapp.core.asResult
import com.pietervandewalle.androidapp.data.repo.CarParkRepository
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.ui.carparks.detail.CarParkDetailViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel class responsible for managing the state and actions of the car parks overview screen.
 *
 * @property carParkRepository The repository responsible for fetching car parks data.
 */
class CarParksOverviewViewModel(private val carParkRepository: CarParkRepository) : ViewModel() {
    private val carParks: Flow<Result<List<CarPark>>> = carParkRepository.getAll().asResult()
    private val isRefreshing = MutableStateFlow(false)
    private val isError = MutableStateFlow(false)
    private val isMapViewVisible = MutableStateFlow(false)

    val uiState: StateFlow<CarParksOverviewState> = combine(
        carParks,
        isRefreshing,
        isError,
        isMapViewVisible,
    ) { carParksResult, refreshing, errorOccurred, mapViewVisible ->
        val carParks: CarParksUiState = when (carParksResult) {
            is Result.Success -> CarParksUiState.Success(carParksResult.data)
            is Result.Loading -> CarParksUiState.Loading
            is Result.Error -> CarParksUiState.Error
        }

        CarParksOverviewState(
            carParks = carParks,
            isRefreshing = refreshing,
            isError = errorOccurred,
            isMapViewVisible = mapViewVisible,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = CarParksOverviewState(
            CarParksUiState.Loading,
            isRefreshing = false,
            isError = false,
            isMapViewVisible = false,
        ),
    )
    private val exceptionHandler = CoroutineExceptionHandler { context, exception ->
        viewModelScope.launch {
            exception.printStackTrace()
            isError.emit(true)
        }
    }

    /**
     * Initiates a refresh operation to update car parks data.
     */
    fun refresh() {
        viewModelScope.launch(exceptionHandler) {
            with(carParkRepository) {
                val refreshCarParksDeferred = async { refresh() }
                isRefreshing.emit(true)

                try {
                    awaitAll(refreshCarParksDeferred)
                } finally {
                    isRefreshing.emit(false)
                }
            }
        }
    }
    /**
     * Toggles the visibility of the map view.
     */
    fun toggleMapView() {
        isMapViewVisible.value = !isMapViewVisible.value
    }

    /**
     * Clears the error state.
     */
    fun onErrorConsumed() {
        viewModelScope.launch {
            isError.emit(false)
        }
    }

    companion object {
        /**
         * Factory for creating [CarParksOverviewViewModel] instances.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AndroidApplication)
                val carParkRepository = application.container.carParkRepository
                CarParksOverviewViewModel(
                    carParkRepository = carParkRepository,
                )
            }
        }
    }
}
