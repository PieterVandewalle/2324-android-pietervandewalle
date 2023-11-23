package com.pietervandewalle.androidapp.ui.carparks

import androidx.lifecycle.ViewModel
import com.pietervandewalle.androidapp.data.CarParkSampler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CarParksOverviewViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CarParksOverviewState(CarParkSampler.getAll()))
    val uiState: StateFlow<CarParksOverviewState> = _uiState.asStateFlow()
}
