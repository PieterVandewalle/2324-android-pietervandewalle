package com.pietervandewalle.androidapp.ui.studylocations.detail

import com.pietervandewalle.androidapp.model.StudyLocation

data class StudyLocationDetailState(
    val studyLocation: StudyLocation,
)

sealed interface StudyLocationApiState {
    data class Success(val studyLocation: StudyLocation) : StudyLocationApiState
    object Error : StudyLocationApiState
    object Loading : StudyLocationApiState
}
