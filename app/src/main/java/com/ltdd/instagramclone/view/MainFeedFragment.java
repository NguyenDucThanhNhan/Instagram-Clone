package com.ltdd.instagramclone.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.ltdd.instagramclone.adapter.StoryAdapter;
import com.ltdd.instagramclone.model.Photo;
import com.ltdd.instagramclone.model.Story;

import java.util.ArrayList;
import java.util.List;


public class MainFeedFragment extends Fragment {


    private RecyclerView recyclerView;
    private PhotoAdapter PhotoAdapter;
    private List<Photo> photolists;
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

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
        recyclerView_story = view.findViewById(R.id.lv_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);

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
                readPosts();
                readyStory();

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
    public void readyStory() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currenttime = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "", FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for(String id: followinglist) {
                    int countStory = 0;
                    Story story = null;
                    for(DataSnapshot dataSnapshot1 :snapshot.child(id).getChildren()) {
                        story = dataSnapshot1.getValue(Story.class);
                        if(currenttime > story.getTimestart() && currenttime < story.getTimeend()) {
                            countStory ++;
                        }
                    }
                    if(countStory > 0) {
                        storyList.add(story);
                    }

                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}