package com.mtaj.mtaj_08.cableplus_new;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullImageActivity extends AppCompatActivity {

    PhotoViewAttacher mAttacher;

    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_full_image);

        Intent j=getIntent();
        bmp=(Bitmap) j.getParcelableExtra("Image");


        ImageView im=(ImageView)findViewById(R.id.imageView12);
        mAttacher = new PhotoViewAttacher(im);

       im.setImageBitmap(bmp);

        mAttacher.update();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
