package com.mtaj.mtaj_08.cableplus_new.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class TypeWriter extends TextView {
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 150; // in ms
    private TyperAnimationListener typerAnimationListener;

    public TypeWriter(Context context) {
        super(context);
    }
    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            } else {
                typerAnimationListener.onAnimationOver();
            }
        }


    };

    public void animateText(CharSequence txt, TyperAnimationListener typerAnimationListener) {
        mText = txt;
        mIndex = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
        this.typerAnimationListener = typerAnimationListener;
    }

    public void setCharacterDelay(long m) {
        mDelay = m;
    }

    public interface TyperAnimationListener {
        void onAnimationOver();
    }

}