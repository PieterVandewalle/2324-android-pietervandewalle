package com.pietervandewalle.androidapp.daotests

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pietervandewalle.androidapp.data.database.MyRoomDatabase
import com.pietervandewalle.androidapp.data.database.dao.CarParkDao
import com.pietervandewalle.androidapp.data.database.entity.asDbCarPark
import com.pietervandewalle.androidapp.data.sampler.CarParkSampler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CarParkDaoTest {
    private lateinit var carParkDao: CarParkDao
    private lateinit var roomDb: MyRoomDatabase

    private val carParks = CarParkSampler.getAll()

    private suspend fun addAllCarParksToDb() {
        carParks.forEach {
            carParkDao.insert(it.asDbCarPark())
        }
    }

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        roomDb = Room.inMemoryDatabaseBuilder(context, MyRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        carParkDao = roomDb.carParkDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        roomDb.close()
    }

    @Test
    fun `getAll returns no car parks when dao is empty`() = runBlocking {
        val carParks = carParkDao.getAll().first()
        assertTrue(carParks.isEmpty())
    }

    @Test
    fun `insert and getById returns the same car park`() = runBlocking {
        val testCarPark = carParks.first()
        carParkDao.insert(testCarPark.asDbCarPark())

        val retrievedCarPark = carParkDao.getById(testCarPark.id).first()
        assertEquals(testCarPark.asDbCarPark(), retrievedCarPark)
    }

    @Test
    fun `getAll returns all inserted car parks in correct order`() = runBlocking {
        addAllCarParksToDb()

        val retrievedCarParks = carParkDao.getAll().first()
        val sortedCarParks = carParks.sortedBy { it.name }
        assertEquals(sortedCarParks.map { it.asDbCarPark() }, retrievedCarParks)
    }

    @Test
    fun `insert with existing id replaces existing entry`() = runBlocking {
        val testCarPark = carParks.first()
        carParkDao.insert(testCarPark.asDbCarPark())
        // Modify and insert the same car park again
        val modifiedCarPark = testCarPark.copy(name = "Modified Name")
        carParkDao.insert(modifiedCarPark.asDbCarPark())

        val retrievedCarParks = carParkDao.getAll().first()
        assertTrue(retrievedCarParks.any { it.name == "Modified Name" })
        assertEquals(1, retrievedCarParks.size)
    }

    @Test
    fun `getById returns null for non-existent car park`() = runBlocking {
        val nonExistentId = 999
        val retrievedCarPark = carParkDao.getById(nonExistentId).firstOrNull()
        assertNull(retrievedCarPark)
    }

    @Test
    fun `insert multiple car parks and getAll returns all`() = runBlocking {
        addAllCarParksToDb()

        val retrievedCarParks = carParkDao.getAll().first()
        assertEquals(carParks.size, retrievedCarParks.size)
    }
}
