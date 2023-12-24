package com.pietervandewalle.androidapp.ui.studylocations.overview

import com.pietervandewalle.androidapp.core.Result
import com.pietervandewalle.androidapp.model.StudyLocation

data class StudyLocationsOverviewState(
    val studyLocations: StudyLocationsUiState,
    val isRefreshing: Boolean,
    val isError: Boolean,
    val completedSearchTerm: String = "",
    val isSearchOpen: Boolean = false,
    val currentSearchTerm: String = "",
    val areResultsFiltered: Boolean = false,
)

data class UIState(
    val isSearchOpen: Boolean,
    val areResultsFiltered: Boolean,
    val currentSearchTerm: String,
)

data class DataState(
    val studyLocationsResult: Result<List<StudyLocation>>,
    val completedSearchTerm: String,
    val isRefreshing: Boolean,
    val isError: Boolean,

)

sealed interface StudyLocationsUiState {
    data class Success(val studyLocations: List<StudyLocation>) : StudyLocationsUiState
    object Error : StudyLocationsUiState
    object Loading : StudyLocationsUiState
}
