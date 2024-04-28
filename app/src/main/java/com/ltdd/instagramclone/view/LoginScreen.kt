package com.ltdd.instagramclone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ltdd.instagramclone.databinding.ActivityLoginScreenBinding

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtEmail.editText?.setText(intent.getStringExtra("EMAIL"))
        binding.edtPassword.editText?.setText(intent.getStringExtra("PASSWORD"))

        binding.btnLogin.setOnClickListener {
            //Lay gia tri email ca password
            val email = binding.edtEmail.editText?.text.toString()
            val password = binding.edtPassword.editText?.text.toString()

            //Kiem tra email va password co rong hay khong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Dang nhap
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, HomeScreen::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.btnShowPassword.setOnClickListener {
            binding.edtPassword.editText?.apply {
                //Thay doi inputType
                inputType = if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                } else {
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                }
            }
        }

        binding.btnCreateNewAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountScreen::class.java)
            startActivity(intent)
        }

        binding.txtForgot.setOnClickListener {
            val intent = Intent(this, ForgotPasswordScreen::class.java)
            startActivity(intent)
        }
    }
}