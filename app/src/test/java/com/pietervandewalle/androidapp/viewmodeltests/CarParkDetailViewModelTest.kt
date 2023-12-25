package com.pietervandewalle.androidapp.viewmodeltests

import androidx.lifecycle.SavedStateHandle
import com.pietervandewalle.androidapp.TestDispatcherRule
import com.pietervandewalle.androidapp.data.repo.CachingCarParkRepository
import com.pietervandewalle.androidapp.data.sampler.CarParkSampler
import com.pietervandewalle.androidapp.ui.carparks.detail.CarParkDetailViewModel
import com.pietervandewalle.androidapp.ui.carparks.detail.CarParkUiState
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs.CARPARK_ID_ARG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CarParkDetailViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: CarParkDetailViewModel

    private val repoMock = mock<CachingCarParkRepository>()

    private val sampleCarPark = CarParkSampler.getOneNotFull()

    @Test
    fun `uiState reflects success and contains carPark on successful load`() = runTest {
        whenever(repoMock.getById(sampleCarPark.id)).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                emit(sampleCarPark)
            },
        )

        viewModel = CarParkDetailViewModel(
            carParkRepository = repoMock,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    CARPARK_ID_ARG to sampleCarPark.id,
                ),
            ),
        )

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        assertTrue(viewModel.uiState.value.carPark is CarParkUiState.Loading)

        advanceTimeBy(1000)
        advanceUntilIdle() // Let the coroutine complete and changes propagate

        assertTrue(viewModel.uiState.value.carPark is CarParkUiState.Success)

        val carParkInState = (viewModel.uiState.value.carPark as CarParkUiState.Success).carPark
        assertEquals(sampleCarPark, carParkInState)
    }

    @Test
    fun `uiState reflects error on network failure during carPark load`() = runTest {
        whenever(repoMock.getById(sampleCarPark.id)).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                throw IOException("Network Error") // Simulate network error
            },
        )

        viewModel = CarParkDetailViewModel(
            carParkRepository = repoMock,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    CARPARK_ID_ARG to sampleCarPark.id,
                ),
            ),
        )

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        assertTrue(viewModel.uiState.value.carPark is CarParkUiState.Loading)

        advanceTimeBy(1001)

        assertTrue(viewModel.uiState.value.carPark is CarParkUiState.Error)
    }
}
