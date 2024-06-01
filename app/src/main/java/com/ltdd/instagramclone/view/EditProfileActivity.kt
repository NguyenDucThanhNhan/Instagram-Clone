package com.ltdd.instagramclone.view

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.ltdd.instagramclone.R
import com.ltdd.instagramclone.model.User
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlin.coroutines.Continuation

class EditProfileActivity : AppCompatActivity() {
    private lateinit var close: ImageView
    private lateinit var imageProfile: ImageView
    private lateinit var save: TextView
    private lateinit var tvChange: TextView
    private lateinit var fullname: EditText
    private lateinit var username: EditText
    private lateinit var bio: EditText
    private lateinit var firebaseUser: FirebaseUser
    private var mImageUri: Uri? = null
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        close = findViewById(R.id.close)
        imageProfile = findViewById(R.id.image_profile)
        save = findViewById(R.id.save)
        tvChange = findViewById(R.id.tv_change)
        fullname = findViewById(R.id.fullname)
        username = findViewById(R.id.username)
        bio = findViewById(R.id.bio)

        // Lấy người dùng hiện tại từ Firebase Authentication
        firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
// Tham chiếu đến Firebase Storage
        storageRef = FirebaseStorage.getInstance().getReference("uploads")
//
// Kiểm tra người dùng hiện tại có tồn tại hay không
        firebaseUser?.let {
            // Tham chiếu đến Firebase Database với đường dẫn tới người dùng hiện tại
            val reference =
                FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Chuyển đổi dữ liệu từ snapshot thành đối tượng User
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        // Cập nhật thông tin người dùng trên giao diện
                        Log.d("UserProfileScreen", "Current user ID: ${user.userid}")
                        fullname.setText(user.name)
                        username.setText(user.username)
                        bio.setText(user.bio)

                        Log.d("UserInfoChange", "ondataChange success: ${dataSnapshot.value}")
                        // Sử dụng Glide để tải và hiển thị ảnh người dùng
                        Glide.with(applicationContext).load(user.imageurl).into(imageProfile)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(
                        "UserProfileScreen",
                        "Failed to retrieve user info: ${databaseError.message}"
                    )
                }
            })
            close.setOnClickListener { // Sử dụng lambda thay cho OnClickListener
                finish() // Kết thúc Activity
            }

            tvChange.setOnClickListener {
                openGallery()
                CropImage.activity()
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this@EditProfileActivity)
            }

            imageProfile.setOnClickListener {
                openGallery()
                CropImage.activity()
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this@EditProfileActivity)
            }
            save.setOnClickListener {
                updateProfile(
                    fullname.text.toString(),
                    username.text.toString(),
                    bio.text.toString()
                )
                finish()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val PICK_IMAGE_REQUEST = 1
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    private fun updateProfile(fullname: String, username: String, bio: String) {
        Log.d("UpdateChangeUserProfile", "Update func open")
        val reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)
        val hashMap = hashMapOf<String, Any>(
            "fullname" to fullname,
            "username" to username,
            "bio" to bio
        )
        reference.updateChildren(hashMap)
        Log.d("UpdateChangeUserProfile", "Update success")
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage() {
        val pd = ProgressDialog(this)
        pd.setMessage("Uploading")
        pd.show()

        if (mImageUri != null) {
            val fileReference =
                storageRef.child("${System.currentTimeMillis()}.${getFileExtension(mImageUri!!)}")

            uploadTask = fileReference.putFile(mImageUri!!)
            uploadTask?.continueWithTask(com.google.android.gms.tasks.Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Unknown error occurred during upload.")
                }
                return@Continuation fileReference.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val myUrl = downloadUri.toString()

                    val reference =
                        FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.uid)

                    val hashMap = hashMapOf<String, Any>(
                        "imageurl" to myUrl
                    )

                    reference.updateChildren(hashMap)
                    pd.dismiss()
                } else {

                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener { e ->
                pd.dismiss()
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            pd.dismiss()
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            mImageUri = result.uri
            uploadImage()
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }
}
