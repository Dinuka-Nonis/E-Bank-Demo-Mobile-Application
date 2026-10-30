package com.example.ebank

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Lab 6: started Service that waits 30 seconds after the Dashboard loads, posts a
 * "session expiring soon" notification, then stops itself.
 *
 * Extends LifecycleService (not Service) so lifecycleScope is available: the coroutine
 * below is cancelled automatically if the service is destroyed, instead of leaking.
 */
class SessionReminderService : LifecycleService() {

    companion object {
        const val CHANNEL_ID = "session_reminder_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        lifecycleScope.launch {
            delay(30_000)
            postReminderNotification()
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Session Reminders", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Warns you before your banking session times out"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun postReminderNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Session expiring soon")
            .setContentText("You will be signed out soon due to inactivity.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val hasPermission = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
        }
    }
}
