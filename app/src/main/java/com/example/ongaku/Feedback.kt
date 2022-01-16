package com.example.ongaku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ongaku.databinding.ActivityFeedbackBinding

class Feedback : AppCompatActivity() {
    lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Feedback"
    }
}