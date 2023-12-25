package com.pietervandewalle.androidapp.daotests

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pietervandewalle.androidapp.data.database.MyRoomDatabase
import com.pietervandewalle.androidapp.data.database.dao.StudyLocationDao
import com.pietervandewalle.androidapp.data.database.entity.asDbStudyLocation
import com.pietervandewalle.androidapp.data.sampler.StudyLocationSampler
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
class StudyLocationDaoTest {
    private lateinit var studyLocationDao: StudyLocationDao
    private lateinit var roomDb: MyRoomDatabase

    private val studyLocations = StudyLocationSampler.getAll()

    private suspend fun addAllStudyLocationsToDb() {
        studyLocations.forEach {
            studyLocationDao.insert(it.asDbStudyLocation())
        }
    }

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        roomDb = Room.inMemoryDatabaseBuilder(context, MyRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        studyLocationDao = roomDb.studyLocationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        roomDb.close()
    }

    @Test
    fun `getAll returns no study locations when dao is empty`() = runBlocking {
        val studyLocations = studyLocationDao.getAll().first()
        assertTrue(studyLocations.isEmpty())
    }

    @Test
    fun `insert and getById returns the same study location`() = runBlocking {
        val testStudyLocation = studyLocations.first()
        studyLocationDao.insert(testStudyLocation.asDbStudyLocation())

        val retrievedStudyLocation = studyLocationDao.getById(testStudyLocation.id).first()
        assertEquals(testStudyLocation.asDbStudyLocation(), retrievedStudyLocation)
    }

    @Test
    fun `getAll returns all inserted study locations in correct order`() = runBlocking {
        addAllStudyLocationsToDb()

        val retrievedStudyLocations = studyLocationDao.getAll().first()
        val sortedStudyLocations = studyLocations.sortedBy { it.title }
        assertEquals(sortedStudyLocations.map { it.asDbStudyLocation() }, retrievedStudyLocations)
    }

    @Test
    fun `insert with existing id replaces existing entry`() = runBlocking {
        val testStudyLocation = studyLocations.first()
        studyLocationDao.insert(testStudyLocation.asDbStudyLocation())
        // Modify and insert the same study location again
        val modifiedStudyLocation = testStudyLocation.copy(title = "Modified Title")
        studyLocationDao.insert(modifiedStudyLocation.asDbStudyLocation())

        val retrievedStudyLocations = studyLocationDao.getAll().first()
        assertTrue(retrievedStudyLocations.any { it.title == "Modified Title" })
        assertEquals(1, retrievedStudyLocations.size)
    }

    @Test
    fun `getById returns null for non-existent study location`() = runBlocking {
        val nonExistentId = 999
        val retrievedStudyLocation = studyLocationDao.getById(nonExistentId).firstOrNull()
        assertNull(retrievedStudyLocation)
    }

    @Test
    fun `getAllBySearchTerm returns correct study locations`() = runBlocking {
        addAllStudyLocationsToDb()

        val searchTerm = "SchooNmEe" // StudyLocation Schoonmeersen should match with this searchTerm
        val expectedStudyLocations = studyLocations.filter {
            it.title.contains(searchTerm, ignoreCase = true) ||
                it.address.contains(searchTerm, ignoreCase = true)
        }

        val retrievedStudyLocations = studyLocationDao.getAllBySearchTerm(searchTerm).first()
        assertEquals(expectedStudyLocations.map { it.asDbStudyLocation() }.sortedBy { it.title }, retrievedStudyLocations)
    }

    @Test
    fun `insert multiple study locations and getAll returns all`() = runBlocking {
        addAllStudyLocationsToDb()

        val retrievedStudyLocations = studyLocationDao.getAll().first()
        assertEquals(studyLocations.size, retrievedStudyLocations.size)
    }
}
