package com.pietervandewalle.androidapp.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.isAlmostFull
import com.pietervandewalle.androidapp.model.isFull
import com.pietervandewalle.androidapp.ui.navigation.Screens
import com.pietervandewalle.androidapp.ui.navigation.deepLinkUri
import kotlinx.coroutines.flow.first

/**
 * A [CoroutineWorker] responsible for handling car parks notifications in the background.
 *
 * This worker is responsible for refreshing car park data, determining if there are
 * notifications to be sent, and creating and displaying notifications if needed. It is
 * intended to be used with the WorkManager library for performing background tasks.
 *
 */
class CarParksNotificationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    /**
     * Performs the background work to handle car park notifications.
     *
     * @return [Result.success] if the notification handling is successful,
     *         [Result.retry] if there was an error and the work should be retried.
     */
    override suspend fun doWork(): Result {
        val application = applicationContext as AndroidApplication
        val carParkRepository = application.container.carParkRepository

        return try {
            carParkRepository.refresh()
            val carParks = carParkRepository.getAll().first()

            val notificationContent = determineNotificationMessage(carParks, applicationContext)

            if (notificationContent != null) {
                makeCarParksNotification(notificationContent, applicationContext)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}

/**
 * Determines the content for the car parks notification message.
 *
 * @param carParks The list of car parks to analyze.
 * @param applicationContext The application context.
 * @return The notification message content, or null if no notification is needed.
 */
private fun determineNotificationMessage(carParks: List<CarPark>, applicationContext: Context): String? {
    val numberOfFullCarParks = carParks.count { it.isFull }
    val numberOfAlmostFullCarParks = carParks.count { it.isAlmostFull }

    return when {
        numberOfFullCarParks == 1 -> {
            val carPark = carParks.find { it.isFull }!!
            applicationContext.getString(R.string.notification_parking_full, carPark.name)
        }
        numberOfFullCarParks > 1 -> {
            applicationContext.getString(R.string.notification_multiple_parkings_full)
        }
        numberOfAlmostFullCarParks != 0 -> {
            applicationContext.getString(R.string.notification_multiple_parkings_almost_full)
        }
        else -> null
    }
}

/**
 * Creates and displays the car parks notification.
 *
 * @param notificationContent The content for the notification.
 * @param context The application context.
 */
private fun makeCarParksNotification(notificationContent: String, context: Context) {
    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "AndroidApp"
        val descriptionText = "AndroidApp notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("androidApp", name, importance).apply {
            description = descriptionText
        }

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create intent to open app on carParks overview
    // https://developer.android.com/develop/ui/views/notifications/navigation

    val deepLinkUri = Uri.parse("$deepLinkUri/${Screens.CarParks.route}")

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = deepLinkUri
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        setPackage(context.packageName)
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    // Create the notification
    val builder = NotificationCompat.Builder(context, "androidApp")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("AndroidApp")
        .setContentText(notificationContent)
        .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setVibrate(LongArray(0))
        .setAutoCancel(true) // automatically removes the notification when the user taps it.

    // Show the notification if permission is granted
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val oneTimeID = System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(context).notify(oneTimeID, builder.build())
    }
}
