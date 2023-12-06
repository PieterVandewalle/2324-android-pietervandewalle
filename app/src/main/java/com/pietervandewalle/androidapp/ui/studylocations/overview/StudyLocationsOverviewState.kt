package com.pietervandewalle.androidapp.ui.studylocations.overview

import com.pietervandewalle.androidapp.model.StudyLocation

data class StudyLocationsOverviewState(
    val studyLocations: List<StudyLocation>,
    val isSearchOpen: Boolean = false,
    val currentSearchterm: String = "",
    val areResultsFiltered: Boolean = false,
    val completedSearchterm: String = "",
)

sealed interface StudyLocationsApiState {
    data class Success(val studyLocations: List<StudyLocation>) : StudyLocationsApiState
    object Error : StudyLocationsApiState
    object Loading : StudyLocationsApiState
}