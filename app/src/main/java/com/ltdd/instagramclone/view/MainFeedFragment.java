package com.ltdd.instagramclone.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ltdd.instagramclone.R;
import com.ltdd.instagramclone.adapter.PhotoAdapter;
import com.ltdd.instagramclone.model.Photo;

import java.util.ArrayList;
import java.util.List;


public class MainFeedFragment extends Fragment {


    private RecyclerView recyclerView;
    private PhotoAdapter PhotoAdapter;
    private List<Photo> photolists;

    private List<String> followinglist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main_feed, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_post);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        photolists = new ArrayList<>();
        PhotoAdapter = new PhotoAdapter(getContext(),photolists);
        recyclerView.setAdapter(PhotoAdapter) ;
        checkFollowing();
        return view;
    }

    private void checkFollowing() {
        followinglist=new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot datasnapshot) {
                followinglist.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {

                    followinglist.add(snapshot.getKey());
                }
                    followinglist.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    readPosts();
                }


            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    public void readPosts () {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("photos");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot datasnapshot) {
                photolists.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {

                    Photo photo = snapshot.getValue(Photo.class);
                    for (String id : followinglist) {

                        if (photo.getUser_id().equals(id)) {
                            photolists.add(photo);
                        }
                    }
                }
                PhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }
}