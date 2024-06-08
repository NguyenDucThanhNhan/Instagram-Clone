package com.ltdd.instagramclone.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ltdd.instagramclone.R;
import com.ltdd.instagramclone.model.Photo;
import com.ltdd.instagramclone.view.DetailPostFragment;

import java.util.List;

public class PhotosPostAdapter extends RecyclerView.Adapter<PhotosPostAdapter.ViewHolder> {
    private Context context;
    private List<Photo> mPhotos;

    public PhotosPostAdapter(Context context, List<Photo> mPhotos) {
        this.context = context;
        this.mPhotos = mPhotos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_photo_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Photo photo = mPhotos.get(i);
        Glide.with(context).load(photo.getImage_path()).into(viewHolder.post_image);

        viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailPostFragment detailPostFragment = new DetailPostFragment();
                // Truyền dữ liệu vào fragment thông qua Bundle
                Bundle bundle = new Bundle();
                bundle.putString("postid", photo.getPhoto_id());
                bundle.putString("publisherid", photo.getUser_id());
                detailPostFragment.setArguments(bundle);

                // Thay đổi fragment bằng DetailPostFragment
                FragmentActivity activity = (FragmentActivity) context;
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView5, detailPostFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
