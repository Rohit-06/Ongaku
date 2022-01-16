package com.example.ongaku

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

//application class is the base class that contains all other components such as activitites and services application class or any subclass of appplication class instantiated before any other class
class ApplicationClass: Application() {
    companion object
    {
        const val CHANNEL_ID = "Channel 1"
        const val PLAYPAUSE = "play/pause"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
        const val FAV = "Fav"
    }

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) // this is to check if current android version of user is greader than android o (because api level 26 has feature that every notification has different channer)
        {
          val notificationChannel = NotificationChannel(CHANNEL_ID , "Now Playing Song" , NotificationManager.IMPORTANCE_HIGH)
          notificationChannel.description = "This is important channel for showing song!!"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}