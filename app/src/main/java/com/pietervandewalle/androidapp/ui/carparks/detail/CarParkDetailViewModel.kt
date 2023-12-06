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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarParkDetailViewModel(private val carParkRepository: CarParkRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val carParkId: Int = savedStateHandle[DestinationsArgs.CARPARK_ID_ARG]!!
    private val carPark: Flow<Result<CarPark>> = carParkRepository.getById(carParkId).asResult()
    private val isError = MutableStateFlow(false)

    val uiState: StateFlow<CarParkDetailState> = combine(
        carPark,
        isError,
    ) { carParkResult, errorOccurred ->
        val carPark: CarParkUiState = when (carParkResult) {
            is Result.Success -> CarParkUiState.Success(carParkResult.data)
            is Result.Loading -> CarParkUiState.Loading
            is Result.Error -> CarParkUiState.Error
        }

        CarParkDetailState(
            carPark,
            errorOccurred,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = CarParkDetailState(
            CarParkUiState.Loading,
            isError = false,
        ),
    )

    fun onErrorConsumed() {
        viewModelScope.launch {
            isError.emit(false)
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
