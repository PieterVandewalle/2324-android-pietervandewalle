package com.pietervandewalle.androidapp.ui.carparks.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.data.CarParkRepository
import com.pietervandewalle.androidapp.data.CarParkSampler
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class CarParkDetailViewModel(private val carParkRepository: CarParkRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(CarParkDetailState(CarParkSampler.getAll().first()))
    val uiState: StateFlow<CarParkDetailState> = _uiState.asStateFlow()

    private val carParkName: String? = savedStateHandle[DestinationsArgs.CARPARK_NAME_ARG]

    var carParkApiState: CarParkApiState by mutableStateOf(CarParkApiState.Loading)
        private set

    init {
        getApiCarPark(carParkName ?: "")
    }

    private fun getApiCarPark(carParkName: String) {
        viewModelScope.launch {
            try {
                val result = carParkRepository.getCarParkByName(carParkName)
                _uiState.update {
                    it.copy(carPark = result)
                }
                carParkApiState = CarParkApiState.Success(result)
            } catch (e: IOException) {
                carParkApiState = CarParkApiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AndroidApplication)
                val carParkRepository = application.container.carParkRepository
                CarParkDetailViewModel(
                    carParkRepository = carParkRepository,
                    savedStateHandle = createSavedStateHandle(),
                )
            }
        }
    }
}
