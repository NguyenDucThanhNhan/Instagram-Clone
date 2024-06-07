package com.ltdd.instagramclone.view;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ltdd.instagramclone.R;
import com.ltdd.instagramclone.adapter.CommentsAdapter;
import com.ltdd.instagramclone.model.Comment;
import com.ltdd.instagramclone.model.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CommentsActivity extends AppCompatActivity {
    EditText addcomment;
    ImageView image_profile;
    TextView post;
    String postid;
    String publisherid;
    FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private List<Comment> commentList;
    private CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        addcomment = findViewById(R.id.addcomment);
        post = findViewById(R.id.post);
        image_profile = findViewById(R.id.image_profile);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (addcomment.getText().toString().equals("")) {

                    Toast.makeText(CommentsActivity.this,
                            "you can not send empty comment", Toast.LENGTH_SHORT).show();

                } else {
                    addcomment();
                }
            }
        });
        recyclerView=findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
       recyclerView.setLayoutManager(linearLayoutManager);
       commentList=new ArrayList<>();
       commentsAdapter=new CommentsAdapter(this,commentList);
       recyclerView.setAdapter(commentsAdapter);
        getImage();
        readComments();
    }
    private void addcomment () {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        // Lấy thời gian hiện tại và định dạng thành chuỗi ISO 8601
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());

        // Tạo HashMap và thêm các giá trị vào
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("time_comment", currentDateAndTime);  // Thêm thời gian hiện tại vào HashMap

        // Đẩy giá trị HashMap vào Firebase
        reference.push().setValue(hashMap);
        addcomment.setText("");
    }
    private void getImage () {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
        private void readComments(){

            DatabaseReference reference = FirebaseDatabase. getInstance() .getReference(  "Comments") .child(postid);

            reference. addValueEventListener (new ValueEventListener() {
                @Override
                public void onDataChange (@NonNull DataSnapshot dataSnapshot) {

                    commentList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Comment comment = snapshot.getValue(Comment.class);
                        commentList.add(comment);
                        Log.d("FirebaseData", "Comment: " + comment.getComment() + ", Publisher: " + comment.getPublisher());
                    }

                    commentsAdapter.notifyDataSetChanged();
                    Log.d("FirebaseData", "Number of comments: " + commentList.size());
                }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError){
                            Log.d("FirebaseData", "DatabaseError: " + databaseError.getMessage());
                }
            });
    }
}