package com.pietervandewalle.androidapp.fake

import com.pietervandewalle.androidapp.data.sampler.ArticleSampler
import com.pietervandewalle.androidapp.data.sampler.CarParkSampler
import com.pietervandewalle.androidapp.data.sampler.StudyLocationSampler
import com.pietervandewalle.androidapp.network.asApiObjects

object FakeDataSource {
    val apiArticles = ArticleSampler.getAll().asApiObjects()
    val apiStudyLocations = StudyLocationSampler.getAll().asApiObjects()
    val apiCarParks = CarParkSampler.getAll().asApiObjects()

    val domainArticles = ArticleSampler.getAll()
    val domainStudyLocations = StudyLocationSampler.getAll()
    val domainCarParks = CarParkSampler.getAll()
}
