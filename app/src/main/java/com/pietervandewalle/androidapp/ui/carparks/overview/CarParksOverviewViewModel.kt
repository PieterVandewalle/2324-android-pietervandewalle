package com.pietervandewalle.androidapp.ui.carparks.overview

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

    private val useApi = true

    var carParksApiState: CarParksApiState by mutableStateOf(CarParksApiState.Loading)
        private set

    var carParksApiRefreshingState: CarParksApiState by mutableStateOf(
        CarParksApiState.Success(
            mutableListOf(),
        ),
    )
        private set

    init {
        if (useApi) {
            getApiCarParks()
        } else {
            carParksApiState = CarParksApiState.Success(CarParkSampler.getAll())
        }
    }

    private fun getApiCarParks() {
        viewModelScope.launch {
            try {
                val listResult = carParkRepository.getCarParks()
                _uiState.update {
                    it.copy(carParks = listResult)
                }
                carParksApiState = CarParksApiState.Success(listResult)
            } catch (e: IOException) {
                carParksApiState = CarParksApiState.Error
            }
        }
    }

    fun refresh() {
        // Don't refresh if still in initial load
        if (carParksApiState is CarParksApiState.Loading) {
            return
        }

        carParksApiRefreshingState = CarParksApiState.Loading
        viewModelScope.launch {
            try {
                val listResult = carParkRepository.getCarParks()
                _uiState.update {
                    it.copy(carParks = listResult)
                }
                carParksApiRefreshingState = CarParksApiState.Success(listResult)

                // if first load was error and refresh was successful, we want to display the items now
                if (carParksApiState is CarParksApiState.Error) {
                    carParksApiState = CarParksApiState.Success(listResult)
                }
            } catch (e: IOException) {
                carParksApiRefreshingState = CarParksApiState.Error
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
