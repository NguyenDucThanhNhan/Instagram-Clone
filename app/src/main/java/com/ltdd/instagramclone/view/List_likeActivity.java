package com.ltdd.instagramclone.view;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.ltdd.instagramclone.adapter.UserAdapter;
import com.ltdd.instagramclone.model.Like;
import com.ltdd.instagramclone.model.User;

import java.util.ArrayList;

public class List_likeActivity extends AppCompatActivity {

    private String postId;
    private RecyclerView rv_likes;
    private UserAdapter userAdapter;
    private ArrayList<User> Users;
    private ArrayList<String> UserId_like;

    private EditText tv_search;
    private FirebaseUser currentUser ;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_like);

        rv_likes = findViewById(R.id.rv_likes);
        rv_likes.setHasFixedSize(true);
        rv_likes.setLayoutManager(new LinearLayoutManager(this));
        tv_search = findViewById(R.id.edt_search_likes);
        Users = new ArrayList<>();
        UserId_like = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userAdapter = new UserAdapter(currentUser,Users);
        rv_likes.setAdapter(userAdapter);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postid");
        loadAllUserID_Like();

        tv_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }
    public void searchUser(String s) {
        // Xóa trắng đầu cuối chuỗi tìm kiếm và chuyển thành chữ thường
        String searchQuery = s.trim().toLowerCase();

        // Xóa dữ liệu người dùng cũ
        Users.clear();

        // Truy vấn tất cả UserId đã thích bài viết
        DatabaseReference likesReference = FirebaseDatabase.getInstance().getReference("Likes").child(postId);
        likesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Like like = dataSnapshot.getValue(Like.class);
                    String userId = like.getUserID_like(); // Lấy UserId từ danh sách likes

                    // Truy vấn thông tin người dùng dựa trên UserId
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User").child(userId);
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null && user.getUsername() != null
                                    && user.getUsername().toLowerCase().contains(searchQuery)
                                    //&& !user.getUserid().equals(currentUser.getUid())
                            ) {
                                // Nếu tìm thấy người dùng và không phải là người dùng hiện tại, thêm vào danh sách Users
                                Users.add(user);
                                userAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu cần
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void loadAllUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    // Kiểm tra xem UserId của người dùng có trong danh sách UserId_like không
                    if (UserId_like.contains(user.getUserid())
                           // && !user.getUserid().equals(currentUser.getUid())
                    ) {
                        Users.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadAllUserID_Like() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserId_like.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Like like = dataSnapshot.getValue(Like.class);
                    UserId_like.add(like.getUserID_like());
                }

                // Sau khi đã lấy UserId của tất cả người đã like, ta tải danh sách người dùng
                loadAllUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    }