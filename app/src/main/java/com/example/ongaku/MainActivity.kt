package com.example.ongaku

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ongaku.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding :ActivityMainBinding
    private lateinit var toogle:ActionBarDrawerToggle
    private lateinit var adapter: MusicAdapter

    companion object{
        lateinit var MusicListMA:ArrayList<Music>
        lateinit var musicsearchlist : ArrayList<Music>
        var search:Boolean = false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Ongaku)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

         // for navigation drawer

        toogle = ActionBarDrawerToggle(this ,binding.root , R.string.open , R.string.close)
        binding.root.addDrawerListener(toogle)
        toogle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(requestRuntimePermission())
        {
            setLayout()

            // for retrieving favourites data using shared preferences
            Favactivity.favSongs = ArrayList()
            val editor = getSharedPreferences("FAVOURITES" , MODE_PRIVATE)
            val jsonString = editor.getString("FavSongs" , null)
            val typeToken = object  : TypeToken<ArrayList<Music>>(){}.type
            if(jsonString != null)
            {
                val data:ArrayList<Music> = GsonBuilder().create().fromJson(jsonString , typeToken)
                Favactivity.favSongs.addAll(data)
            }

            // for retrieving playlist data using shared preferences
            Playlistactivity.playlists = MusicPlaylist()
            val jsonStringplaylist = editor.getString("Playlist" , null)
            val typeTokenplaylist = object  : TypeToken<MusicPlaylist>(){}.type
            if(jsonStringplaylist != null)
            {
                val dataPlaylist:MusicPlaylist = GsonBuilder().create().fromJson(jsonStringplaylist , typeTokenplaylist)
                Playlistactivity.playlists = dataPlaylist
            }

        }

        binding.shufflebtn.setOnClickListener {
            val intent = Intent(this , PlayerActivity::class.java)
            intent.putExtra("class" , "MainActivity")

            startActivity(intent)
        }
        binding.searchlyrics.setOnClickListener {
            val intent = Intent(this , searchLyrics::class.java)
            startActivity(intent)
        }
        binding.playlistbtn.setOnClickListener {
            Toast.makeText(this , "Playlist btn is clicked"  , Toast.LENGTH_SHORT).show()
            val intent  =  Intent(this , Playlistactivity::class.java)
            startActivity(intent)
        }
        binding.favbtn.setOnClickListener {


            Toast.makeText(this , "Fav btn is clicked"  , Toast.LENGTH_SHORT).show()
            val intent  =  Intent(this ,  Favactivity::class.java)
            startActivity(intent)
        }


        // for navigation view
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId)
            {
                R.id.feedback -> {Toast.makeText(this , "Feedback" , Toast.LENGTH_SHORT).show()
                startActivity(Intent(this , Feedback::class.java))
                }
                R.id.setting ->{ Toast.makeText(this , "Settings" , Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this , Settings::class.java))
                }
                R.id.about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this ,About::class.java))

                }
                // ALERT DIALOG
                R.id.exit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close the app?")
                        .setPositiveButton("Yes")
                        { _, _ ->
                           exitApplication()

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
            true
        }


    }
 // For requesting permission

    private fun requestRuntimePermission():Boolean
    {
        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this , arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE) , 13)
                return false
           }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if(requestCode == 13)
         {
             if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
             {
                 Toast.makeText(this , "Permission Granted" , Toast.LENGTH_SHORT).show()
                 setLayout()
             }

             else
                 ActivityCompat.requestPermissions(this , arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE) , 13)
         }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toogle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }
    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.R)
    fun getAllAudio():ArrayList<Music>
    {
        val templist = ArrayList<Music>()

        // storage me se kuch bhi access krne ke liye apni help krta h cursor

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0" // selection btate h kis type ki file chahiye
        val projection  = arrayOf(MediaStore.Audio.Media._ID , MediaStore.Audio.Media.TITLE , MediaStore.Audio.Media.ALBUM , MediaStore.Audio.Media.ARTIST , MediaStore.Audio.Media.DURATION , MediaStore.Audio.Media.DATE_ADDED , MediaStore.Audio.Media.DATA , MediaStore.Audio.Media.ALBUM_ID)

        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null,
            MediaStore.Audio.Media.DATE_ADDED +  " DESC", null)

        if(cursor!=null)
        {

            if(cursor.moveToFirst())
                do{
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val albumIdC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val arturic = Uri.withAppendedPath(uri , albumIdC).toString()
                    val music = Music(id = idC , title = titleC , album = albumC , artist = artistC , duration = durationC , path = pathC , arturi = arturic)
                    val file = File(music.path)
                    if(file.exists())
                        templist.add(music)

                }while (cursor.moveToNext())
                cursor.close()
        }

        return templist
    }
  @SuppressLint("SetTextI18n")
  @RequiresApi(Build.VERSION_CODES.R)
  private fun setLayout()
  {
      search = false
      MusicListMA = getAllAudio()

      binding.recycler.setHasFixedSize(true) // recycler view utne hi object bnaega jitni use jrort h extra nhi bnaega
      binding.recycler.setItemViewCacheSize(13)
      binding.recycler.layoutManager = LinearLayoutManager(this)
      adapter = MusicAdapter(this , MusicListMA)
      binding.recycler.adapter = adapter
      binding.totalsongs.text = "Total Songs : "+adapter.itemCount
  }

    override fun onDestroy()
    {
        super.onDestroy()

        if(PlayerActivity.musicService!=null)
            exitApplication()


    }

    override fun onResume() {
        super.onResume()
        // for storing favourites and playlist data using shared preferences

        val editor = getSharedPreferences("FAVOURITES" , MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(Favactivity.favSongs)
        editor.putString("FavSongs" , jsonString)
        val jsonStringplaylist = GsonBuilder().create().toJson(Playlistactivity.playlists)
        editor.putString("Playlist" , jsonStringplaylist)
        editor.apply()
    }
    // music serach

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.serach_view_menu , menu)
        val serachView = menu?.findItem(R.id.searchview)?.actionView as SearchView
        serachView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                musicsearchlist = ArrayList()
                 if(newText != null)
                 {
                     val userInput = newText.lowercase()
                     for(song in MusicListMA)
                         if(song.title.lowercase().contains(userInput))
                             musicsearchlist.add(song)

                     search = true
                     adapter.updateList(serachList = musicsearchlist)
                 }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }



}