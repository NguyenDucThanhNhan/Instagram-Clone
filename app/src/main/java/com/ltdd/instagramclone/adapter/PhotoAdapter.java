package com.ltdd.instagramclone.adapter;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ltdd.instagramclone.view.DetailPostFragment;
import com.ltdd.instagramclone.view.List_likeActivity;
import com.ltdd.instagramclone.R;
import com.ltdd.instagramclone.Utils.TimeUtils;
import com.ltdd.instagramclone.view.CommentsActivity;
import com.ltdd.instagramclone.model.Like;
import com.ltdd.instagramclone.model.Photo;
import com.ltdd.instagramclone.model.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public Context mContext ;
    private List<Photo> mPhoto ;
    private FirebaseUser firebaseUser ;

    public PhotoAdapter (Context mContext, List<Photo> mPhoto) {
        this.mContext = mContext;
        this.mPhoto = mPhoto;

    }
    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater. from(mContext) .inflate(R.layout.photo_item, parent,
                false);

        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Photo photo = mPhoto.get(position);
        Picasso.get().load(photo.getImage_path()).into(holder.post_image);
        holder.caption.setText(photo.getCaption());
        holder.time.setText(TimeUtils.getTimeAgo(photo.getDate_created()));

//        if (photo.getTags().equals("")) {
//            holder.tag.setVisibility(View.GONE);
//        }
//        else {
//            holder.tag.setVisibility(View.VISIBLE);
//            holder.tag.setText(photo.getTags());
//
//        }


        publisherInfo(holder.image_profile,holder.image_profile2 ,holder.username
                , photo.getUser_id());

        isLikes(photo.getPhoto_id(),holder.image_heart);
        nrLikes(holder.likes,photo.getPhoto_id());
        getComments(photo.getPhoto_id(),holder.comments);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("postid", photo.getPhoto_id());
                bundle.putString("publisherid", photo.getUser_id());

                // Log bundle data
                Log.d(TAG, "Bundle data: postid=" + photo.getPhoto_id() + ", publisherid=" + photo.getUser_id());

                DetailPostFragment detailPostFragment = new DetailPostFragment();
                detailPostFragment.setArguments(bundle);

                ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homescreen, detailPostFragment)
                        .addToBackStack(null)
                        .commit();

                // Log fragment transaction
                Log.d(TAG, "DetailPostFragment transaction committed.");
            }
        });
        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (mContext, CommentsActivity.class);
                intent.putExtra (  "postid", photo.getPhoto_id());
                intent.putExtra ( "publisherid", photo.getUser_id());
                mContext.startActivity(intent);
            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (mContext, CommentsActivity.class);
                intent.putExtra (  "postid", photo.getPhoto_id());
                intent.putExtra ( "publisherid", photo.getUser_id());
                mContext.startActivity(intent);
            }
        });
        holder.speech_bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (mContext, CommentsActivity.class);
                intent.putExtra (  "postid", photo.getPhoto_id());
                intent.putExtra ( "publisherid", photo.getUser_id());
                mContext.startActivity(intent);
            }
        });
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (mContext, List_likeActivity.class);
                intent.putExtra (  "postid", photo.getPhoto_id());
                mContext.startActivity(intent);

            }

        });

        holder.image_heart.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                if (holder.image_heart.getTag().equals("like")) {

                    DatabaseReference reference =  FirebaseDatabase
                            .getInstance()
                            .getReference("Likes")
                            .child(photo.getPhoto_id());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("UserID_like",firebaseUser.getUid() );
                    reference.push().setValue(hashMap);

                    addNotification(photo.getUser_id(),photo.getPhoto_id());



                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(photo.getPhoto_id());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Like like = dataSnapshot.getValue(Like.class);
                                if (like != null && like.getUserID_like().equals(firebaseUser.getUid())) {
                                    dataSnapshot.getRef().removeValue(); // Xóa giá trị hiện tại từ Likes
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhoto.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile, post_image,image_heart,speech_bubble,image_profile2;
        public TextView username;
        public TextView tv_comment;
        public TextView likes;
        public TextView comments;
        public TextView tag;
        public TextView caption;
        public TextView time;
        public FragmentContainerView FragmentContainerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
//            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            //tag = itemView.findViewById(R.id.tag);
            caption = itemView.findViewById(R.id.caption);
            time = itemView.findViewById(R.id.time_posted);
            image_heart=itemView.findViewById(R.id.image_heart);
            speech_bubble=itemView.findViewById(R.id.speech_bubble);
            FragmentContainerView=itemView.findViewById(R.id.fragmentContainerView4);
            image_profile2 = itemView.findViewById(R.id.image_profile2);
        }
    }
    private void getComments (String postid, final TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance() .getReference () .child("Comments").child(postid) ;

        reference.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {

                comments.setText("Xem tất cả " + dataSnapshot.getChildrenCount() + " bình luận");
            }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError){
                }
                });
        }

private void isLikes(String postid, final ImageView imageView) {
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
   reference.addValueEventListener(new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull DataSnapshot snapshot) {
           boolean liked = false;
           for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
               Like like =dataSnapshot.getValue(Like.class);
               Log.d("FirebaseData", "Comment: " +like.getUserID_like());
               if (like.getUserID_like() != null && like.getUserID_like().equals(firebaseUser.getUid())) {
                   liked = true;
                   break;
               }
           }
           if (liked) {
               imageView.setImageResource(R.drawable.icon_heart_red);
               imageView.setTag("liked");
           } else {
               imageView.setImageResource(R.drawable.icon_heart_white);
               imageView.setTag("like");
           }
       }

       @Override
       public void onCancelled(@NonNull DatabaseError error) {

       }
   });
}



    private void nrLikes(final TextView likes , String postid) {

        DatabaseReference reference = FirebaseDatabase.getInstance() .getReference() .child("Likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " lượt thích");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void publisherInfo (final ImageView image_profile,final ImageView image_profile2, final TextView username , String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageurl()!=null)
                Picasso.get().load(user.getImageurl()).into(image_profile);
                Picasso.get().load(user.getImageurl()).into(image_profile2);
                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addNotification(String userid, String postid)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance() .getReference(  "Notifications").child(userid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put ("postid", postid);
        hashMap.put("ispost",true);
        hashMap.put("time", currentDateAndTime);
        reference.push() .setValue (hashMap) ;
    }

}

