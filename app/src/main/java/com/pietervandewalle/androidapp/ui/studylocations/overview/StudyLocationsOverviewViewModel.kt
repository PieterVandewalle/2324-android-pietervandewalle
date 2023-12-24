package com.pietervandewalle.androidapp.ui.studylocations.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.WhileUiSubscribed
import com.pietervandewalle.androidapp.core.Result
import com.pietervandewalle.androidapp.core.asResult
import com.pietervandewalle.androidapp.data.repo.StudyLocationRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Study Locations Overview screen.
 *
 * @param studyLocationRepository The repository for study locations data.
 */
class StudyLocationsOverviewViewModel(private val studyLocationRepository: StudyLocationRepository) : ViewModel() {
    private val isRefreshing = MutableStateFlow(false)
    private val isError = MutableStateFlow(false)
    private val isSearchOpen = MutableStateFlow(false)
    private val currentSearchTerm = MutableStateFlow("")
    private val completedSearchTerm = MutableStateFlow("")
    private val areResultsFiltered = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val studyLocationsFlow = completedSearchTerm.flatMapLatest { query ->
        if (query.isEmpty()) {
            studyLocationRepository.getAll().asResult()
        } else {
            studyLocationRepository.getAllBySearchTerm(query).asResult()
        }
    }

    /**
     * Represents the state flow of the UI state for the Study Location Overview screen.
     */
    val uiState: StateFlow<StudyLocationsOverviewState> = combine(
        // Combine UI related states
        combine(isSearchOpen, areResultsFiltered, currentSearchTerm) { searchOpen, resultsFiltered, currentSearch ->
            UIState(searchOpen, resultsFiltered, currentSearch)
        },
        // Combine data related states
        combine(studyLocationsFlow, completedSearchTerm, isRefreshing, isError) { studyLocations, completedSearch, refreshing, error ->
            DataState(studyLocations, completedSearch, refreshing, error)
        },
    ) { ui, data ->
        // Map to final UI state
        StudyLocationsOverviewState(
            studyLocations = when (data.studyLocationsResult) {
                is Result.Success -> StudyLocationsUiState.Success(data.studyLocationsResult.data)
                is Result.Loading -> StudyLocationsUiState.Loading
                is Result.Error -> StudyLocationsUiState.Error
            },
            isRefreshing = data.isRefreshing,
            isError = data.isError,
            completedSearchTerm = data.completedSearchTerm,
            isSearchOpen = ui.isSearchOpen,
            currentSearchTerm = ui.currentSearchTerm,
            areResultsFiltered = ui.areResultsFiltered,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = StudyLocationsOverviewState(
            studyLocations = StudyLocationsUiState.Loading,
            isRefreshing = false,
            isError = false,
            completedSearchTerm = "",
            isSearchOpen = false,
            currentSearchTerm = "",
            areResultsFiltered = false,
        ),
    )

    private val exceptionHandler = CoroutineExceptionHandler { context, exception ->
        viewModelScope.launch {
            isError.emit(true)
            exception.printStackTrace()
        }
    }

    /**
     * Function to trigger a data refresh.
     */
    fun refresh() {
        viewModelScope.launch(exceptionHandler) {
            with(studyLocationRepository) {
                val refreshStudyLocationsDeferred = async { refresh() }
                isRefreshing.emit(true)
                try {
                    awaitAll(refreshStudyLocationsDeferred)
                } finally {
                    isRefreshing.emit(false)
                }
            }
        }
    }

    /**
     * Function to be called after the error message is shown to clear the error state.
     */
    fun onErrorConsumed() {
        viewModelScope.launch {
            isError.emit(false)
        }
    }

    /**
     * Function to open the search bar.
     */
    fun openSearch() {
        isSearchOpen.value = true
    }

    /**
     * Function to close the search bar.
     */
    fun closeSearch() {
        isSearchOpen.value = false
    }

    /**
     * Function to update the current search term.
     *
     * @param newSearchTerm The new search term to set.
     */
    fun updateSearchTerm(newSearchTerm: String) {
        currentSearchTerm.value = newSearchTerm
    }

    /**
     * Function to reset the search and clear filters.
     */
    fun resetSearch() {
        currentSearchTerm.value = ""
        isSearchOpen.value = false
        areResultsFiltered.value = false
        completedSearchTerm.value = ""
    }

    /**
     * Function to perform the search operation.
     */
    fun search() {
        completedSearchTerm.value = currentSearchTerm.value
        areResultsFiltered.value = true
    }

    companion object {
        /**
         * Factory for creating instances of [StudyLocationsOverviewViewModel].
         */
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
