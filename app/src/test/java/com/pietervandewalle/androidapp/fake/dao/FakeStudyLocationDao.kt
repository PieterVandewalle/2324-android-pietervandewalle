package com.pietervandewalle.androidapp.fake.dao

import com.pietervandewalle.androidapp.data.database.dao.StudyLocationDao
import com.pietervandewalle.androidapp.data.database.entity.DbStudyLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeStudyLocationDao(initialStudyLocations: List<DbStudyLocation>? = emptyList()) :
    StudyLocationDao {

    private var _studyLocations: MutableMap<Int, DbStudyLocation>? = null

    var studyLocations: List<DbStudyLocation>?
        get() = _studyLocations?.values?.toList()
        set(newStudyLocations) {
            _studyLocations = newStudyLocations?.associateBy { it.id }?.toMutableMap()
        }

    init {
        studyLocations = initialStudyLocations
    }

    override suspend fun insert(studyLocation: DbStudyLocation) {
        studyLocations = studyLocations?.plus(studyLocation)
    }

    override fun getAll(): Flow<List<DbStudyLocation>> = flow {
        emit(studyLocations?.sortedBy { it.title } ?: emptyList())
    }

    override fun getAllBySearchTerm(searchTerm: String): Flow<List<DbStudyLocation>> = flow {
        emit(
            studyLocations?.filter { it.title.contains(searchTerm, ignoreCase = true) || it.address.contains(searchTerm, ignoreCase = true) }
                ?.sortedBy { it.title } ?: emptyList(),
        )
    }

    override fun getById(id: Int): Flow<DbStudyLocation> = flow {
        emit(_studyLocations?.values?.firstOrNull { it.id == id } ?: throw Exception("StudyLocation not found"))
    }
}
