package com.example.ongaku

//import android.support.v7.graphics.Palette
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ongaku.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {
     companion object{
         @SuppressLint("StaticFieldLeak")
         lateinit var binding:FragmentNowPlayingBinding
     }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view) // binding initialize
        binding.root.visibility = View.GONE // root invisible (not neseccary that when we open app song is playing)
        binding.playpauseNP.setOnClickListener{
            if(PlayerActivity.isPlaying)
                pauseMusic()
            else
                playMusic()
        }

        binding.nextNP.setOnClickListener{
            setNextSongPosition()
            PlayerActivity.musicService!!.createMediaPlayer()

            // for now playing fragment
            Glide.with(this)
                .load(PlayerActivity.musiclistPA[PlayerActivity.songPosition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
                .into(binding.songimgNP)

            binding.songnameNP.text = PlayerActivity.musiclistPA[PlayerActivity.songPosition].title

            PlayerActivity.fIndex = favChecker(PlayerActivity.currentsongid)
            if(PlayerActivity.isFav)
                PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_24)
            else
                PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_border_24)


            // for now playing fragment color
            val imgArt = getImgArt(PlayerActivity.musiclistPA[PlayerActivity.songPosition].path)
            val img = if(imgArt!=null)
            {
                BitmapFactory.decodeByteArray(imgArt , 0 , imgArt.size)
            }
            else
            {
                BitmapFactory.decodeResource(resources, R.drawable.music_album)
            }

           updatePlayerBar(img)

            playMusic()
        }
        return view
    }

    override fun onResume() {
        super.onResume()

        if(PlayerActivity.musicService!=null)
        {
            binding.root.visibility = View.VISIBLE
            Glide.with(this)
                .load(PlayerActivity.musiclistPA[PlayerActivity.songPosition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_album)).centerCrop()
                .into(binding.songimgNP)
            binding.songnameNP.text = PlayerActivity.musiclistPA[PlayerActivity.songPosition].title

            // for update color of now playing
            val imgArt = getImgArt(PlayerActivity.musiclistPA[PlayerActivity.songPosition].path)
            val img = if(imgArt!=null)
            {
                BitmapFactory.decodeByteArray(imgArt , 0 , imgArt.size)
            }
            else
            {
                BitmapFactory.decodeResource(resources, R.drawable.music_album)
            }



            // to set the play pause btn
            if(PlayerActivity.isPlaying)
                binding.playpauseNP.setImageResource(R.drawable.ic_baseline_pause_24)
            else
                binding.playpauseNP.setImageResource(R.drawable.ic_baseline_play_arrow_24)

            // for marquee text

            binding.songnameNP.isSelected = true

            binding.root.setOnClickListener {
                val intent = Intent(requireContext() , PlayerActivity::class.java)
                intent.putExtra("index" , PlayerActivity.songPosition)
                intent.putExtra("class" , "Now Playing")

                ContextCompat.startActivity(requireContext() , intent , null)
            }
        }

    }
    private fun playMusic() {
        binding.playpauseNP.setImageResource(R.drawable.ic_baseline_pause_24)
        PlayerActivity.isPlaying = true
        PlayerActivity.binding.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        PlayerActivity.musicService!!.mediaPlayer!!.start()

        PlayerActivity.fIndex = favChecker(PlayerActivity.currentsongid)
        if(PlayerActivity.isFav)
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_24)
        else
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24 , R.drawable.ic_baseline_favorite_border_24)
    }

    private fun pauseMusic() {
        binding.playpauseNP.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        PlayerActivity.isPlaying = false
        PlayerActivity.binding.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        PlayerActivity.musicService!!.mediaPlayer!!.pause()

        PlayerActivity.fIndex = favChecker(PlayerActivity.currentsongid)
        if(PlayerActivity.isFav)
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24 , R.drawable.ic_baseline_favorite_24)
        else
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24 , R.drawable.ic_baseline_favorite_border_24)
    }




}