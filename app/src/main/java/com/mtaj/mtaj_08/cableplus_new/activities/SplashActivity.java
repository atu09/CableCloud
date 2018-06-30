package com.mtaj.mtaj_08.cableplus_new.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.TextView;

import com.mtaj.mtaj_08.cableplus_new.R;
import com.mtaj.mtaj_08.cableplus_new.helpers.Utils;

public class SplashActivity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";
    private android.widget.TextView txtAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        this.txtAnimate = (TextView) findViewById(R.id.txtAnimate);
        txtAnimate.setTypeface(Typeface.createFromAsset(this.getAssets(), "font/pacifico.ttf"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        setLayoutAnimations();
    }

    private void setLayoutAnimations() {

        Utils.animateFadeView(this, txtAnimate, 1, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                if (pref.getString("LoginStatus", "").equals("login")) {
                    startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}