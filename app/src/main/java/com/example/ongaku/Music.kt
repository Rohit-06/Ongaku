package com.example.ongaku


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import kotlin.system.exitProcess


data class Music(val id:String , val title:String , val album:String, val artist:String , val duration:Long = 0 , val path:String , var arturi:String) {
}

class Playlist{
    lateinit var name:String
    lateinit var playlist:ArrayList<Music>
    lateinit var createdBy:String
    lateinit var createdOn:String
}
class MusicPlaylist{
    var ref:ArrayList<Playlist> = ArrayList()
}
fun formatDuration(duration: Long): String {
    val hrs: Long = duration / 3600000
    val mns: Long = duration / 60000 % 60000
    val scs: Long = duration % 60000 / 1000

//    val minutes = TimeUnit.MINUTES.convert(duration , TimeUnit.MILLISECONDS)
//    val seconds = (TimeUnit.SECONDS.convert(duration , TimeUnit.MILLISECONDS) - minutes*TimeUnit.SECONDS.convert(1 , TimeUnit.MINUTES))

    return String.format("%02d:%02d"  ,  mns , scs)
}

fun getImgArt(path: String) : ByteArray?
{
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setNextSongPosition() {

    if(!PlayerActivity.repeat) {
        if (PlayerActivity.musiclistPA.size - 1 == PlayerActivity.songPosition)
            PlayerActivity.songPosition = 0
        else
            ++PlayerActivity.songPosition
    }
}

fun setPreviousSongPosition()
{
   if(!PlayerActivity.repeat) {
       if (0 == PlayerActivity.songPosition)
           PlayerActivity.songPosition = PlayerActivity.musiclistPA.size - 1
       else
           --PlayerActivity.songPosition
   }
}


// set the now playing tile color with image color
@SuppressLint("ResourceAsColor")
fun updatePlayerBar(bitmap: Bitmap) {
    Palette.from(bitmap).generate(Palette.PaletteAsyncListener()
    {
        val swatch = it?.dominantSwatch
        if (swatch != null) {
           PlayerActivity.binding.songimagepa.setBackgroundResource(R.drawable.gradient)
            PlayerActivity.binding.playeractivitylayout.setBackgroundColor(swatch.rgb)
            NowPlaying.binding.nowplayinglayout.setBackgroundColor(swatch.rgb)


            PlayerActivity.binding.songnamepa.setTextColor(swatch.bodyTextColor)
            PlayerActivity.binding.tvseekbarstart.setTextColor(swatch.bodyTextColor)
            PlayerActivity.binding.tvseekendtime.setTextColor(swatch.bodyTextColor)
            PlayerActivity.binding.pause.setColorFilter(swatch.bodyTextColor)
            PlayerActivity.binding.next.setColorFilter(swatch.bodyTextColor)
            PlayerActivity.binding.previous.setColorFilter(swatch.bodyTextColor)
            PlayerActivity.binding.sharebtnpa.setColorFilter(swatch.bodyTextColor)
            PlayerActivity.binding.repeat.setColorFilter(swatch.bodyTextColor)
            PlayerActivity.binding.lyrics.setTextColor(swatch.bodyTextColor)

            NowPlaying.binding.nextNP.setColorFilter(swatch.bodyTextColor)
            NowPlaying.binding.playpauseNP.setColorFilter(swatch.bodyTextColor)
            NowPlaying.binding.songnameNP.setTextColor(swatch.bodyTextColor)

        } else {

            PlayerActivity.binding.playeractivitylayout.setBackgroundColor(Color.GRAY)
            NowPlaying.binding.nowplayinglayout.setBackgroundColor(Color.GRAY)
            PlayerActivity.binding.songnamepa.setTextColor(Color.WHITE)
            PlayerActivity.binding.tvseekbarstart.setTextColor(Color.WHITE)
            PlayerActivity.binding.tvseekendtime.setTextColor(Color.WHITE)
            PlayerActivity.binding.pause.setColorFilter(Color.WHITE)
            PlayerActivity.binding.next.setColorFilter(Color.WHITE)
            PlayerActivity.binding.previous.setColorFilter(Color.WHITE)
            PlayerActivity.binding.sharebtnpa.setColorFilter(Color.WHITE)
            PlayerActivity.binding.repeat.setColorFilter(Color.WHITE)
            PlayerActivity.binding.lyrics.setTextColor(Color.WHITE)
            NowPlaying.binding.nextNP.setColorFilter(Color.WHITE)
            NowPlaying.binding.playpauseNP.setColorFilter(Color.WHITE)
            NowPlaying.binding.songnameNP.setTextColor(Color.WHITE)
        }
    })

    }







fun favChecker(id:String):Int
{
    PlayerActivity.isFav = false
    Favactivity.favSongs.forEachIndexed{index, music ->
        if(id == music.id){
            PlayerActivity.isFav = true
            return index
        }
    }

    return -1
}

@SuppressLint("SetTextI18n")
fun updatePlaylistDetails()
{
    PlaylistDetails.binding.playlistnamePD.setText(Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].name)
    PlaylistDetails.binding.moreinfo.setText("Total ${PlaylistDetails.adapter.itemCount} Songs .\n\n" +
            "Created By: ${Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].createdBy}\n\n"+
            "Created On:${Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].createdOn}")

    if(PlaylistDetails.adapter.itemCount>0)
    {
        Glide.with(PlaylistDetails.context)
            .load(Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist[0].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
            .into(PlaylistDetails.binding.playlistimgPD)
    }
}


// this is to check if usser has removed any file from the system or not
fun checkPlaylist(playlist:ArrayList<Music>):ArrayList<Music>{
    playlist.forEachIndexed{index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)

    }

    return playlist
}

fun exitApplication()
{
    if(PlayerActivity.musicService != null){
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null}
    exitProcess(1)
}

