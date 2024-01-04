package com.pietervandewalle.androidapp.viewmodeltests

import com.pietervandewalle.androidapp.TestDispatcherRule
import com.pietervandewalle.androidapp.data.repo.CachingStudyLocationRepository
import com.pietervandewalle.androidapp.data.sampler.StudyLocationSampler
import com.pietervandewalle.androidapp.ui.studylocations.overview.StudyLocationsOverviewViewModel
import com.pietervandewalle.androidapp.ui.studylocations.overview.StudyLocationsUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class StudyLocationsOverviewViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: StudyLocationsOverviewViewModel

    private val repoMock = mock<CachingStudyLocationRepository>()

    @Test
    fun `uiState reflects success and contains studyLocations on successful studyLocations load`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                emit(StudyLocationSampler.getAll())
            },
        )

        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        assertTrue(viewModel.uiState.value.studyLocations is StudyLocationsUiState.Loading)

        advanceTimeBy(1000)
        advanceUntilIdle() // Let the coroutine complete and changes propagate

        assertTrue(viewModel.uiState.value.studyLocations is StudyLocationsUiState.Success)

        val studyLocationsInState = (viewModel.uiState.value.studyLocations as StudyLocationsUiState.Success).studyLocations
        assertEquals(StudyLocationSampler.getAll(), studyLocationsInState)
    }

    @Test
    fun `uiState reflects error on network failure during studyLocations load`() = runTest {
        // Simulate network error
        whenever(repoMock.getAll()).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                throw IOException("Network Error") // Simulate network error
            },
        )

        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        assertTrue(viewModel.uiState.value.studyLocations is StudyLocationsUiState.Loading)

        advanceTimeBy(1001)

        assertTrue(viewModel.uiState.value.studyLocations is StudyLocationsUiState.Error)
    }

    @Test
    fun `onErrorConsumed resets error state after refresh error`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                emit(StudyLocationSampler.getAll())
            },
        )
        // Set up repoMock to throw an exception on refresh
        whenever(repoMock.refresh()).thenAnswer { throw IOException("Network Error") }

        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        // Call refresh which should throw an exception and set isError to true
        viewModel.refresh()

        advanceUntilIdle()

        // The isError state should be true after the refresh error
        val errorStateAfterRefresh = viewModel.uiState.value.isError
        assertTrue(errorStateAfterRefresh)

        // Reset error
        viewModel.onErrorConsumed()

        advanceUntilIdle()

        // The error state should be reset to false
        val errorStateAfterReset = viewModel.uiState.value.isError
        assertFalse(errorStateAfterReset)
    }

    @Test
    fun `refresh resets error state after refresh error`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                emit(StudyLocationSampler.getAll())
            },
        )
        // Set up repoMock to throw an exception on refresh
        whenever(repoMock.refresh()).thenAnswer { throw IOException("Network Error") }

        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        // Call refresh which should throw an exception and set isError to true
        viewModel.refresh()

        advanceUntilIdle()

        // The isError state should be true after the refresh error
        val errorStateAfterRefresh = viewModel.uiState.value.isError
        assertTrue(errorStateAfterRefresh)

        // Reset error
        viewModel.onErrorConsumed()

        advanceUntilIdle()

        // The error state should be reset to false
        val errorStateAfterReset = viewModel.uiState.value.isError
        assertFalse(errorStateAfterReset)
    }

    @Test
    fun `refresh updates studyLocations in uiState`() = runTest {
        val studyLocationsFlow = MutableStateFlow(listOf(StudyLocationSampler.getAll().first()))

        whenever(repoMock.getAll()).thenReturn(
            studyLocationsFlow,
        )

        whenever(repoMock.refresh()).thenAnswer {
            // Simulate the behavior of updating studyLocations
            runBlocking {
                studyLocationsFlow.emit(StudyLocationSampler.getAll())
            }
        }

        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        val initialStudyLocationsInState = (viewModel.uiState.value.studyLocations as StudyLocationsUiState.Success).studyLocations

        assertEquals(1, initialStudyLocationsInState.size)

        viewModel.refresh()

        advanceUntilIdle()

        val updatedStudyLocations = (viewModel.uiState.value.studyLocations as StudyLocationsUiState.Success).studyLocations
        assertEquals(StudyLocationSampler.getAll().size, updatedStudyLocations.size)
    }

    @Test
    fun `uiState reflects filtered studyLocations on successful search`() = runTest {
        val searchTerm = "SchoonMeEr" // there exists one studyLocations with title 'Bib Schoonmeersen'
        val filteredStudyLocations = StudyLocationSampler.getAll().filter { it.title.contains(searchTerm, ignoreCase = true) || it.address.contains(searchTerm, ignoreCase = true) }
            .sortedBy { it.title }

        whenever(repoMock.getAll()).thenReturn(
            flow {
                emit(StudyLocationSampler.getAll())
            },
        )

        whenever(repoMock.getAllBySearchTerm(searchTerm)).thenReturn(
            flowOf(filteredStudyLocations),
        )

        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)

        val collectedState = mutableListOf<StudyLocationsUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect { state ->
                collectedState.add(state.studyLocations)
            }
        }

        viewModel.updateSearchTerm(searchTerm)
        viewModel.search()

        advanceUntilIdle()
        // Assert that the state is a success and contains filtered locations
        val initialStudyLocations = (collectedState[1] as StudyLocationsUiState.Success).studyLocations
        assertEquals(StudyLocationSampler.getAll().size, initialStudyLocations.size)

        assertTrue(collectedState.last() is StudyLocationsUiState.Success)
        val studyLocationsInState = (collectedState.last() as StudyLocationsUiState.Success).studyLocations
        assertEquals(filteredStudyLocations, studyLocationsInState)
    }

    @Test
    fun `resetSearch resets search related states`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                emit(StudyLocationSampler.getAll())
            },
        )

        whenever(repoMock.getAllBySearchTerm(anyString())).thenReturn(
            flow {
                emit(listOf())
            },
        )

        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        viewModel.updateSearchTerm("Search Term")
        viewModel.openSearch()
        viewModel.search() // This sets areResultsFiltered to true
        advanceUntilIdle()

        viewModel.resetSearch()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.currentSearchTerm.isEmpty())
        assertFalse(viewModel.uiState.value.isSearchOpen)
        assertFalse(viewModel.uiState.value.areResultsFiltered)
    }

    @Test
    fun `currentSearchTerm updates correctly on new search term`() = runTest {
        whenever(repoMock.getAll()).thenReturn(flow { emit(emptyList()) })

        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }
        val newSearchTerm = "New Search Term"
        viewModel.updateSearchTerm(newSearchTerm)

        assertEquals(newSearchTerm, viewModel.uiState.value.currentSearchTerm)
    }

    @Test
    fun `isSearchOpen reflects correct state when search is opened and closed`() = runTest {
        whenever(repoMock.getAll()).thenReturn(flow { emit(emptyList()) })
        viewModel = StudyLocationsOverviewViewModel(studyLocationRepository = repoMock)
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        viewModel.openSearch()
        assertTrue(viewModel.uiState.value.isSearchOpen)

        viewModel.closeSearch()
        assertFalse(viewModel.uiState.value.isSearchOpen)
    }
}
