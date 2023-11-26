package com.pietervandewalle.androidapp.ui.studylocations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.data.StudyLocationRepository
import com.pietervandewalle.androidapp.data.StudyLocationSampler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class StudyLocationsOverviewViewModel(private val studyLocationRepository: StudyLocationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(StudyLocationsOverviewState(StudyLocationSampler.getAll()))
    val uiState: StateFlow<StudyLocationsOverviewState> = _uiState.asStateFlow()

    private val useApi = true

    var studyLocationsApiState: StudyLocationsApiState by mutableStateOf(StudyLocationsApiState.Loading)
        private set

    var studyLocationsApiRefreshingState: StudyLocationsApiState by mutableStateOf(
        StudyLocationsApiState.Success(
            mutableListOf(),
        ),
    )
        private set

    init {
        if (useApi) {
            getApiStudyLocations()
        } else {
            studyLocationsApiState = StudyLocationsApiState.Success(StudyLocationSampler.getAll())
        }
    }

    private fun getApiStudyLocations(searchterm: String? = null) {
        studyLocationsApiState = StudyLocationsApiState.Loading
        viewModelScope.launch {
            try {
                val listResult = studyLocationRepository.getStudyLocations(searchterm)
                _uiState.update {
                    it.copy(studyLocations = listResult)
                }
                studyLocationsApiState = StudyLocationsApiState.Success(listResult)
            } catch (e: IOException) {
                studyLocationsApiState = StudyLocationsApiState.Error
            }
        }
    }

    fun refresh() {
        // Don't refresh if still in initial load
        if (studyLocationsApiState is StudyLocationsApiState.Loading) {
            return
        }

        studyLocationsApiRefreshingState = StudyLocationsApiState.Loading
        viewModelScope.launch {
            try {
                val listResult = studyLocationRepository.getStudyLocations(
                    _uiState.value.currentSearchterm.ifBlank { null },
                )
                _uiState.update {
                    it.copy(studyLocations = listResult)
                }
                studyLocationsApiRefreshingState = StudyLocationsApiState.Success(listResult)

                // if first load was error and refresh was successful, we want to display the items now
                if (studyLocationsApiState is StudyLocationsApiState.Error) {
                    studyLocationsApiState = StudyLocationsApiState.Success(listResult)
                }
            } catch (e: IOException) {
                studyLocationsApiRefreshingState = StudyLocationsApiState.Error
            }
        }
    }

    fun openSearch() {
        _uiState.update {
            it.copy(isSearchOpen = true)
        }
    }

    fun closeSearch() {
        _uiState.update {
            it.copy(isSearchOpen = false)
        }
    }

    fun updateSearchterm(newSearchterm: String) {
        _uiState.update {
            it.copy(currentSearchterm = newSearchterm)
        }
    }

    fun resetSearch() {
        _uiState.update {
            it.copy(
                currentSearchterm = "",
                isSearchOpen = false,
                areResultsFiltered = false,
                completedSearchterm = "",
            )
        }
        getApiStudyLocations()
    }

    fun search() {
        _uiState.update {
            it.copy(
                isSearchOpen = false,
                areResultsFiltered = true,
                completedSearchterm = it.currentSearchterm,
            )
        }
        getApiStudyLocations(_uiState.value.currentSearchterm)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AndroidApplication)
                val studyLocationRepository = application.container.studyLocationRepository
                StudyLocationsOverviewViewModel(
                    studyLocationRepository = studyLocationRepository,
                )
            }
        }
    }
}
