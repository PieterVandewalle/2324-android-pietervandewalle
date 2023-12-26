package com.pietervandewalle.androidapp.ui.carparks.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.WhileUiSubscribed
import com.pietervandewalle.androidapp.core.Result
import com.pietervandewalle.androidapp.core.asResult
import com.pietervandewalle.androidapp.data.repo.CarParkRepository
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for displaying details of a car park.
 *
 * @param carParkRepository The repository responsible for fetching car park data.
 * @param savedStateHandle The saved state handle to access and store UI-related data.
 */
class CarParkDetailViewModel(private val carParkRepository: CarParkRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val carParkId: Int = savedStateHandle[DestinationsArgs.CARPARK_ID_ARG]!!
    private val carPark: Flow<Result<CarPark>> = carParkRepository.getById(carParkId).asResult()

    val uiState: StateFlow<CarParkDetailState> = carPark.map { carParkResult ->
        val carPark: CarParkUiState = when (carParkResult) {
            is Result.Success -> CarParkUiState.Success(carParkResult.data)
            is Result.Loading -> CarParkUiState.Loading
            is Result.Error -> CarParkUiState.Error
        }

        CarParkDetailState(
            carPark,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = CarParkDetailState(
            CarParkUiState.Loading,
        ),
    )

    companion object {
        /**
         * Factory for creating [CarParkDetailViewModel] instances.
         */
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
