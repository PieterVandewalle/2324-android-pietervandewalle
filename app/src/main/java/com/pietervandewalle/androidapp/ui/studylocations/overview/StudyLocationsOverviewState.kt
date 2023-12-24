package com.pietervandewalle.androidapp.ui.studylocations.overview

import com.pietervandewalle.androidapp.core.Result
import com.pietervandewalle.androidapp.model.StudyLocation

/**
 * Represents the state for the Study Locations Overview screen.
 *
 * @property studyLocations The state of study locations.
 * @property isRefreshing True if refreshing, false otherwise.
 * @property isError True if there is an error, false otherwise.
 * @property completedSearchTerm The completed search term.
 * @property isSearchOpen True if the search bar is open, false otherwise.
 * @property currentSearchTerm The current search term.
 * @property areResultsFiltered True if results are filtered, false otherwise.
 */
data class StudyLocationsOverviewState(
    val studyLocations: StudyLocationsUiState,
    val isRefreshing: Boolean,
    val isError: Boolean,
    val completedSearchTerm: String = "",
    val isSearchOpen: Boolean = false,
    val currentSearchTerm: String = "",
    val areResultsFiltered: Boolean = false,
)

/**
 * Represents the UI state for the Study Locations Overview screen.
 *
 * @property isSearchOpen True if the search bar is open, false otherwise.
 * @property areResultsFiltered True if results are filtered, false otherwise.
 * @property currentSearchTerm The current search term.
 */
data class UIState(
    val isSearchOpen: Boolean,
    val areResultsFiltered: Boolean,
    val currentSearchTerm: String,
)

/**
 * Represents the data state for the Study Locations Overview screen.
 *
 * @property studyLocationsResult The result of fetching study locations.
 * @property completedSearchTerm The completed search term.
 * @property isRefreshing True if refreshing, false otherwise.
 * @property isError True if there is an error, false otherwise.
 */
data class DataState(
    val studyLocationsResult: Result<List<StudyLocation>>,
    val completedSearchTerm: String,
    val isRefreshing: Boolean,
    val isError: Boolean,

)

/**
 * Sealed interface representing the various states for study locations.
 */
sealed interface StudyLocationsUiState {
    /**
     * Represents a successful state with a list of study locations.
     *
     * @property studyLocations The list of study locations.
     */
    data class Success(val studyLocations: List<StudyLocation>) : StudyLocationsUiState

    /**
     * Represents an error state for study locations.
     */
    object Error : StudyLocationsUiState

    /**
     * Represents a loading state while fetching study locations.
     */
    object Loading : StudyLocationsUiState
}
