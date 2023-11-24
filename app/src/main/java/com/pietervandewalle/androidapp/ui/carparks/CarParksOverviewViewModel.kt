package com.pietervandewalle.androidapp.ui.carparks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.data.CarParkRepository
import com.pietervandewalle.androidapp.data.CarParkSampler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class CarParksOverviewViewModel(private val carParkRepository: CarParkRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CarParksOverviewState(CarParkSampler.getAll()))
    val uiState: StateFlow<CarParksOverviewState> = _uiState.asStateFlow()

    private val useApi = false

    var carParkApiState: CarParkApiState by mutableStateOf(CarParkApiState.Loading)
        private set

    var carParkApiRefreshingState: CarParkApiState by mutableStateOf(
        CarParkApiState.Success(
            mutableListOf(),
        ),
    )
        private set

    init {
        if (useApi) {
            getApiCarParks()
        } else {
            carParkApiState = CarParkApiState.Success(CarParkSampler.getAll())
        }
    }

    private fun getApiCarParks() {
        viewModelScope.launch {
            try {
                val listResult = carParkRepository.getCarParks()
                _uiState.update {
                    it.copy(carParks = listResult)
                }
                carParkApiState = CarParkApiState.Success(listResult)
            } catch (e: IOException) {
                carParkApiState = CarParkApiState.Error
            }
        }
    }

    fun refresh() {
        // Don't refresh if still in initial load
        if (carParkApiState is CarParkApiState.Loading) {
            return
        }

        carParkApiRefreshingState = CarParkApiState.Loading
        viewModelScope.launch {
            try {
                val listResult = carParkRepository.getCarParks()
                _uiState.update {
                    it.copy(carParks = listResult)
                }
                carParkApiRefreshingState = CarParkApiState.Success(listResult)

                // if first load was error and refresh was successful, we want to display the items now
                if (carParkApiState is CarParkApiState.Error) {
                    carParkApiState = CarParkApiState.Success(listResult)
                }
            } catch (e: IOException) {
                carParkApiRefreshingState = CarParkApiState.Error
            }
        }
    }

    fun toggleMapView() {
        _uiState.update {
            it.copy(isMapViewVisible = !it.isMapViewVisible)
        }
    }

    companion object {
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
