package com.example.ongaku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ongaku.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {

    lateinit var binding:ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Settings"
    }
}