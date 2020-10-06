package com.flutter_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {


    private val TAG = MyFirebaseMessagingService::class.simpleName

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG,token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var mediaPlayer = MediaPlayer()
        try {
            var afd = assets.openFd("ringer.mp3")
            mediaPlayer.setDataSource(afd.fileDescriptor,afd.startOffset,afd.length)
            //mediaPlayer.setDataSource(this, Uri.parse("android.resource://com.flutter_app/raw/ringer.mp3"))
            afd.close()
            mediaPlayer.prepare()
            mediaPlayer.start()
            try {
                showNotification(remoteMessage.data["title"], remoteMessage.data["message"])
            } catch (e: Exception) {
                //println("$tag error -->${e.localizedMessage}")
                Log.e(TAG,e.localizedMessage)
            }
        }
        catch (e : Exception)
        {
            Log.e("Notification","Ringer",e)
        }
    }

    private fun showNotification(
        title: String?,
        body: String?
    ) {

        Log.d(TAG,"Remove message is ${title}")

        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.channel_id)
        val channelName = getString(R.string.channel_name)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannels(channelId, channelName, notificationManager)
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //var soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+":/"+packageName+"/raw/ringer.mp3")
        var soundUri = Uri.parse("android.resource://$packageName/raw/ringer.mp3")
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
        notificationManager.notify(0, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels(
        channelId: String,
        channelName: String,
        notificationManager: NotificationManager
    ) { val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }

}