package com.pietervandewalle.androidapp.ui.studylocations.detail

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
import com.pietervandewalle.androidapp.data.StudyLocationRepository
import com.pietervandewalle.androidapp.data.StudyLocationSampler
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class StudyLocationDetailViewModel(private val studyLocationRepository: StudyLocationRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(
        StudyLocationDetailState(StudyLocationSampler.getAll().first()),
    )
    val uiState: StateFlow<StudyLocationDetailState> = _uiState.asStateFlow()

    private val studyLocationId: Int = savedStateHandle[DestinationsArgs.STUDYLOCATION_ID_ARG]!!
    var studyLocationApiState: StudyLocationApiState by mutableStateOf(StudyLocationApiState.Loading)
        private set

    init {
        getApiStudyLocation(studyLocationId)
    }

    private fun getApiStudyLocation(id: Int) {
        studyLocationApiState = StudyLocationApiState.Loading
        viewModelScope.launch {
            try {
                val result = studyLocationRepository.getStudyLocationById(id)
                _uiState.update {
                    it.copy(studyLocation = result)
                }
                studyLocationApiState = StudyLocationApiState.Success(result)
            } catch (e: IOException) {
                studyLocationApiState = StudyLocationApiState.Error
            }
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
