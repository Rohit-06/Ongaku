package com.example.ongaku

import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View.GONE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.ongaku.databinding.ActivitySearchLyricsBinding
import com.example.ongaku.databinding.SearchLyricsDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONException

class searchLyrics : AppCompatActivity() {
    private lateinit var binding: ActivitySearchLyricsBinding
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchLyricsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.songlyrics.movementMethod = ScrollingMovementMethod()

        binding.searchLyrics.setOnClickListener{
            customAlertDialog()

            }
        }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.search_lyrics_dialog , binding.root , false)
        val binder = SearchLyricsDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Lyrics")
                .setPositiveButton("Search"){ dialog, _ ->
                val artistname = binder.artist.text
                val trackname = binder.track.text
                if(artistname!=null && trackname != null)
                {
                    if(artistname.isNotEmpty() && trackname.isNotEmpty()){
                        val url = "https://api.lyrics.ovh/v1/" + artistname +"/"+ trackname
                        search(url)
                    }

                }
                dialog.dismiss()
            }
        val cs = builder.create()
        cs.show()
    }

    fun search(url:String)
    {
        binding.pb.isVisible = true
     val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            try {
                binding.pb.visibility = GONE
                binding.songlyrics.text = response.getString("lyrics")

            }
            catch (e: JSONException){
                e.printStackTrace()
            }
        },
        {
            binding.pb.visibility = GONE
            binding.songlyrics.text = "Sorry Not able to find the lyrics.\n Search for another song"

        }
    )
    MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
}

}
