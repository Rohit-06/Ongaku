package com.example.ongaku

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat

class MusicService: Service() , AudioManager.OnAudioFocusChangeListener{



    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer?=null
    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var runnable: Runnable // runnable helps us to run a one code multiple times
    lateinit var audioManager: AudioManager
    // on bind method tb call hota h jb apne kisi activity ko service ke sath combine krenge
    override fun onBind(intent: Intent?): IBinder {
    mediaSession = MediaSessionCompat(baseContext , "MyMusic")
    return myBinder
    }

    // this inner class helps us to return the object of main class
    inner class MyBinder: Binder()  // inner class can accese all the memebers of outer class
    {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn : Int   ,favimg:Int ) {

        val intent = Intent(baseContext , MainActivity::class.java)
        intent.putExtra("index" , PlayerActivity.songPosition)
        intent.putExtra("class" , "Now Playing")
        val contentIntent = PendingIntent.getActivity(this , 0 , intent , 0)

        val prevIntent = Intent(this , NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(this , 0 , prevIntent , PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(this , NotificationReceiver::class.java).setAction(ApplicationClass.PLAYPAUSE)
        val playPendingIntent = PendingIntent.getBroadcast(this , 0 , playIntent , PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this , NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(this , 0 , nextIntent , PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(this , NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(this , 0 , exitIntent , PendingIntent.FLAG_UPDATE_CURRENT)

        val favIntent = Intent(this , NotificationReceiver::class.java).setAction(ApplicationClass.FAV)
        val favPendingIntent = PendingIntent.getBroadcast(this , 0 , favIntent , PendingIntent.FLAG_UPDATE_CURRENT)

      // set image of song in notfication
        val imgArt = getImgArt(PlayerActivity.musiclistPA[PlayerActivity.songPosition].path)
        val image = if(imgArt != null)
        {
            BitmapFactory.decodeByteArray(imgArt , 0 , imgArt.size)
        }
        else
        {
            BitmapFactory.decodeResource(resources, R.drawable.music_album)
        }



        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musiclistPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musiclistPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_baseline_exit_to_app_24, "Exit", exitPendingIntent)
            .addAction(favimg , "Fav" , favPendingIntent)

            .build()
         if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
             val playbackSpeed = if(PlayerActivity.isPlaying) 1F else 0F
             mediaSession.setMetadata(MediaMetadataCompat.Builder()
                 .putLong(MediaMetadataCompat.METADATA_KEY_DURATION , mediaPlayer!!.duration.toLong())
                 .build())
             val playBackState = PlaybackStateCompat.Builder()
                 .setState(PlaybackStateCompat.STATE_PLAYING , mediaPlayer!!.currentPosition.toLong() , playbackSpeed)
                 .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                 .build()
             mediaSession.setPlaybackState(playBackState)
             mediaSession.setCallback(object : MediaSessionCompat.Callback(){
                 override fun onSeekTo(pos:Long){
                     super.onSeekTo(pos)
                     mediaPlayer!!.seekTo(pos.toInt())
                     val playBackStateNew = PlaybackStateCompat.Builder()
                         .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                         .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                         .build()
                     mediaSession.setPlaybackState(playBackStateNew)
                 }
             })
         }
        startForeground(13 , notification)

    }

     fun createMediaPlayer() {
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null)
                PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset() // !! ye btata h ki mediaplayer null nhi h or isme apne change krna chahte h
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musiclistPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.pause.setImageResource(R.drawable.ic_baseline_pause_24)
            PlayerActivity.binding.tvseekbarstart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvseekendtime.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekbarPA.progress = 0
            PlayerActivity.binding.seekbarPA.max = mediaPlayer!!.duration
            PlayerActivity.currentsongid = PlayerActivity.musiclistPA[PlayerActivity.songPosition].id

            PlayerActivity.fIndex = favChecker(PlayerActivity.currentsongid)
            if(PlayerActivity.isFav)
            showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_24)
            else
                showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_border_24)

        } catch (e: Exception) {
            return
        }

    }

   fun seekBarSetup(){
       runnable = Runnable {
           PlayerActivity.binding.tvseekbarstart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
           PlayerActivity.binding.seekbarPA.progress = mediaPlayer!!.currentPosition
           Handler(Looper.getMainLooper()).postDelayed(runnable , 200) // 200 millisecond baad fir se run krna chahiye
       }
        Handler(Looper.getMainLooper()).postDelayed(runnable , 0) // 0 millisecond pr upr vala code start ho jae
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <= 0)
        {
            // pause music
            PlayerActivity.binding.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            PlayerActivity.isPlaying = false
            NowPlaying.binding.playpauseNP.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            mediaPlayer!!.pause()

            PlayerActivity.fIndex = favChecker(PlayerActivity.currentsongid)
            if(PlayerActivity.isFav)
                showNotification(R.drawable.ic_baseline_play_arrow_24 , R.drawable.ic_baseline_favorite_24)
            else
                showNotification(R.drawable.ic_baseline_play_arrow_24 , R.drawable.ic_baseline_favorite_border_24)
        }
        else
        {
            // play music
            PlayerActivity.binding.pause.setImageResource(R.drawable.ic_baseline_pause_24)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
            NowPlaying.binding.playpauseNP.setImageResource(R.drawable.ic_baseline_pause_24)

            PlayerActivity.fIndex = favChecker(PlayerActivity.currentsongid)
            if(PlayerActivity.isFav)
                showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_24)
            else
                showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_border_24)
        }
    }

}


