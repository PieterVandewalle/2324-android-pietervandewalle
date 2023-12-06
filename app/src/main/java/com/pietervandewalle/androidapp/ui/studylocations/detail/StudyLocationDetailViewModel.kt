package com.pietervandewalle.androidapp.ui.studylocations.detail

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
import com.pietervandewalle.androidapp.data.repo.StudyLocationRepository
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyLocationDetailViewModel(private val studyLocationRepository: StudyLocationRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val studyLocationId: Int = savedStateHandle[DestinationsArgs.STUDYLOCATION_ID_ARG]!!
    private val studyLocation: Flow<Result<StudyLocation>> = studyLocationRepository.getById(studyLocationId).asResult()
    private val isError = MutableStateFlow(false)

    val uiState: StateFlow<StudyLocationDetailState> = combine(
        studyLocation,
        isError,
    ) { studyLocationResult, errorOccurred ->
        val studyLocation: StudyLocationUiState = when (studyLocationResult) {
            is Result.Success -> StudyLocationUiState.Success(studyLocationResult.data)
            is Result.Loading -> StudyLocationUiState.Loading
            is Result.Error -> StudyLocationUiState.Error
        }

        StudyLocationDetailState(
            studyLocation,
            errorOccurred,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = StudyLocationDetailState(
            StudyLocationUiState.Loading,
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
                val studyLocationRepository = application.container.studyLocationRepository
                StudyLocationDetailViewModel(
                    studyLocationRepository = studyLocationRepository,
                    savedStateHandle = createSavedStateHandle(),
                )
            }
        }
    }
}
