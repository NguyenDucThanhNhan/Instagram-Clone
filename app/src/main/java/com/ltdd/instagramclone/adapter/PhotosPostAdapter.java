package com.ltdd.instagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ltdd.instagramclone.R;
import com.ltdd.instagramclone.model.Post;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PhotosPostAdapter extends RecyclerView.Adapter<PhotosPostAdapter.ViewHolder>{
    private Context context;
    private List<Post> mPosts;

    public PhotosPostAdapter (Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater. from(context) . inflate (R.layout. post_photo_item, viewGroup,false);
        return new PhotosPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

            Post post = mPosts.get(i);
            //Glide.with(context).load(post.getImageUrl().into(viewHolder.post_image));
    }

    @Override
    public int getItemCount() {

        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }

}
