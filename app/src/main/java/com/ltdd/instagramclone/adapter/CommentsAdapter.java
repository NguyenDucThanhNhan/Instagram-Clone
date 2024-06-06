package com.ltdd.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ltdd.instagramclone.R;
import com.ltdd.instagramclone.Utils.TimeUtils;
import com.ltdd.instagramclone.model.Comment;
import com.ltdd.instagramclone.model.User;
import com.ltdd.instagramclone.view.HomeScreen;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentsAdapter extends  RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mcomment;

    FirebaseUser firebaseUser;

    public CommentsAdapter(Context mContext, List<Comment> mcomment) {
        this.mContext = mContext;
        this.mcomment = mcomment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater. from(mContext) .inflate(R.layout.comment_item, parent, false);


        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final  Comment comment=mcomment.get(position);
        holder.content_comment.setText(comment.getComment());
        holder.time_commnent.setText(TimeUtils.getTimeAgo(comment.getTime_comment()));
        getUserInfo(holder.image_profile,holder.username,comment.getPublisher());
    }

    @Override
    public int getItemCount() {
        return mcomment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView image_profile;
        public TextView content_comment, username,time_commnent;
        public ViewHolder (@NonNull View itemView) {
            super (itemView) ;

            image_profile = itemView.findViewById(R.id.image_profile);
            content_comment = itemView.findViewById(R.id.content_comment);
            username = itemView.findViewById(R.id.username);
            time_commnent=itemView.findViewById(R.id.time_comment);

        }
    }
    private void getUserInfo (final ImageView imageView, final TextView username, String publisherid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
