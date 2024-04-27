package com.ltdd.instagramclone.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ltdd.instagramclone.databinding.ActivityCreateAccountScreenBinding
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class CreateAccountScreen : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountScreenBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Chuan bi
        var verifyCode = 1496
        binding.edtCode.visibility = View.GONE
        binding.edtNewPassword.visibility = View.GONE
        binding.edtConfirmPassword.visibility = View.GONE
        binding.btnEnter.text = "Send code"

        //Kiem tra xem dang o buoc Send code hay Verify code
        binding.btnEnter.setOnClickListener {
            if (binding.btnEnter.text == "Send code") {
                val email = binding.edtEmail.editText?.text.toString()
                if (isEmailValid(email)) {
                    //Chuyen doi trang thai
                    binding.btnEnter.text = "Confirm"
                    binding.edtCode.visibility = View.VISIBLE
                    binding.edtNewPassword.visibility = View.VISIBLE
                    binding.edtConfirmPassword.visibility = View.VISIBLE

                    //Tao ma xac nhan va gui email
                    verifyCode = (1000..9999).random()
                    sendEmailRegisterAccount(email, verifyCode)
                } else {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (binding.edtCode.editText?.text.toString() == verifyCode.toString()){
                    //Tao tai khoan
                    val email = binding.edtEmail.editText?.text.toString()
                    val password = binding.edtNewPassword.editText?.text.toString()
                    val confirmPassword = binding.edtConfirmPassword.editText?.text.toString()
                    if (isEmailValid(email) && isPasswordValid(password) && password == confirmPassword) {
                        createAccount(email, password)
                        //Reset
                        binding.btnEnter.text = "Send code"
                        binding.edtCode.visibility = View.GONE
                        binding.edtNewPassword.visibility = View.GONE
                        binding.edtConfirmPassword.visibility = View.GONE

                        val intent = Intent(this, LoginScreen::class.java)
                        intent.putExtra("EMAIL", email)
                        intent.putExtra("PASSWORD", password)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Invalid value, password at least 6 characters", Toast.LENGTH_LONG).show()
                    }
                }

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

    private fun isPasswordValid(password: String): Boolean {
        val minLength = 6
        return password.length >= minLength
    }

    private fun createAccount(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Tài khoản đã được tạo thành công.")
                } else {
                    println("Lỗi: ${task.exception?.message}")
                }
            }

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

    private fun sendEmailRegisterAccount(email: String, verifyCode: Int) {
        val stringSenderEmail = "ndtnhan2003@gmail.com"
        val stringPasswordSenderEmail = "uaozzauifrkyougs"

        val stringHost = "smtp.gmail.com"

        val properties = System.getProperties()

        properties["mail.smtp.host"] = stringHost
        properties["mail.smtp.port"] = "465"
        properties["mail.smtp.ssl.enable"] = "true"
        properties["mail.smtp.auth"] = "true"

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail)
            }
        })

        val mimeMessage = MimeMessage(session)
        mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(email))

        mimeMessage.subject = "Instagram Account Registration Confirmation Code"
        mimeMessage.setText("Hello,\n" +
                "\n" +
                "We have received your request to register a new Instagram account. Below is the 4-digit confirmation code to complete the registration process:\n" +
                "\n" +
                "Confirmation Code: $verifyCode\n" +
                "\n" +
                "Please use this code to finalize the registration of your account on the Instagram app. Do not share this code with anyone to protect your personal information.\n" +
                "\n" +
                "If you have any questions or need assistance, feel free to contact us.\n" +
                "\n" +
                "We look forward to seeing you on Instagram!\n" +
                "\n" +
                "Best regards,\n" +
                "Instagram Support Team")

        val thread = Thread {
            try {
                Transport.send(mimeMessage)
            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}