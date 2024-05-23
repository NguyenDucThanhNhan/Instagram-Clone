package com.ltdd.instagramclone.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ltdd.instagramclone.R
import com.ltdd.instagramclone.databinding.ActivityHomeScreenBinding

class HomeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("EMAIL")
        //binding.textView.text = email


    }
}