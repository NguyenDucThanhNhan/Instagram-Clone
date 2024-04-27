package com.ltdd.instagramclone.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ltdd.instagramclone.databinding.ActivityForgotPasswordScreenBinding
import java.util.*

class ForgotPasswordScreen : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordScreenBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtCode.visibility = View.GONE
        val email = binding.edtEmail.editText?.text.toString()

        //Kiem tra xem dang o buoc Send code hay Verify code
        binding.btnEnter.setOnClickListener {
            if (binding.btnEnter.text == "Send code") {
                binding.btnEnter.text = "Verify code"
                binding.edtCode.visibility = View.VISIBLE
                sendEmail(email)
            } else {
                binding.btnEnter.text = "Send code"
                binding.edtCode.visibility = View.GONE
            }
        }
    }

    private fun sendEmail(email: String) {

    }
}