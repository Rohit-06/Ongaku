package com.example.ongaku

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ongaku.databinding.ActivityFavactivityBinding
import com.google.gson.GsonBuilder

// for persistent data storage we will use shared preference but shared preference can only use to save primitve data type so for this we will use shared prefence with json
class Favactivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavactivityBinding
    private lateinit var adapter: FavAdapter

    companion object{
        var favSongs : ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Ongaku)
        supportActionBar?.hide()
        binding = ActivityFavactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // this is to check if usser has removed any file from the system or not
     //  favSongs = checkPlaylist(favSongs)

        binding.backbtnFA.setOnClickListener{
            finish()
        }
        binding.shufflebtnFA.setOnClickListener {
            Toast.makeText(this , "Shuffle btn is clicked"  , Toast.LENGTH_SHORT).show()
            val intent  =  Intent(this , PlayerActivity::class.java)
            intent.putExtra("index" , 0)
            intent.putExtra("class" , "FavShuffle")
            startActivity(intent)
        }
        binding.recyclerviewFV.setHasFixedSize(true) // recycler view utne hi object bnaega jitni use jrort h extra nhi bnaega
        binding.recyclerviewFV.setItemViewCacheSize(13)
        binding.recyclerviewFV.layoutManager = LinearLayoutManager(this)
        adapter = FavAdapter(this , favSongs)
        binding.recyclerviewFV.adapter = adapter

        if(favSongs.size<1)
            binding.shufflebtnFA.visibility = GONE



    }

    override fun onDestroy() {
        super.onDestroy()
        // for storing favourites and playlist data using shared preferences

        val editor = getSharedPreferences("FAVOURITES" , MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(favSongs)
        editor.putString("FavSongs" , jsonString)
        val jsonStringplaylist = GsonBuilder().create().toJson(Playlistactivity.playlists)
        editor.putString("Playlist" , jsonStringplaylist)
        editor.apply()
    }
}