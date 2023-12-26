package com.pietervandewalle.androidapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pietervandewalle.androidapp.AndroidApplication


/**
 * A [CoroutineWorker] that refreshes car parks in the background.
 *
 * This worker is responsible for refreshing car parks by calling the `refresh()` method
 * of the carParkRepository in the background. It is intended to be used with the
 * WorkManager library for performing background tasks.
 *
 */
class CarParksRefreshWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    /**
     * Performs the background work to refresh car parks.
     *
     * @return [Result.success] if the refresh operation is successful, [Result.retry]
     *         if there was an error and the work should be retried. Note that [Result.retry]
     *         is a function provided by WorkManager.
     */
    override suspend fun doWork(): Result {
        val application = applicationContext as AndroidApplication
        val carParkRepository = application.container.carParkRepository

        return try {
            carParkRepository.refresh()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
