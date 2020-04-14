package com.dancing_koala.clairdelune.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dancing_koala.clairdelune.R
import com.dancing_koala.clairdelune.ui.HomeActivity

class NotificationService : Service() {

    companion object {
        const val CHANNEL_ID = "NewImageChannel"
        const val CODE_HOME_ACTIVITY = 306
        const val NOTIF_ID = 111
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val newIntent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(this, CODE_HOME_ACTIVITY, newIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_moon)
            .setContentText("Une nouvelle image de lune est disponible  ❤")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        createNotificationChannel()

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIF_ID, notifBuilder.build())
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw NotImplementedError()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Nouvelle image"
            val descriptionText = "Indique quand une nouvelle image est disponible."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}