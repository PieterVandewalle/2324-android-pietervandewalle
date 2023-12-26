package com.pietervandewalle.androidapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pietervandewalle.androidapp.AndroidApplication

/**
 * A [CoroutineWorker] that refreshes articles in the background.
 *
 * This worker is responsible for refreshing articles by calling the `refresh()` method
 * of the articleRepository in the background. It is intended to be used with the
 * WorkManager library for performing background tasks.
 *
 */
class ArticlesRefreshWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    /**
     * Performs the background work to refresh articles.
     *
     * @return [Result.success] if the refresh operation is successful, [Result.retry]
     *         if there was an error and the work should be retried. Note that [Result.retry]
     *         is a function provided by WorkManager.
     */
    override suspend fun doWork(): Result {
        val application = applicationContext as AndroidApplication
        val articleRepository = application.container.articleRepository

        return try {
            articleRepository.refresh()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
