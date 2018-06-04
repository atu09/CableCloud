package com.mtaj.mtaj_08.cableplus_new.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.mtaj.mtaj_08.cableplus_new.BuildConfig;
import com.mtaj.mtaj_08.cableplus_new.R;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by atirek.pothiwala on 29/03/18.
 */

public class Utils {

    public static GradientDrawable getGradientDrawable(Context context, int color1, int color2, GradientDrawable.Orientation orientation) {
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, new int[]{ContextCompat.getColor(context, color1), ContextCompat.getColor(context, color2)});
        gradientDrawable.setCornerRadius(0f);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        return gradientDrawable;
    }

    public static GradientDrawable getGradientDrawable(Context context, int color1, int color2, int color3, GradientDrawable.Orientation orientation) {
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, new int[]{ContextCompat.getColor(context, color1), ContextCompat.getColor(context, color2), ContextCompat.getColor(context, color3)});
        gradientDrawable.setCornerRadius(0f);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setOrientation(orientation);

        return gradientDrawable;
    }

    public static SpannableString getStyle(Context context, String currentString, String font) {

        SpannableString updatedString = new SpannableString(currentString);
        final Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/" + font);
        MetricAffectingSpan span = new MetricAffectingSpan() {
            @Override
            public void updateMeasureState(TextPaint paint) {
                paint.setTypeface(custom_font);
            }

            @Override
            public void updateDrawState(TextPaint paint) {
                paint.setTypeface(custom_font);
            }
        };

        updatedString.setSpan(span, 0, updatedString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return updatedString;
    }

    public static AnimationDrawable getAnimatedBackground(Context context, int[] colors, int duration) {

        AnimationDrawable animation = new AnimationDrawable();
        animation.setOneShot(false);

        duration = duration * 1000;

        for (int i = 0; i < colors.length - 1; i++) {
            animation.addFrame(getGradientDrawable(context, colors[i], colors[i + 1], GradientDrawable.Orientation.TR_BL), duration);
        }

        animation.setEnterFadeDuration(duration);
        animation.setExitFadeDuration(duration);

        return animation;

    }

    public static void animateFadeView(Context context, View view, int duration, Animation.AnimationListener animationListener) {
        view.setVisibility(View.VISIBLE);
        Animation anim_translate = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        anim_translate.setDuration(duration * 1000);
        anim_translate.setStartTime(Animation.START_ON_FIRST_FRAME);
        if (animationListener != null) {
            anim_translate.setAnimationListener(animationListener);
        }
        view.startAnimation(anim_translate);
    }

    public static boolean checkPermissions(Context context, String[] permissions) {

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermissions(Context context, String[] permissions) {
        ActivityCompat.requestPermissions((Activity) context, permissions, 101);
    }

    public static void popToast(Context context, Object data) {
        try {
            Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            checkLog("popToast>>", data.toString(), null);
        }
    }

    public static void checkLog(String TAG, Object data, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            if (throwable != null) {
                Log.d(TAG + ">>", data.toString(), throwable);
            } else {
                Log.d(TAG + ">>", data.toString());
            }
        }
    }

    public static void restartApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void closeKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void closeKeyboard(Context context, Dialog dialog) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getColor(Context context, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else {
            return context.getResources().getColor(color);
        }
    }

}
