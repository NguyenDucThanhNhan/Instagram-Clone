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
<<<<<<< HEAD

        val email = intent.getStringExtra("EMAIL")
        //binding.textView.text = email


=======
        binding.btnSearch.setOnClickListener {
            // Create an instance of SearchFragment
            val searchFragment = searchFragment()
            // Get the FragmentManager and start a transaction
            val transaction = supportFragmentManager.beginTransaction()
            // Replace the content with the new fragment
            transaction.replace(R.id.homescreen, searchFragment);
            // Optionally add the transaction to the back stack so the user can navigate back
            transaction.addToBackStack(null)
            // Commit the transaction
            transaction.commit()
        }
>>>>>>> origin/main
    }


}
