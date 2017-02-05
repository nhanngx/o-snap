package com.nhanngx.osnap.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by nhanngx on 29-Jan-17.
 * A square layout that dynamically stretches the width of the parent layout and set the height accordingly.
 */

public class SquareLayout extends FrameLayout {
    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mHeight = MeasureSpec.getSize(heightMeasureSpec);
        int size = mWidth > mHeight ? mHeight : mWidth;
        setMeasuredDimension(size, size);
    }
}
