package com.pietervandewalle.androidapp.ui.studylocations.detail

import com.pietervandewalle.androidapp.model.StudyLocation

/**
 * Represents the state for the Study Location Detail screen.
 *
 * @property studyLocation The state of the study location details.
 */
data class StudyLocationDetailState(
    val studyLocation: StudyLocationUiState,
)

/**
 * Sealed interface representing the various states for study location details.
 */
sealed interface StudyLocationUiState {
    /**
     * Represents a successful state with study location details.
     *
     * @property studyLocation The study location data.
     */
    data class Success(val studyLocation: StudyLocation) : StudyLocationUiState

    /**
     * Represents an error state for study location details.
     */
    object Error : StudyLocationUiState

    /**
     * Represents a loading state while fetching study location details.
     */
    object Loading : StudyLocationUiState
}
