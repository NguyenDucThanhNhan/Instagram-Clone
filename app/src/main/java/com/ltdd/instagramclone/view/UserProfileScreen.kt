package com.ltdd.instagramclone.view
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ltdd.instagramclone.R
import com.ltdd.instagramclone.model.Post
import com.ltdd.instagramclone.model.User
class UserProfileScreen : AppCompatActivity() {
    private lateinit var imageProfile: ImageView
    private lateinit var options: ImageView
    private lateinit var posts: TextView
    private lateinit var followers: TextView
    private lateinit var following: TextView
    private lateinit var fullname: TextView
    private lateinit var bio: TextView
    private lateinit var editProfile: Button
    private lateinit var myFotos: ImageButton
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("UserProfileScreen", "onCreate started")

        enableEdgeToEdge()

        setContentView(R.layout.activity_user_profile_screen)



        try {
            imageProfile = findViewById(R.id.profile_image)

            options = findViewById(R.id.more_button)

            posts = findViewById(R.id.posts_count)

            followers = findViewById(R.id.followers_count)

            following = findViewById(R.id.following_count)

            fullname = findViewById(R.id.username)

            bio = findViewById(R.id.bio)

            editProfile = findViewById(R.id.edit_profile)

            myFotos = findViewById(R.id.my_photos)

            Log.d("UserProfileScreen", "Before initializing firebaseUser")

            firebaseUser = FirebaseAuth.getInstance().currentUser!!

            Log.d("UserProfileScreen", "Before initializing profileId")

            val prefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE)

            profileId = prefs.getString("profileid", "none") ?: "none"

            Log.d("UserProfileScreen", "Before initializing views")



            editProfile.setOnClickListener {

                val btn = editProfile.text.toString()

                when (btn) {

                    "Edit Profile" -> {

                        // go to EditProfile

                    }

                    "follow" -> {

                        FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)

                            .child("following").child(profileId).setValue(true)

                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)

                            .child("followers").child(firebaseUser.uid).setValue(true)

                    }

                    "following" -> {

                        FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)

                            .child("following").child(profileId).removeValue()

                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)

                            .child("followers").child(firebaseUser.uid).removeValue()

                    }

                }

            }

            userInfo()

            getFollowersAndFollowing()

            getNrPosts()

        } catch (e: Exception) {

            Log.e("UserProfileScreen", "Error in onCreate: ${e.message}")

        }
        if (profileId == firebaseUser.uid) {
            editProfile.text = "Edit Profile"
        } else {
            checkFollow()
        }
    }
    private fun userInfo() {
        Log.d("UserInfo", "run success")

        // Đảm bảo bạn sử dụng URL đúng
        val database = FirebaseDatabase.getInstance("https://instagram-clone-f77f1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val reference = database.getReference("User").child("userid") // Thay thế "userid" bằng ID thực tế
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("UserInfo", "ondataChange success: ${dataSnapshot.value}")
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    Glide.with(this@UserProfileScreen).load(it.imageurl).into(imageProfile)
                    Log.d("UserInfo", "name success")
                    fullname.text = it.username
                    Log.d("UserInfo", "bio success")
                    bio.text = it.bio
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserProfileScreen", "Failed to retrieve user info: ${databaseError.message}")
            }
        })
    }

    private fun checkFollow() {
        val reference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser.uid).child("following")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(profileId).exists()) {
                    editProfile.tag = "following"
                } else {
                    editProfile.tag = "follow"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserProfileScreen", "Failed to check follow status: ${databaseError.message}")
            }
        })
    }
    private fun getFollowersAndFollowing() {
        val followersReference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId).child("followers")
        followersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followers.text = dataSnapshot.childrenCount.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserProfileScreen", "Failed to get followers count: ${databaseError.message}")
            }
        })
        val followingReference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId).child("following")
        followingReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                following.text = dataSnapshot.childrenCount.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserProfileScreen", "Failed to get following count: ${databaseError.message}")
            }
        })
    }
    private fun getNrPosts() {
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var postCount = 0
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    if (post?.postPublisherId == profileId) {
                        postCount++
                    }
                }
                posts.text = postCount.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserProfileScreen", "Failed to get number of posts: ${databaseError.message}")
            }
        })
    }
}
