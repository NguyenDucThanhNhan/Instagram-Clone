package com.ltdd.instagramclone.view
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ltdd.instagramclone.R
import com.ltdd.instagramclone.adapter.PhotosPostAdapter
import com.ltdd.instagramclone.databinding.ActivityUserProfileScreenBinding
import com.ltdd.instagramclone.model.Photo
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
    private lateinit var changeImage: ImageButton
    private lateinit var myFotos: ImageButton
    private lateinit var postButton: ImageButton
    private lateinit var moreOptions: ImageButton
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var photosPostAdapter: PhotosPostAdapter
    private lateinit var postList: ArrayList<Photo>

    private lateinit var binding: ActivityUserProfileScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeScreen::class.java))
                    true
                }
                R.id.search -> {
                    val searchFragment = searchFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.user_profile_fragment, searchFragment)
                        .addToBackStack(null)
                        .commit()
                    binding.fragmentContainerView5.visibility=View.GONE;
                    true
                }
                R.id.post -> {
                    val fragment = Gallery()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.user_profile_fragment, fragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.reels -> {
                    // startActivity(Intent(this, ReelsActivity::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, UserProfileScreen::class.java))
                    true
                }
                else -> false
            }
        }



        try {
            imageProfile = findViewById(R.id.profile_image)

            options = findViewById(R.id.more_button)

            posts = findViewById(R.id.Post_count)

            postButton= findViewById(R.id.post_button)

            followers = findViewById(R.id.followers_count)

            following = findViewById(R.id.following_count)

            fullname = findViewById(R.id.username)

            bio = findViewById(R.id.bio)

            editProfile = findViewById(R.id.edit_profile)

            myFotos = findViewById(R.id.my_photos)

            moreOptions = findViewById(R.id.more_button)
            changeImage = findViewById(R.id.change_profile_image_button)
            recyclerView = findViewById(R.id.posts_recycler_view)
            recyclerView.setHasFixedSize(true)
            val linearLayoutManager = GridLayoutManager(this, 3)
            recyclerView.layoutManager = linearLayoutManager
            postList = ArrayList()
            photosPostAdapter = PhotosPostAdapter(this, postList)
            recyclerView.adapter = photosPostAdapter


            // Nhận userID từ Intent
            val userID = intent.getStringExtra("userID")
            if (userID != null) {
                // Sử dụng userID để hiển thị thông tin người dùng
                profileId = userID
            } else {
                // Nếu không có userID, sử dụng ID của người dùng hiện tại
                profileId = firebaseUser.uid
            }

            // Debug log
            Log.d("UserProfileScreen", "Current user ID: $profileId")

            changeImage.setOnClickListener{
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            }
            postButton.setOnClickListener{
                val intent = Intent(this, NextActivity::class.java)
                startActivity(intent)
            }

            editProfile.setOnClickListener {

                val btn = editProfile.text.toString()

                when (btn) {

                    "Chỉnh sửa trang cá nhân" -> {

                        val intent = Intent(this, EditProfileActivity::class.java)
                        startActivity(intent)

                    }

                    "follow" -> {

                        FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)

                            .child("following").child(profileId).setValue(true)

                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)

                            .child("followers").child(firebaseUser.uid).setValue(true)
                        editProfile.text = "Following"

                    }

                    "following" -> {

                        FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)

                            .child("following").child(profileId).removeValue()

                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)

                            .child("followers").child(firebaseUser.uid).removeValue()
                        editProfile.text = "Follow"
                    }

                }

            }
            options.setOnClickListener { view ->
                showPopupMenu(view)
            }

            fetchAndDisplayUserInfo()

            getFollowersAndFollowing()

            getNrPosts()
            myPhotos()

        } catch (e: Exception) {

            Log.e("UserProfileScreen", "Error in onCreate: ${e.message}")

        }
        if (profileId == firebaseUser.uid) {
            editProfile.text = "Chỉnh sửa trang cá nhân"
        } else {
            checkFollow()
        }
    }
    private fun userInfo(userid: String) {
        Log.d("UserInfo", "run success")

        // Đảm bảo bạn sử dụng URL đúng
        val database = FirebaseDatabase.getInstance("https://instagram-clone-f77f1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val reference = database.getReference("User").child(userid) // Thay thế "userid" bằng ID thực tế
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

    private fun getCurrentUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }
    private fun fetchAndDisplayUserInfo() {
        val userId = getCurrentUserId()
        Log.d("UserProfileScreen", "Current user ID: $profileId") // Thêm dòng log này để kiểm tra userId
        if (userId != null) {
            userInfo(profileId)
        } else {
            Log.d("UserProfileScreen", "User not logged in")
        }
    }
    private fun checkFollow() {
        val reference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser.uid).child("following").child(profileId)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Đã theo dõi, hiển thị "Following"
                    editProfile.text = "following"
                } else {
                    // Chưa theo dõi, hiển thị "Follow"
                    editProfile.text = "follow"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý lỗi nếu có
                Log.d("UserProfileScreen", "Failed to check follow status: ${databaseError.message}")
            }
        })
    }
    private fun getFollowersAndFollowing() {
        Log.d("UserProfileScreen Follow", "run success")
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
        val reference = FirebaseDatabase.getInstance().getReference("user_photos").child(profileId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var postCount = 0
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Photo::class.java)
                    if (post?.user_id == profileId) {
                        postCount++
                    }
                }
                posts.text = postCount.toString()
                Log.d("UserProfilePost", "get success:")
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserProfileScreen", "Failed to get number of posts: ${databaseError.message}")
            }
        })
    }
    private fun myPhotos() {
        val reference = FirebaseDatabase.getInstance().getReference("user_photos").child(profileId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for (snapshot in dataSnapshot.children) {
                    val photo = snapshot.getValue(Photo::class.java)
                    if (photo?.user_id == profileId) {  // Thay đổi từ post?.user_id thành photo?.userId
                        postList.add(photo)
                    }
                }
                postList.reverse()
                photosPostAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_profile_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    // Thực hiện chức năng đăng xuất
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginScreen::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}
