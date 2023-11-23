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

    var carParkApiState: CarParkApiState by mutableStateOf(CarParkApiState.Loading)
        private set

    init {
        getApiCarParks()
    }

    private fun getApiCarParks() {
        viewModelScope.launch {
            try {
                // use the repository
                // val tasksRepository = ApiTasksRepository() //repo is now injected
                val listResult = carParkRepository.getCarParks()
                _uiState.update {
                    it.copy(carParks = listResult)
                }
                carParkApiState = CarParkApiState.Success(listResult)
            } catch (e: IOException) {
                // show a toast? save a log on firebase? ...
                // set the error state
                carParkApiState = CarParkApiState.Error
            }
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
