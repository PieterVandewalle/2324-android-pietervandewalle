package com.pietervandewalle.androidapp.ui.studylocations.overview

import com.pietervandewalle.androidapp.core.Result
import com.pietervandewalle.androidapp.model.StudyLocation

data class StudyLocationsOverviewState(
    val studyLocations: StudyLocationsUiState,
    val isRefreshing: Boolean,
    val isError: Boolean,
    val isSearchOpen: Boolean = false,
    val currentSearchTerm: String = "",
    val areResultsFiltered: Boolean = false,
    val completedSearchTerm: String = "",
)

data class UIState(
    val isRefreshing: Boolean,
    val isError: Boolean,
    val isSearchOpen: Boolean,
    val areResultsFiltered: Boolean,
    val completedSearchTerm: String,
)

data class DataState(
    val studyLocationsResult: Result<List<StudyLocation>>,
    val currentSearchTerm: String,
)

sealed interface StudyLocationsUiState {
    data class Success(val studyLocations: List<StudyLocation>) : StudyLocationsUiState
    object Error : StudyLocationsUiState
    object Loading : StudyLocationsUiState
}
