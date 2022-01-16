package com.example.ongaku

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
       when(intent?.action)
       {
           ApplicationClass.PREVIOUS -> previous(context = context!!)
           ApplicationClass.PLAYPAUSE -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
           ApplicationClass.NEXT -> next(context = context!!)
           ApplicationClass.EXIT ->{
               PlayerActivity.musicService!!.stopForeground(true)
               PlayerActivity.musicService!!.mediaPlayer!!.release()
               PlayerActivity.musicService = null
               exitProcess(1)
           }
           ApplicationClass.FAV->{
               PlayerActivity.fIndex = favChecker(PlayerActivity.musiclistPA[PlayerActivity.songPosition].id)
               if(PlayerActivity.isFav)
               {
                   PlayerActivity.binding.favbtnPA.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                   PlayerActivity.isFav = false
                   Favactivity.favSongs.removeAt(PlayerActivity.fIndex)
                   PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_border_24)

               }
               else
               {
                   PlayerActivity.binding.favbtnPA.setImageResource(R.drawable.ic_baseline_favorite_24)
                   PlayerActivity.isFav = true
                   Favactivity.favSongs.add(PlayerActivity.musiclistPA[PlayerActivity.songPosition])
                   PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_24)
               }
           }
       }
    }

    private fun playMusic()
    {
        PlayerActivity.fIndex = favChecker(PlayerActivity.musiclistPA[PlayerActivity.songPosition].id)
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.binding.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        NowPlaying.binding.playpauseNP.setImageResource(R.drawable.ic_baseline_pause_24)
        if(PlayerActivity.isFav)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_24)
        else
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_border_24)

    }
    private fun pauseMusic()
    {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.binding.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        NowPlaying.binding.playpauseNP.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        if(PlayerActivity.isFav)
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24 , R.drawable.ic_baseline_favorite_24)
        else
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24 , R.drawable.ic_baseline_favorite_border_24)
    }

    private fun previous(context: Context){
        setPreviousSongPosition()
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musiclistPA[PlayerActivity.songPosition].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
            .into(PlayerActivity.binding.songimagepa)


        PlayerActivity.binding.songnamepa.text = PlayerActivity.musiclistPA[PlayerActivity.songPosition].title
        PlayerActivity.binding.tvseekendtime.text = formatDuration(PlayerActivity.musiclistPA[PlayerActivity.songPosition].duration)
        playMusic()
    }

    private fun next(context: Context)
    {
        setNextSongPosition()
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musiclistPA[PlayerActivity.songPosition].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
            .into(PlayerActivity.binding.songimagepa)

        // for now playing fragment
        Glide.with(context)
            .load(PlayerActivity.musiclistPA[PlayerActivity.songPosition].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
            .into(NowPlaying.binding.songimgNP)

        NowPlaying.binding.songnameNP.text = PlayerActivity.musiclistPA[PlayerActivity.songPosition].title

        PlayerActivity.binding.songnamepa.text = PlayerActivity.musiclistPA[PlayerActivity.songPosition].title
        PlayerActivity.binding.tvseekendtime.text = formatDuration(PlayerActivity.musiclistPA[PlayerActivity.songPosition].duration)

        val imgArt = getImgArt(PlayerActivity.musiclistPA[PlayerActivity.songPosition].path)

        if(imgArt!=null)
        {
           val img =   BitmapFactory.decodeByteArray(imgArt , 0 , imgArt.size)
            updatePlayerBar(img)
        }
        // for the fav button
        PlayerActivity.fIndex = favChecker(PlayerActivity.musiclistPA[PlayerActivity.songPosition].id)
        if(PlayerActivity.isFav)
            PlayerActivity.binding.favbtnPA.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            PlayerActivity.binding.favbtnPA.setImageResource(R.drawable.ic_baseline_favorite_border_24)


        playMusic()
    }
}