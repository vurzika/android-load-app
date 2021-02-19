package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AppNotificationsManager(private val context: Context) {

    // Notification ID - current notification instance
    private companion object {
        const val NOTIFICATION_ID = 0
        const val NOTIFICATION_CHANNEL_ID = "load-app-notification-channel"
    }

    init {
        registerNotificationChannel()
    }

    // Register channel to be able to show notifications on Android O+
    private fun registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(selectedCategoryName: String, downloadSuccessful: Boolean) {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_assistant)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_channel_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Adding button with the action to open details screen
            .addAction(
                0,
                context.getString(R.string.notification_button_title),
                createOpenDetailsActivityIntent(selectedCategoryName, downloadSuccessful)
            )
        // Sending the notification
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    // Creating an intent to open details activity
    private fun createOpenDetailsActivityIntent(
        selectedCategoryName: String,
        downloadSuccessful: Boolean
    ): PendingIntent {
        val notifyIntent = Intent(context, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            putExtra(DetailActivity.INTENT_EXTRA_CATEGORY_NAME, selectedCategoryName)
            putExtra(DetailActivity.INTENT_EXTRA_DOWNLOAD_STATUS, downloadSuccessful)
        }

        return PendingIntent.getActivity(
            context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


}