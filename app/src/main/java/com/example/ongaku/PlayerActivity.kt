package com.example.ongaku

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ongaku.databinding.ActivityPlayerBinding
import com.google.gson.GsonBuilder


@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity(), ServiceConnection , MediaPlayer.OnCompletionListener {
   
    companion object {
        lateinit var musiclistPA: ArrayList<Music>
        var songPosition: Int = 0
      //  var mediaPlayer: MediaPlayer? = null // isko hta denge kyunki ab apno ne musicservice ka object bna lia h jo ki destroy nhi hota app bnd krne pr
        var isPlaying: Boolean = false
        var musicService:MusicService?=null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat:Boolean = false
        var currentsongid : String = ""
        var isFav : Boolean = false
        var fIndex:Int = -1
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Ongaku)
        supportActionBar?.hide()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // api call for lyrics
        binding.lyrics.setOnClickListener {
            val intent = Intent(this , Lyrics::class.java)
//            intent.putExtra("title"  , musiclistPA[songPosition].title)
//            intent.putExtra("artist" , musiclistPA[songPosition].artist)
            intent.putExtra("class" , "PlayerActivity")
            startActivity(intent)
        }

        // to play the song when clicked from the file manager

        if(intent.data?.scheme.contentEquals("content")){
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)

            musiclistPA = ArrayList()
            musiclistPA.add(getMusicDetail(intent.data!!))
            Glide.with(this)
                .load(getImgArt(musiclistPA[songPosition].path))
                .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
                .into(binding.songimagepa)
            binding.songnamepa.text = musiclistPA[songPosition].title
            binding.tvseekendtime.text = formatDuration(musiclistPA[songPosition].duration)

        }
        else {
            initializeLayout()
        }
        binding.pause.setOnClickListener {
            if (isPlaying)
                pauseMusic()
            else
                playMusic()
        }
        binding.next.setOnClickListener {
            nextSong()
        }
        binding.previous.setOnClickListener {
            previousSong()
        }
       binding.seekbarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
           override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
             if(fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
           }

           override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

           override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
       })

        binding.repeat.setOnClickListener{

            if(!repeat)
            {
                repeat = true
                Toast.makeText(this , "Repeat button is clicked" , Toast.LENGTH_SHORT).show()
                binding.repeat.setBackgroundColor(resources.getColor(R.color.teal_700))

            }
            else
            {
                repeat = false
                Toast.makeText(this , "Repeat button is clicked" , Toast.LENGTH_SHORT).show()
                binding.repeat.background = null
            }

        }
        // for marquee of song name
        binding.songnamepa.isSelected = true
        binding.backbtn.setOnClickListener {
            finish()
        }

        binding.equilizer.setOnClickListener{

           try{
               binding.equilizer.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
               Toast.makeText(this , "Equalizer Button is clicked" , Toast.LENGTH_SHORT).show()
               val EqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
               EqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION , musicService!!.mediaPlayer!!.audioSessionId)
               EqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME , baseContext.packageName)
               EqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE , AudioEffect.CONTENT_TYPE_MUSIC)
               startActivityForResult(EqIntent , 13) //startActivityForResult() method, we can get result from another activity.
           }
           catch (e:Exception)
           {
               Toast.makeText(this , "Equalizer Feature is not Supported by your device!!" , Toast.LENGTH_SHORT).show()

           }
        }

        binding.sharebtnpa.setOnClickListener {
            binding.sharebtnpa.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
            Toast.makeText(this , "Share button is clicked" , Toast.LENGTH_SHORT).show()

            // Implicit intent
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM , Uri.parse(musiclistPA[songPosition].path))
            startActivityForResult(Intent.createChooser(shareIntent , "Sharing this music file!!") , 13)
        }

        binding.favbtnPA.setOnClickListener {
            fIndex = favChecker(currentsongid)
            if(isFav)
            {
                binding.favbtnPA.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                isFav = false
                Favactivity.favSongs.removeAt(fIndex)
                musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_border_24)

            }
            else
            {
                binding.favbtnPA.setImageResource(R.drawable.ic_baseline_favorite_24)
                isFav = true
                Favactivity.favSongs.add(musiclistPA[songPosition])
                musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_24)
            }

        }

    }


    private fun setLayout() {
        fIndex = favChecker(musiclistPA[songPosition].id)
        Glide.with(this)
            .load(musiclistPA[songPosition].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
            .into(binding.songimagepa)

        binding.songnamepa.text = musiclistPA[songPosition].title
        binding.tvseekendtime.text = formatDuration(musiclistPA[songPosition].duration)

         if(isFav)
             binding.favbtnPA.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
             binding.favbtnPA.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        if(repeat)
            binding.repeat.setBackgroundColor(resources.getColor(R.color.teal_700))

        val imgArt = getImgArt(musiclistPA[songPosition].path)
        val img = if(imgArt!=null)
        {
            BitmapFactory.decodeByteArray(imgArt , 0 , imgArt.size)
        }
        else
        {
            BitmapFactory.decodeResource(resources, R.drawable.music_album)
        }

        updatePlayerBar(img)

    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "Now Playing" -> {
                setLayout()
                binding.tvseekbarstart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.seekbarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekbarPA.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying)
                    binding.pause.setImageResource(R.drawable.ic_baseline_pause_24)
                else
                    binding.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)

            }
//            "MusicAdapterSearch" -> {
//                // for starting service
//                val intent = Intent(this, MusicService::class.java)
//                bindService(intent, this, BIND_AUTO_CREATE)
//                startService(intent)
//
//                musiclistPA = ArrayList()
//                musiclistPA.addAll(MainActivity.musicsearchlist)
//                setLayout()
//            }
//            "MusicAdapter" -> {
//                // for starting service
//                val intent = Intent(this, MusicService::class.java)
//                bindService(intent, this, BIND_AUTO_CREATE)
//                startService(intent)
//
//                musiclistPA = ArrayList()
//                musiclistPA.addAll(MainActivity.MusicListMA)
//                setLayout()
//
//            }
//            "MainActivity" -> {
//                // for starting service
//                val intent = Intent(this, MusicService::class.java)
//                bindService(intent, this, BIND_AUTO_CREATE)
//                startService(intent)
//
//                musiclistPA = ArrayList()
//                musiclistPA.addAll(MainActivity.MusicListMA)
//                musiclistPA.shuffle()
//                setLayout()
//            }
//            "FavAdapter" -> {
//                val intent = Intent(this, MusicService::class.java)
//                bindService(intent, this, BIND_AUTO_CREATE)
//                startService(intent)
//
//                musiclistPA = ArrayList()
//                musiclistPA.addAll(Favactivity.favSongs)
//                setLayout()
//            }
//            "FavShuffle" -> {
//                val intent = Intent(this, MusicService::class.java)
//                bindService(intent, this, BIND_AUTO_CREATE)
//                startService(intent)
//
//                musiclistPA = ArrayList()
//                musiclistPA.addAll(Favactivity.favSongs)
//                musiclistPA.shuffle()
//                setLayout()
//            }
//            "PlaylistDetailsAdapter" ->
//            {
//                val intent = Intent(this, MusicService::class.java)
//                bindService(intent, this, BIND_AUTO_CREATE)
//                startService(intent)
//
//                musiclistPA = ArrayList()
//                musiclistPA.addAll(Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist)
//                setLayout()
//            }
//            "PlaylistDetailsShuffle" -> {
//                val intent = Intent(this, MusicService::class.java)
//                bindService(intent, this, BIND_AUTO_CREATE)
//                startService(intent)
//
//                musiclistPA = ArrayList()
//                musiclistPA.addAll(Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist)
//                musiclistPA.shuffle()
//                setLayout()
//            }
            "MusicAdapterSearch"-> initServiceAndPlaylist(MainActivity.musicsearchlist, shuffle = false)
            "MusicAdapter" -> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = false)
            "FavAdapter"-> initServiceAndPlaylist(Favactivity.favSongs, shuffle = false)
            "MainActivity"-> initServiceAndPlaylist(MainActivity.MusicListMA, shuffle = true)
            "FavShuffle"-> initServiceAndPlaylist(Favactivity.favSongs, shuffle = true)
            "PlaylistDetailsAdapter"->
                initServiceAndPlaylist(Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist, shuffle = false)
            "PlaylistDetailsShuffle"->
                initServiceAndPlaylist(Playlistactivity.playlists.ref[PlaylistDetails.currentPlaylistpos].playlist, shuffle = true)
        }
    }

    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null)
                musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset() // !! ye btata h ki mediaplayer null nhi h or isme apne change krna chahte h
            musicService!!.mediaPlayer!!.setDataSource(musiclistPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            binding.tvseekbarstart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbarPA.progress = 0
            binding.seekbarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            currentsongid = musiclistPA[songPosition].id
            playMusic()
        } catch (e: Exception) {
            Toast.makeText(this , e.toString(),Toast.LENGTH_SHORT).show()
        }

    }

    private fun playMusic() {
        binding.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        NowPlaying.binding.playpauseNP.setImageResource(R.drawable.ic_baseline_pause_24)

        fIndex = favChecker(currentsongid)
        if(isFav)
            musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_24)
        else
            musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_border_24)
    }

    private fun pauseMusic() {
        binding.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        isPlaying = false
        NowPlaying.binding.playpauseNP.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        musicService!!.mediaPlayer!!.pause()

        fIndex = favChecker(currentsongid)
        if(isFav)
            musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24 , R.drawable.ic_baseline_favorite_24)
        else
            musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24 , R.drawable.ic_baseline_favorite_border_24)

    }

    private fun nextSong() {
        setNextSongPosition()
        createMediaPlayer()
        setLayout()
    }

    private fun previousSong() {
        setPreviousSongPosition()
        createMediaPlayer()
        setLayout()
    }



    override fun onServiceConnected(name: ComponentName?, service: IBinder?)
    {
        if(musicService == null){
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
            musicService!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        createMediaPlayer()
        musicService!!.seekBarSetup()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
         musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setNextSongPosition()
        createMediaPlayer()
        setLayout()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 || resultCode == RESULT_OK)
        {
            binding.equilizer.background = null
            binding.sharebtnpa.background = null
            return
        }

    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getMusicDetail(contentUri:Uri):Music
    {
          var cursor:Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA , MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contentUri , projection , null , null ,null)
            val dataColumns = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumns = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumns?.let { cursor.getString(it) }
            val duration  = durationColumns?.let { cursor.getLong(it) }!!

            return Music(id = "Unkown" , title = path.toString() , album = "Unkonwn" , artist = "Unkonwn" , duration = duration , arturi = "Unkonw" , path = path.toString())

        }finally {
            cursor?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // for storing favourites and playlist data using shared preferences

        val editor = getSharedPreferences("FAVOURITES" , MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(Favactivity.favSongs)
        editor.putString("FavSongs" , jsonString)
        val jsonStringplaylist = GsonBuilder().create().toJson(Playlistactivity.playlists)
        editor.putString("Playlist" , jsonStringplaylist)
        editor.apply()

        if(musiclistPA[songPosition].id == "Unkonw" && !isPlaying)
            exitApplication()
    }
    private fun initServiceAndPlaylist(playlist: ArrayList<Music>, shuffle: Boolean){
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        musiclistPA = ArrayList()
        musiclistPA.addAll(playlist)
        if(shuffle) musiclistPA.shuffle()
        setLayout()
    }
}
