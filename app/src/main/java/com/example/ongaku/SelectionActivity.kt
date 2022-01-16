package com.example.ongaku

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ongaku.databinding.ActivitySelectionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.backbtnSA.setOnClickListener {
            finish()
        }
        //binding.recyclerviewSA.setItemViewCacheSize(10)
        //binding.recyclerviewSA.setHasFixedSize(true)
        binding.recyclerviewSA.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, MainActivity.MusicListMA, selectionActivity = true)
        binding.recyclerviewSA.adapter = adapter

        binding.add.setOnClickListener {
           //PlaylistDetails()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Add")
                .setMessage("Do you want to Add these songs in the Playlist")
                .setPositiveButton("Yes")
                { dialog, _ ->
                    adapter.addsong(adapter.playlistSongs)
                    PlaylistDetails.adapter.notifyDataSetChanged()
                    updatePlaylistDetails()
                    dialog.dismiss()
                    finish()
                } //.setPositiveButton(resources.getString(R.string.accept)) { dialog, which -> // Respond to positive button press }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.DKGRAY)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.DKGRAY)
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.CYAN)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.CYAN)


        }

        binding.serachviewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicsearchlist = ArrayList()
                if(newText != null)
                {
                    val userInput = newText.lowercase()
                    for(song in MainActivity.MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.musicsearchlist.add(song)

                    MainActivity.search = true
                    adapter.updateList(serachList = MainActivity.musicsearchlist)
                }
                return true
            }
    })
    }
}


