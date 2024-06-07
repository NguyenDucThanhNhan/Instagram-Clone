package com.ltdd.instagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ltdd.instagramclone.R;
import com.ltdd.instagramclone.Utils.TimeUtils;
import com.ltdd.instagramclone.model.Comment;
import com.ltdd.instagramclone.model.Notification;
import com.ltdd.instagramclone.model.Photo;
import com.ltdd.instagramclone.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<com.ltdd.instagramclone.model.Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);

        return new NotificationAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notification notification = mNotification.get(position);

            holder.tv_notification.setText(notification.getText());
        holder.time.setText(TimeUtils.getTimeAgo(notification.getTime()));
        getUserInfo( holder.image_profile,holder.username, notification.getUserid());

        if(notification.isIspost ()) {

            holder.post_image.setVisibility(View.VISIBLE);

            getPostImage(holder.post_image, notification.getPostid());

        }else {
                holder.post_image.setVisibility(View.GONE);

            }
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image_profile, post_image;
        TextView username, tv_notification,time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.iv_avatar1);
            tv_notification = itemView.findViewById(R.id.tv_notification);

            username = itemView.findViewById(R.id.username);
            post_image = itemView.findViewById(R.id.image_post);
            time =itemView.findViewById(R.id.tv_time);
        }
    }


    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(publisherid);

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

    private void getPostImage(final ImageView imageView, String postid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("photos").
                child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Photo photo = snapshot.getValue(Photo.class);
                Picasso.get().load(photo.getImage_path()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
