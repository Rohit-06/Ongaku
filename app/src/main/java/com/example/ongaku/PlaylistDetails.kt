package com.example.ongaku

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ongaku.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding : ActivityPlaylistDetailsBinding
        var currentPlaylistpos:Int =  0
        @SuppressLint("StaticFieldLeak")
        lateinit var adapter: PlaylistDetailsAdapter
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
         setContentView(binding.root)
        supportActionBar?.hide()
        currentPlaylistpos = intent.getIntExtra("index" , 0)

       Playlistactivity.playlists.ref[currentPlaylistpos].playlist = checkPlaylist( Playlistactivity.playlists.ref[currentPlaylistpos].playlist)


        context = this
        binding.backbtnPD.setOnClickListener {
            finish()
        }
        binding.recyclerviewPD.setItemViewCacheSize(10)
        binding.recyclerviewPD.setHasFixedSize(true)
        binding.recyclerviewPD.layoutManager = LinearLayoutManager(this)
        adapter = PlaylistDetailsAdapter(this , Playlistactivity.playlists.ref[currentPlaylistpos].playlist )
        binding.recyclerviewPD.adapter = adapter

        updatePlaylistDetails()


        binding.shufflebtn.setOnClickListener{
            Toast.makeText(this , "Shuffle btn is clicked"  , Toast.LENGTH_SHORT).show()
            val intent  =  Intent(this , PlayerActivity::class.java)
            intent.putExtra("index" , 0)
            intent.putExtra("class" , "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.add.setOnClickListener {
            val intent = Intent(this , SelectionActivity::class.java)
            startActivity(intent)
        }
        binding.remove.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to delete all the songs from the playlist")
                .setPositiveButton("Yes")
                { dialog, _ ->
                    Playlistactivity.playlists.ref[currentPlaylistpos].playlist.clear()
                    adapter.updatePlaylist()
                    dialog.dismiss()
                } //.setPositiveButton(resources.getString(R.string.accept)) { dialog, which -> // Respond to positive button press }
                .setNegativeButton("No"){dialog , _->
                    dialog.dismiss()
                }

            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.DKGRAY)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.DKGRAY)
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.CYAN)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.CYAN)
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
    }

}