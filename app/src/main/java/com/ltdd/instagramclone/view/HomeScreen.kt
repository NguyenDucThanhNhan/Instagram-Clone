package com.ltdd.instagramclone.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ltdd.instagramclone.R
import com.ltdd.instagramclone.adapter.StoryAdapter
import com.ltdd.instagramclone.databinding.ActivityHomeScreenBinding

import com.ltdd.instagramclone.model.Notification
import com.ltdd.instagramclone.model.Story


class HomeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("EMAIL")
        //binding.textView.text = email

        binding.btnSearch.setOnClickListener {
            val fragment = searchFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.homescreen, fragment)
                .addToBackStack(null) // optional: add to back stack if you want to be able to navigate back
              .commit()
            binding.fragmentContainerView4.visibility=View.GONE;

        }
        binding.btnHome.setOnClickListener{
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)

        }
        binding.btNotification.setOnClickListener{
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)

        }
        binding.btnPost.setOnClickListener {
            val fragment = Gallery()
            supportFragmentManager.beginTransaction()
                .replace(R.id.homescreen, fragment)

                .addToBackStack(null) // optional: add to back stack if you want to be able to navigate back
                .commit()
            binding.fragmentContainerView4.visibility=View.GONE;
        }
        binding.btnProfile.setOnClickListener{
            val intent = Intent(this, UserProfileScreen::class.java)
            startActivity(intent)
        }


    }





}
