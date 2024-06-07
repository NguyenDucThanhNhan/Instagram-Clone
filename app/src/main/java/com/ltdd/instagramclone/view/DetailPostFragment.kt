package com.ltdd.instagramclone.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ltdd.instagramclone.R
import com.ltdd.instagramclone.Utils.TimeUtils
import com.ltdd.instagramclone.model.Like
import com.ltdd.instagramclone.model.Photo
import com.ltdd.instagramclone.model.User
import com.squareup.picasso.Picasso

class DetailPostFragment : Fragment() {
    private var image_profile: ImageView? = null
    private var post_image: ImageView? = null
    private var image_heart: ImageView? = null
    private var speech_bubble: ImageView? = null
    private var image_profile2: ImageView? = null
    private var username: TextView? = null
    private var tv_comment: TextView? = null
    private var likes: TextView? = null
    private var comments: TextView? = null
    private var tag: TextView? = null
    private var caption: TextView? = null
    private var time: TextView? = null
    private var firebaseUser: FirebaseUser? = null
    private var postid: String? = null
    private var publisherid: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_post, container, false)
        image_profile = view.findViewById(R.id.image_profile)
        post_image = view.findViewById(R.id.post_image)
        username = view.findViewById(R.id.username)
        tv_comment = view.findViewById(R.id.tv_comment)
        likes = view.findViewById(R.id.likes)
        comments = view.findViewById(R.id.comments)
        tag = view.findViewById(R.id.tag)
        caption = view.findViewById(R.id.caption)
        time = view.findViewById(R.id.time_posted)
        image_heart = view.findViewById(R.id.image_heart)
        speech_bubble = view.findViewById(R.id.speech_bubble)
        image_profile2 = view.findViewById(R.id.image_profile2)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val bundle = arguments
        if (bundle != null) {
            postid = bundle.getString("postid")
            publisherid = bundle.getString("publisherid")
            loadPostDetails(postid, publisherid)
        }
        return view
    }

    private fun loadPostDetails(postid: String?, publisherid: String?) {
        val postRef = FirebaseDatabase.getInstance().getReference("photos").child(
            postid!!
        )
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val photo = snapshot.getValue(
                    Photo::class.java
                )
                if (photo != null) {
                    Picasso.get().load(photo.image_path).into(post_image)
                    caption!!.text = photo.caption
                    time!!.text = TimeUtils.getTimeAgo(photo.date_created)
                    if (photo.tags == "") {
                        tag!!.visibility = View.GONE
                    } else {
                        tag!!.visibility = View.VISIBLE
                        tag!!.text = photo.tags
                    }
                    publisherInfo(publisherid)
                    isLikes(postid, image_heart)
                    nrLikes(likes, postid)
                    getComments(postid, comments)
                    post_image!!.setOnClickListener { v: View? ->
                        openDetailFragment(
                            postid,
                            publisherid
                        )
                    }
                    tv_comment!!.setOnClickListener { v: View? ->
                        openCommentsActivity(
                            postid,
                            publisherid
                        )
                    }
                    comments!!.setOnClickListener { v: View? ->
                        openCommentsActivity(
                            postid,
                            publisherid
                        )
                    }
                    speech_bubble!!.setOnClickListener { v: View? ->
                        openCommentsActivity(
                            postid,
                            publisherid
                        )
                    }
                    likes!!.setOnClickListener { v: View? ->
                        openLikesActivity(
                            postid
                        )
                    }
                    image_heart!!.setOnClickListener { view: View? ->
                        if (image_heart!!.tag == "like") {
                            addLike(postid)
                        } else {
                            removeLike(postid)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun openDetailFragment(postid: String?, publisherid: String?) {
        val bundle = Bundle()
        bundle.putString("postid", postid)
        bundle.putString("publisherid", publisherid)
        val detailPostFragment = DetailPostFragment()
        detailPostFragment.setArguments(bundle)
        requireFragmentManager().beginTransaction()
            .replace(R.id.homescreen, detailPostFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openCommentsActivity(postid: String?, publisherid: String?) {
        val intent = Intent(context, CommentsActivity::class.java)
        intent.putExtra("postid", postid)
        intent.putExtra("publisherid", publisherid)
        startActivity(intent)
    }

    private fun openLikesActivity(postid: String?) {
        val intent = Intent(context, List_likeActivity::class.java)
        intent.putExtra("postid", postid)
        startActivity(intent)
    }

    private fun addLike(postid: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("Likes").child(
            postid!!
        )
        val hashMap = HashMap<String, Any>()
        hashMap["UserID_like"] = firebaseUser!!.uid
        reference.push().setValue(hashMap)
    }

    private fun removeLike(postid: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("Likes").child(
            postid!!
        )
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.getChildren()) {
                    val like = dataSnapshot.getValue(Like::class.java)
                    if (like != null && like.userID_like == firebaseUser!!.uid) {
                        dataSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getComments(postid: String?, comments: TextView?) {
        val reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(
            postid!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                comments!!.text = "View All " + dataSnapshot.childrenCount + " Comments"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun isLikes(postid: String?, imageView: ImageView?) {
        val reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(
            postid!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var liked = false
                for (dataSnapshot in snapshot.getChildren()) {
                    val like = dataSnapshot.getValue(Like::class.java)
                    if (like != null && like.userID_like == firebaseUser!!.uid) {
                        liked = true
                        break
                    }
                }
                if (liked) {
                    imageView!!.setImageResource(R.drawable.icon_heart_red)
                    imageView.tag = "liked"
                } else {
                    imageView!!.setImageResource(R.drawable.icon_heart_white)
                    imageView.tag = "like"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun nrLikes(likes: TextView?, postid: String?) {
        val reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(
            postid!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                likes!!.text = snapshot.childrenCount.toString() + " likes"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun publisherInfo(userid: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("User").child(
            userid!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(
                    User::class.java
                )
                if (user != null) {
                    if (user.imageurl != null) {
                        Picasso.get().load(user.imageurl).into(image_profile)
                        Picasso.get().load(user.imageurl).into(image_profile2)
                    }
                    username!!.text = user.username
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
