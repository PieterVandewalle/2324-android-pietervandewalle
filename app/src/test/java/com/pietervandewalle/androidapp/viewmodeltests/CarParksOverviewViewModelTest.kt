package com.pietervandewalle.androidapp.viewmodeltests

import com.pietervandewalle.androidapp.TestDispatcherRule
import com.pietervandewalle.androidapp.data.repo.CachingCarParkRepository
import com.pietervandewalle.androidapp.data.sampler.CarParkSampler
import com.pietervandewalle.androidapp.ui.carparks.overview.CarParksOverviewViewModel
import com.pietervandewalle.androidapp.ui.carparks.overview.CarParksUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CarParksOverviewViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: CarParksOverviewViewModel

    private val repoMock = mock<CachingCarParkRepository>()

    @Test
    fun `uiState reflects success and contains carParks on successful carParks load`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                emit(CarParkSampler.getAll())
            },
        )

        viewModel = CarParksOverviewViewModel(carParkRepository = repoMock)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        assertTrue(viewModel.uiState.value.carParks is CarParksUiState.Loading)

        advanceTimeBy(1000)
        advanceUntilIdle() // Let the coroutine complete and changes propagate

        assertTrue(viewModel.uiState.value.carParks is CarParksUiState.Success)

        val carParksInState = (viewModel.uiState.value.carParks as CarParksUiState.Success).carParks
        assertEquals(CarParkSampler.getAll(), carParksInState)
    }

    @Test
    fun `uiState reflects error on network failure during carParks load`() = runTest {
        // Simulate network error
        whenever(repoMock.getAll()).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                throw IOException("Network Error") // Simulate network error
            },
        )

        viewModel = CarParksOverviewViewModel(carParkRepository = repoMock)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        assertTrue(viewModel.uiState.value.carParks is CarParksUiState.Loading)

        advanceTimeBy(1001)

        assertTrue(viewModel.uiState.value.carParks is CarParksUiState.Error)
    }

    @Test
    fun `toggleMapView toggles map view visibility state`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            emptyFlow(),
        )
        viewModel = CarParksOverviewViewModel(carParkRepository = repoMock)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // Initial state of isMapViewVisible
        val initialMapViewState = viewModel.uiState.value.isMapViewVisible

        //  Toggle the map view
        viewModel.toggleMapView()

        // Assert: The state should be opposite of initial state
        assertNotEquals(initialMapViewState, viewModel.uiState.value.isMapViewVisible)
    }

    @Test
    fun `onErrorConsumed resets error state after refresh error`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                emit(CarParkSampler.getAll())
            },
        )
        // Set up repoMock to throw an exception on refresh
        whenever(repoMock.refresh()).thenAnswer { throw IOException("Network Error") }

        viewModel = CarParksOverviewViewModel(carParkRepository = repoMock)

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
                emit(CarParkSampler.getAll())
            },
        )
        // Set up repoMock to throw an exception on refresh
        whenever(repoMock.refresh()).thenAnswer { throw IOException("Network Error") }

        viewModel = CarParksOverviewViewModel(carParkRepository = repoMock)

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
    fun `refresh updates carParks in uiState`() = runTest {
        val carParksFlow = MutableStateFlow(listOf(CarParkSampler.getOneNotFull()))

        whenever(repoMock.getAll()).thenReturn(
            carParksFlow,
        )

        whenever(repoMock.refresh()).thenAnswer {
            // Simulate the behavior of updating car parks
            runBlocking {
                carParksFlow.emit(CarParkSampler.getAll())
            }
        }

        viewModel = CarParksOverviewViewModel(carParkRepository = repoMock)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        val initialCarParksInState = (viewModel.uiState.value.carParks as CarParksUiState.Success).carParks

        assertEquals(1, initialCarParksInState.size)

        viewModel.refresh()

        advanceUntilIdle()

        val updatedCarParks = (viewModel.uiState.value.carParks as CarParksUiState.Success).carParks
        assertEquals(CarParkSampler.getAll().size, updatedCarParks.size)
    }
}
