package com.pietervandewalle.androidapp.ui.studylocations.detail

import com.pietervandewalle.androidapp.model.StudyLocation

data class StudyLocationDetailState(
    val studyLocation: StudyLocationUiState,
    val isError: Boolean,
)

sealed interface StudyLocationUiState {
    data class Success(val studyLocation: StudyLocation) : StudyLocationUiState
    object Error : StudyLocationUiState
    object Loading : StudyLocationUiState
}
