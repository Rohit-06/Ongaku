package com.example.ongaku

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ongaku.databinding.ActivityPlaylistactivityBinding
import com.example.ongaku.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Playlistactivity : AppCompatActivity() {

    private lateinit var adapter: PlaylistViewAdapter

    companion object{
        var playlists :MusicPlaylist = MusicPlaylist()
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlaylistactivityBinding

    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Ongaku)
        supportActionBar?.hide()
         binding = ActivityPlaylistactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbtnPA.setOnClickListener{
            finish()
        }



        // for adapter
        binding.recyclerviewPV.setHasFixedSize(true) // recycler view utne hi object bnaega jitni use jrort h extra nhi bnaega
        binding.recyclerviewPV.setItemViewCacheSize(13)
        binding.recyclerviewPV.layoutManager = LinearLayoutManager(this@Playlistactivity)
        adapter = PlaylistViewAdapter(this , playlist= playlists.ref)
        binding.recyclerviewPV.adapter = adapter

        //for alert dialog when click on add playlist button

        binding.addplaylist.setOnClickListener {
            customAlertDialog() }

    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog , binding.root , false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Your Playlist")
            .setPositiveButton("ADD"){ dialog, _ ->
                val playlistname = binder.playlistname.text
                val owner = binder.playlistowner.text
                 if(playlistname!=null && owner != null)
                 {
                     if(playlistname.isNotEmpty() && owner.isNotEmpty())
                         addPlaylist(playlistname.toString() , owner.toString())
                 }
              dialog.dismiss()
            }
        val cs = builder.create()
        cs.show()
    }

  private fun addPlaylist(name :String , owner :String){
       var isPlaylist = false
      for(i in playlists.ref){
          if(name == i.name){
              isPlaylist = true
              break
          }
      }

      if(isPlaylist)
          Toast.makeText(this , "Playlist Already Exists!!" , Toast.LENGTH_SHORT).show()
      else{
          val tempList = Playlist()
          tempList.name = name
          tempList.createdBy = owner
          tempList.playlist = ArrayList()


          val calendar = Calendar.getInstance().time
          val sdf = SimpleDateFormat("dd MMM yyyy" , Locale.ENGLISH)
          tempList.createdOn = sdf.format(calendar)
          playlists.ref.add(tempList)
          adapter.refreshPlaylist()
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
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()



    }
}

