package com.example.dailyselfie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class PhotoActivity extends Activity {

    ImageView image;
    String mCurrentPhotoPath;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);
        if(getIntent().hasExtra("byteArray")) {
        	mCurrentPhotoPath = (String)getIntent().getStringExtra("fileName");
        	image = (ImageView)findViewById(R.id.imageView2);
            Bitmap b = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);        
            image.setImageBitmap(b);
        }     
    }
}