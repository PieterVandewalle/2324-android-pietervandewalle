package com.pietervandewalle.androidapp.ui.studylocations.overview

import android.util.Log
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
            Log.i("Query: ", query)
            studyLocationRepository.getAllBySearchTerm(query).asResult()
        }
    }

    val uiState: StateFlow<StudyLocationsOverviewState> = combine(
        // Combine UI related states
        combine(isRefreshing, isError, isSearchOpen, areResultsFiltered, completedSearchTerm) { refreshing, error, searchOpen, resultsFiltered, completedSearch ->
            UIState(refreshing, error, searchOpen, resultsFiltered, completedSearch)
        },
        // Combine data related states
        combine(studyLocationsFlow, currentSearchTerm) { studyLocations, currentSearch ->
            DataState(studyLocations, currentSearch)
        },
    ) { ui, data ->
        // Map to final UI state
        StudyLocationsOverviewState(
            studyLocations = when (data.studyLocationsResult) {
                is Result.Success -> StudyLocationsUiState.Success(data.studyLocationsResult.data)
                is Result.Loading -> StudyLocationsUiState.Loading
                is Result.Error -> StudyLocationsUiState.Error
            },
            isRefreshing = ui.isRefreshing,
            isError = ui.isError,
            isSearchOpen = ui.isSearchOpen,
            currentSearchTerm = data.currentSearchTerm,
            areResultsFiltered = ui.areResultsFiltered,
            completedSearchTerm = ui.completedSearchTerm,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = StudyLocationsOverviewState(
            studyLocations = StudyLocationsUiState.Loading,
            isRefreshing = false,
            isError = false,
            isSearchOpen = false,
            currentSearchTerm = "",
            areResultsFiltered = false,
            completedSearchTerm = "",
        ),
    )

    private val exceptionHandler = CoroutineExceptionHandler { context, exception ->
        viewModelScope.launch {
            isError.emit(true)
        }
    }

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

    // Should be called after snackbar message is shown
    fun onErrorConsumed() {
        viewModelScope.launch {
            isError.emit(false)
        }
    }

    fun openSearch() {
        isSearchOpen.value = true
    }

    fun closeSearch() {
        isSearchOpen.value = false
    }

    fun updateSearchTerm(newSearchTerm: String) {
        currentSearchTerm.value = newSearchTerm
    }

    fun resetSearch() {
        currentSearchTerm.value = ""
        isSearchOpen.value = false
        areResultsFiltered.value = false
        completedSearchTerm.value = ""
    }

    fun search() {
        completedSearchTerm.value = currentSearchTerm.value
        areResultsFiltered.value = true
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
