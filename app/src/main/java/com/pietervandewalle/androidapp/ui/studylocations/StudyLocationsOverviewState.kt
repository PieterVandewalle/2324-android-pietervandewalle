package com.pietervandewalle.androidapp.ui.studylocations

import com.pietervandewalle.androidapp.model.StudyLocation

data class StudyLocationsOverviewState(
    val studyLocations: List<StudyLocation>,
)

sealed interface StudyLocationsApiState {
    data class Success(val carParks: List<StudyLocation>) : StudyLocationsApiState
    object Error : StudyLocationsApiState
    object Loading : StudyLocationsApiState
}
