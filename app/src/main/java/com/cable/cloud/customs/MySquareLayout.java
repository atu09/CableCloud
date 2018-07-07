package com.cable.cloud.customs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Atirek Pothiwala on 6/11/2018.
 */

public class MySquareLayout extends RelativeLayout {

    public MySquareLayout(Context context) {
        super(context);
    }

    public MySquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySquareLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Set a square layout.
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
