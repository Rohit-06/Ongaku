package com.example.ongaku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ongaku.databinding.ActivityAboutBinding

class About : AppCompatActivity() {
    lateinit var binding:ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "About"
    }
}