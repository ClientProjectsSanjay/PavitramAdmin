package com.artisan.un.utils.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.artisan.un.BuildConfig
import com.artisan.un.R
import com.artisan.un.ui.splashscreen.ActivitySplashScreen
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FireBaseMessage : FirebaseMessagingService() {
    private var mNotificationManager: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    private val channelId = "com.artisan.un"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        addNotification(remoteMessage)
    }

    /**
     * Read and format data received from the notification payload.
     */
    private fun addNotification(remoteMessage: RemoteMessage?) {
        val data = Gson().fromJson(Gson().toJson(remoteMessage?.data), NotificationData::class.java)
        if (data.type == "custom" && !TextUtils.isEmpty(data.image)) {
            Glide.with(applicationContext)
                .asBitmap()
                .load(data.image)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("FireBaseMessage", " onLoadFailed " + e?.message)
                        displayNotification(null, data)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("FireBaseMessage", " onResourceReady " + resource)
                        displayNotification(resource, data)
                        return false
                    }
                }).submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
        } else {
            displayNotification(null, data)
        }
    }

    /**
     * Build and display the notification. In case [bitmap] is null displays simple text notification else display large
     * image notification.
     */
    private fun displayNotification(bitmap: Bitmap?, data: NotificationData) {
        Log.d("FireBaseMessage", " diaplayNotification " + bitmap)

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = generateRandom()
        val intent = Intent(applicationContext,ActivitySplashScreen::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        val contentIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNotificationCompatChanel()
            NotificationCompat.Builder(this, channelId)
        } else {
            NotificationCompat.Builder(this)
        }

        val style = if (bitmap != null) NotificationCompat.BigPictureStyle().bigPicture(bitmap)
        else NotificationCompat.BigTextStyle().bigText(data.body)

        bitmap?.let { builder?.setLargeIcon(it) }
        builder?.setSmallIcon(R.drawable.app_logo)
        builder?.setLargeIcon(bitmap ?: BitmapFactory.decodeResource(resources, R.drawable.app_logo))
        builder?.color = ContextCompat.getColor(this, R.color.brown)
        builder?.setContentTitle(data.title)
        builder?.setContentText(data.body)
        builder?.setStyle(style)
        builder?.setContentIntent(contentIntent)
        builder?.setAutoCancel(true)
        mNotificationManager?.notify(id, builder?.build())
    }

    private fun generateRandom(): Int {
        return ((Math.random() * 100).toInt())
    }

    /**
     * Build a new notification channel with [NotificationManager.IMPORTANCE_DEFAULT].
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    internal fun setNotificationCompatChanel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, applicationContext.getString(R.string.app_name), importance)

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        mNotificationManager?.createNotificationChannel(notificationChannel)
    }

    data class NotificationData(
        var title: String? = "",
        var type: String? = "",
        var image: String? = "",
        var body: String? = ""
    )
}


