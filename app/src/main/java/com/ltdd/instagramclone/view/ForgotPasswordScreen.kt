package com.ltdd.instagramclone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ltdd.instagramclone.R
import com.ltdd.instagramclone.databinding.ActivityForgotPasswordScreenBinding

class ForgotPasswordScreen : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtNotice.visibility = View.GONE
        binding.btnSend.setOnClickListener {
            val email = binding.edtEmail.editText?.text.toString()
            if (isEmailValid(email) && email.isNotEmpty()){
                sendEmailResetPassword(email)
                binding.txtNotice.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Invalid email.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun sendEmailResetPassword(email: String) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Email reset password đã được gửi thành công.")
                } else {
                    println("Lỗi: ${task.exception?.message}")
                }
            }
    }
}