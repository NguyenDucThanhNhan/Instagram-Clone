package com.ltdd.instagramclone.view;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.ltdd.instagramclone.R;

import java.io.File;

public class NextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        setImage();
    }
    private void setImage() {

        Intent intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);

        File imageUrl = new File(intent.getStringExtra("selected_image"));
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


    }
}