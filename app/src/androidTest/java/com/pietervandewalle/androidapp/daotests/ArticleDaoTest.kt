package com.pietervandewalle.androidapp.daotests

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pietervandewalle.androidapp.data.database.MyRoomDatabase
import com.pietervandewalle.androidapp.data.database.dao.ArticleDao
import com.pietervandewalle.androidapp.data.database.entity.asDbArticle
import com.pietervandewalle.androidapp.data.sampler.ArticleSampler
import com.pietervandewalle.androidapp.model.Article
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
class ArticleDaoTest {
    private lateinit var articleDao: ArticleDao
    private lateinit var roomDb: MyRoomDatabase

    private val articles = ArticleSampler.getAll()

    private suspend fun addFirstArticleToDb() {
        articleDao.insert(articles.first().asDbArticle())
    }

    private suspend fun addAllArticlesToDb() {
        articles.forEach {
            articleDao.insert(it.asDbArticle())
        }
    }

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        roomDb = Room.inMemoryDatabaseBuilder(context, MyRoomDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        articleDao = roomDb.articleDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        roomDb.close()
    }

    @Test
    fun `getAll returns no articles when dao is empty`() = runBlocking {
        val articles = articleDao.getAll().first()
        assertTrue(articles.isEmpty())
    }

    @Test
    fun `insert and getById returns the same article`() = runBlocking {
        val testArticle = articles.first()
        articleDao.insert(testArticle.asDbArticle())

        val retrievedArticle = articleDao.getById(testArticle.id).first()
        assertEquals(testArticle.asDbArticle(), retrievedArticle)
    }

    @Test
    fun `getAll returns all inserted articles in correct order`() = runBlocking {
        addAllArticlesToDb()

        val retrievedArticles = articleDao.getAll().first()
        val sortedArticles = articles.sortedWith(compareByDescending<Article> { it.date }.thenBy { it.title })
        assertEquals(sortedArticles.map { it.asDbArticle() }, retrievedArticles)
    }

    @Test
    fun `insert with existing id ignores new entry`() = runBlocking {
        val testArticle = articles.first()
        articleDao.insert(testArticle.asDbArticle())
        // Attempt to insert the same article again
        articleDao.insert(testArticle.asDbArticle())

        val retrievedArticles = articleDao.getAll().first()
        assertEquals(1, retrievedArticles.size)
    }

    @Test
    fun `getById returns null for non-existent article`() = runBlocking {
        val nonExistentId = 999
        val retrievedArticle = articleDao.getById(nonExistentId).firstOrNull()
        assertNull(retrievedArticle)
    }

    @Test
    fun `insert multiple articles and getAll returns all`() = runBlocking {
        addAllArticlesToDb()

        val retrievedArticles = articleDao.getAll().first()
        assertEquals(articles.size, retrievedArticles.size)
    }
}
