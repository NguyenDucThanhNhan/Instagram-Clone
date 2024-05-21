package com.ltdd.instagramclone.view;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ltdd.instagramclone.R;
import com.ltdd.instagramclone.model.FilePaths;
import com.ltdd.instagramclone.model.Photo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class NextActivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    private EditText mCaption;
    private File imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mCaption = (EditText) findViewById(R.id.caption) ;

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        //getIntent:
        Intent intent = getIntent();
        imageUrl = new File(Objects.requireNonNull(intent.getStringExtra("selected_image")));

        //Set Image to ImageView
        setImage(imageUrl);
        TextView nextScreen = (TextView) findViewById(R.id.tvShare);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNewPhoto(mCaption.getText().toString(), 1, imageUrl, null);

            }
        });
        ImageView previousAc = (ImageView) findViewById(R.id.ivCloseShare);
        previousAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }
    private void setImage(File imageUrl) {


        ImageView image = (ImageView) findViewById(R.id.imageShare);


        if (imageUrl.exists()) {
            // on below line we are creating an image bitmap variable
            // and adding a bitmap to it from image file.
            Bitmap imgBitmap = BitmapFactory.decodeFile(imageUrl.getAbsolutePath());

            // on below line we are setting bitmap to our image view.
            image.setImageBitmap(imgBitmap);
        }
        Log.d(TAG, "receive: " + imageUrl);
//        StorageReference storageReference = storageRef
//                .child("photo/users/" + "/" + "iOF2PYZN4tXagFtATdrd8ffq2mK2" + "/photo1");
//
//        //convert image url to bitmap
//        Bitmap bm = ImageManager.getBitmap(intent.getStringExtra(getString(R.string.selected_image)));
//        byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);
//
//        UploadTask uploadTask = null;
//        uploadTask = storageReference.putBytes(bytes);
//
//
    }
    public static String getTags(String string){
        if(string.indexOf("#") > 0){
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundWord = false;
            for( char c : charArray){
                if(c == '#'){
                    foundWord = true;
                    sb.append(c);
                }else{
                    if(foundWord){
                        sb.append(c);
                    }
                }
                if(c == ' ' ){
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ", "").replace("#", ",#");
            return s.substring(1, s.length());
        }
        return string;
    }

        public void uploadNewPhoto (final String caption, final int count, final File imgUrl,
        Bitmap bm){

            FilePaths filePaths = new FilePaths();

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            if (bm == null) {
                bm = BitmapFactory.decodeFile(imageUrl.getAbsolutePath());
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] bytes = stream.toByteArray();

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Lấy URL tải xuống từ Firebase Storage
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            String firebaseUrl = downloadUri.toString();

                            // Hiển thị Toast
                            Toast.makeText(NextActivity.this, "Bạn đã đăng 1 bài viết mới!", Toast.LENGTH_SHORT).show();

                            // Thêm ảnh mới vào cơ sở dữ liệu
                            addPhotoToDatabase(caption, firebaseUrl);

                            // Chuyển đến HomeActivity (nếu cần)
//                Intent intent = new Intent(NextActivity.this, HomeActivity.class);
//                startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Photo upload failed.");
                            Toast.makeText(NextActivity.this, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(NextActivity.this, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            });



        }
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", new Locale("vi", "VN"));
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Thiết lập timezone cho múi giờ của Việt Nam
        return sdf.format(new Date());
    }
    private void addPhotoToDatabase(String caption, String url){
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        String tags = getTags(caption);
        String newPhotoKey = myRef.child("photos").push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child("user_photos")
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);
        myRef.child("photos").child(newPhotoKey).setValue(photo);

    }



}