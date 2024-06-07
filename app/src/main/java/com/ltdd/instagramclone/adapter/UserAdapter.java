package com.ltdd.instagramclone.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.ltdd.instagramclone.model.User;
import com.ltdd.instagramclone.view.UserProfileScreen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private FirebaseUser currentUser;
    private ArrayList<User> Users;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public UserAdapter( FirebaseUser currentUser, ArrayList<User> users) {
        this.currentUser = currentUser;
        this.Users = users;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view, mListener, Users);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        final User user = Users.get(position);
        holder.username.setText(user.getUsername());
        holder.name.setText(user.getName());
        Picasso.get().load(user.getImageurl()).into(holder.iv_avatar);

        if (!user.getUserid().equals(currentUser.getUid())) {
            holder.bt_follow.setVisibility(View.VISIBLE);
            isFollowing(user.getUserid(), holder.bt_follow);
        }

        holder.bt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.bt_follow.getText().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(currentUser.getUid()).child("following")
                            .child(user.getUserid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUserid()).child("followers")
                            .child(currentUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(currentUser.getUid()).child("following")
                            .child(user.getUserid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUserid()).child("followers")
                            .child(currentUser.getUid()).removeValue();
                }
            }
        });
        // Use itemView.getContext() to start UserProfileScreen
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), UserProfileScreen.class);
                intent.putExtra("userID", user.getUserid());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView name;
        public Button bt_follow;
        public ImageView iv_avatar;
        private ArrayList<User> Users;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener, ArrayList<User> users) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            name = itemView.findViewById(R.id.tv_name);
            username = itemView.findViewById(R.id.tv_username);
            bt_follow = itemView.findViewById(R.id.bt_follow);
            this.Users = users;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(Users.get(position));
                        }
                    }
                }
            });
        }
    }

    public void isFollowing(final String userID, final Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(currentUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userID).exists()) {
                    button.setText("Following");
                } else {
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
