package com.cable.cloud.customs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Alm on 9/26/2016.
 */
public class MySquareImage extends ImageView {
    public MySquareImage(Context context) {
        super(context);
    }

    public MySquareImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySquareImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
    }

}
