package com.pietervandewalle.androidapp.modeltests

import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.GPSCoordinates
import com.pietervandewalle.androidapp.model.isAlmostFull
import com.pietervandewalle.androidapp.model.isFull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class CarParkTest {
    private val sampleCarPark = CarPark(
        id = 1,
        name = "Sample Car Park",
        lastUpdate = LocalDateTime.now(),
        totalCapacity = 100,
        availableCapacity = 20,
        description = "Sample description",
        extraInfo = "Sample extra info",
        isOpenNow = true,
        isTemporaryClosed = false,
        isFree = false,
        isInLEZ = false,
        operator = "Sample Operator",
        location = GPSCoordinates(0.0, 0.0),
    )

    @Test
    fun `CarPark isAlmostFull should be true when available capacity is 10 or less`() {
        val carPark = sampleCarPark.copy(availableCapacity = 10)
        assertTrue(carPark.isAlmostFull)
    }

    @Test
    fun `CarPark isAlmostFull should be false when available capacity is more than 10`() {
        val carPark = sampleCarPark.copy(availableCapacity = 11)
        assertFalse(carPark.isAlmostFull)
    }

    @Test
    fun `CarPark isFull should be true when available capacity is 0`() {
        val carPark = sampleCarPark.copy(availableCapacity = 0)
        assertTrue(carPark.isFull)
    }

    @Test
    fun `CarPark isFull should be false when available capacity is more than 0`() {
        val carPark = sampleCarPark.copy(availableCapacity = 1)
        assertFalse(carPark.isFull)
    }
}
