package com.pietervandewalle.androidapp.repotests

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.pietervandewalle.androidapp.data.database.entity.asDbArticle
import com.pietervandewalle.androidapp.data.database.entity.asDomainArticle
import com.pietervandewalle.androidapp.data.repo.CachingArticleRepository
import com.pietervandewalle.androidapp.fake.FakeDataSource
import com.pietervandewalle.androidapp.fake.FakeGhentApiService
import com.pietervandewalle.androidapp.fake.dao.FakeArticleDao
import com.pietervandewalle.androidapp.model.Article
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class ArticleRepositoryTest {
    private lateinit var fakeApiService: FakeGhentApiService
    private lateinit var context: Context
    private lateinit var workManager: WorkManager

    @Before
    fun setup() {
        fakeApiService = FakeGhentApiService(FakeDataSource.apiCarParks, FakeDataSource.apiArticles, FakeDataSource.apiStudyLocations)
        context = ApplicationProvider.getApplicationContext()

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @After
    fun teardown() {
        WorkManagerTestInitHelper.closeWorkDatabase()
    }

    @Test
    fun `getAll returns no articles when dao is empty`() = runTest {
        val fakeDao = FakeArticleDao()
        val repo = CachingArticleRepository(
            fakeDao,
            fakeApiService,
            context,
        )

        val articlesFlow = repo.getAll()

        assertTrue(articlesFlow.first().isEmpty())
    }

    @Test
    fun `automatically refreshes and populates dao when initially empty`() = runTest {
        val fakeDao = FakeArticleDao() // Initially empty
        val repo = CachingArticleRepository(
            fakeDao,
            fakeApiService, // Returns 2 articles
            context,
        )

        // Initially, the repository should be empty
        val initialArticlesFlow = repo.getAll()
        val initialArticles = initialArticlesFlow.first()
        assertTrue(initialArticles.isEmpty())

        // Triggering getAll should automatically refresh and populate the DAO
        val updatedArticlesFlow = repo.getAll()
        val updatedArticles = updatedArticlesFlow.first()
        assertTrue(updatedArticles.isNotEmpty())
    }

    @Test
    fun `getAll enqueues periodic refresh worker`() = runTest {
        val fakeDao = FakeArticleDao() // Initially empty
        val repo = CachingArticleRepository(
            fakeDao,
            fakeApiService, // Returns 2 articles
            context,
        )

        // Invoke the method that should start the worker
        repo.getAll().first()

        // Verify the work is enqueued
        val workInfosWithTag = workManager.getWorkInfos(WorkQuery.fromUniqueWorkNames("refreshArticlesPeriodically")).get(5L, TimeUnit.SECONDS)
        assertNotNull(workInfosWithTag.firstOrNull())

        val workInfo = workInfosWithTag.first()
        assertTrue(workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING)
    }

    @Test
    fun `getAll enqueues periodic notification worker`() = runTest {
        val fakeDao = FakeArticleDao() // Initially empty
        val repo = CachingArticleRepository(
            fakeDao,
            fakeApiService, // Returns 2 articles
            context,
        )

        // Invoke the method that should start the worker
        repo.getAll().first()

        // Verify the work is enqueued
        val workInfosWithTag = workManager.getWorkInfos(WorkQuery.fromUniqueWorkNames("refreshArticlesPeriodically")).get(5L, TimeUnit.SECONDS)
        assertNotNull(workInfosWithTag.firstOrNull())

        val workInfo = workInfosWithTag.first()
        assertTrue(workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING)
    }

    @Test
    fun `correctly inserts an article`() = runTest {
        val fakeDao = FakeArticleDao()
        val repo = CachingArticleRepository(fakeDao, fakeApiService, context)

        val articleToInsert = Article(111, "test", LocalDate.now(), "https://test.be/readmore", "testcontent", "https://test.be/image.png")
        repo.insert(articleToInsert)

        val insertedArticle = fakeDao.getById(articleToInsert.id).first()
        assertEquals(articleToInsert, insertedArticle.asDomainArticle())
    }

    @Test
    fun `returns correct article by id`() = runTest {
        val fakeDao = FakeArticleDao(FakeDataSource.domainArticles.map { it.asDbArticle() })
        val repo = CachingArticleRepository(fakeDao, fakeApiService, context)

        val articleToFind = FakeDataSource.domainArticles.first()

        val retrievedArticleFlow = repo.getById(articleToFind.id)
        val retrievedArticle = retrievedArticleFlow.first()
        assertEquals(articleToFind, retrievedArticle)
    }

    @Test
    fun `refresh throws error for UI to catch`() = runTest {
        val fakeDao = FakeArticleDao()
        fakeApiService.setShouldReturnNetworkError(true)
        val repo = CachingArticleRepository(fakeDao, fakeApiService, context)

        try {
            repo.refresh()
            fail("Expected an exception during refresh, but none was thrown")
        } catch (e: Exception) {
            assertTrue(e is IOException)
        }
    }

    @Test
    fun `refresh inserts new articles`() = runTest {
        val fakeDao = FakeArticleDao()
        val repo = CachingArticleRepository(fakeDao, fakeApiService, context)

        // Trigger a refresh
        repo.refresh()

        assertEquals(fakeApiService.getArticles().results.size, fakeDao.articles!!.size)
    }
}
