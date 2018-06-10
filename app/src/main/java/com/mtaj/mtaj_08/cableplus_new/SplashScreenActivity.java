package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtaj.mtaj_08.cableplus_new.helpers.Utils;

public class SplashScreenActivity extends AppCompatActivity {

    private android.widget.ImageView imageView;
    private static final String PREF_NAME = "LoginPref";
    private android.widget.TextView txtAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        this.txtAnimate = (TextView) findViewById(R.id.txtAnimate);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        txtAnimate.setTypeface(Typeface.createFromAsset(this.getAssets(), "font/pacifico.ttf"));

        setLayoutAnimations();

    }

    private void setLayoutAnimations() {
        Utils.animateFadeView(this, txtAnimate, 4, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


            }


            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                //  SharedPreferences.Editor editor=pref.edit();

             /*   if (pref.getString("LoginStatus", "").toString().equals("login")) {
                    Intent i = new Intent(getApplicationContext(), DashBoard.class);
                    startActivity(i);
                    finish();
                } else {*/
//                    imageView.setTransitionName("appLogo");
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, imageView, "appLogo");
                Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(i);

//                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
