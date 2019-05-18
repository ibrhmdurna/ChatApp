package com.ibrhmdurna.chatapp.util;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

public class GridImageViewRounded extends RoundedImageView {
    public GridImageViewRounded(Context context) {
        super(context);
    }

    public GridImageViewRounded(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridImageViewRounded(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
