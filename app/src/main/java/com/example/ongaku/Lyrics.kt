package com.example.ongaku


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.ongaku.databinding.ActivityLyricsBinding
import org.json.JSONException


class Lyrics : AppCompatActivity() {

   companion object
   {
       @SuppressLint("StaticFieldLeak")
       lateinit var binding: ActivityLyricsBinding
   }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLyricsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Lyricsofthesong.movementMethod = ScrollingMovementMethod()

        val url = "https://api.lyrics.ovh/v1/" + PlayerActivity.musiclistPA[PlayerActivity.songPosition].artist +"/"+PlayerActivity.musiclistPA[PlayerActivity.songPosition].title
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    binding.Lyricsofthesong.text = response.getString("lyrics")
                }
                catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            {
                binding.Lyricsofthesong.text = "Sorry Not able to find the lyrics"

            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


    }




}