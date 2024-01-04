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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Study Location Detail screen.
 *
 * @param studyLocationRepository The repository for study location data.
 * @param savedStateHandle The SavedStateHandle to access saved state data.
 */
class StudyLocationDetailViewModel(private val studyLocationRepository: StudyLocationRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val studyLocationId: Int = savedStateHandle[DestinationsArgs.STUDYLOCATION_ID_ARG]!!
    private val studyLocation: Flow<Result<StudyLocation>> = studyLocationRepository.getById(studyLocationId).asResult()

    /**
     * Represents the state flow of the UI state for the Study Location Detail screen.
     */
    val uiState: StateFlow<StudyLocationDetailState> = studyLocation.map { studyLocationResult ->
        val studyLocation: StudyLocationUiState = when (studyLocationResult) {
            is Result.Success -> StudyLocationUiState.Success(studyLocationResult.data)
            is Result.Loading -> StudyLocationUiState.Loading
            is Result.Error -> StudyLocationUiState.Error
        }

        StudyLocationDetailState(
            studyLocation,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = StudyLocationDetailState(
            StudyLocationUiState.Loading,
        ),
    )

    companion object {
        /**
         * Factory for creating instances of [StudyLocationDetailViewModel].
         */
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
