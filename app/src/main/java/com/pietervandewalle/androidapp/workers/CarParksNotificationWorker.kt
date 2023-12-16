package com.pietervandewalle.androidapp.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.isAlmostFull
import com.pietervandewalle.androidapp.model.isFull

class CarParksNotificationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val application = applicationContext as AndroidApplication
        val carParkRepository = application.container.carParkRepository

        return try {
            carParkRepository.refresh()

            carParkRepository.getAll().collect { carParks ->
                val numberOfFullCarParks = carParks.count { it.isFull }
                val numberOfAlmostFullCarParks = carParks.count { it.isAlmostFull }
                var notificationMessage: String? = null

                if (numberOfFullCarParks == 1) {
                    val carPark = carParks.find { it.isFull }
                    notificationMessage = applicationContext.getString(R.string.melding_opgelet_parking_is_volzet, carPark!!.name)
                } else if (numberOfFullCarParks > 1) {
                    notificationMessage =
                        applicationContext.getString(
                            R.string.melding_opgelet_meerdere_parkings_volzet,
                        )
                } else if (numberOfAlmostFullCarParks != 0) {
                    notificationMessage =
                        applicationContext.getString(R.string.opgelet_een_aantal_parkings_bijna_volzet)
                }
                if (notificationMessage != null) {
                    makeNotification(notificationMessage, applicationContext)
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}

fun makeNotification(message: String, context: Context) {
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

    // Create the notification
    val builder = NotificationCompat.Builder(context, "androidApp")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("AndroidApp")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

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
