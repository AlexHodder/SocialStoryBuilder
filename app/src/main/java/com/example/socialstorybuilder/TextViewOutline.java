package com.example.socialstorybuilder;

import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


import androidx.appcompat.widget.AppCompatTextView;

public class TextViewOutline extends AppCompatTextView {

    public TextViewOutline(Context context) {
        super(context);

    }

    public TextViewOutline(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public TextViewOutline(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    @Override
    public void onDraw(Canvas canvas) {
        setTypeface(Typeface.DEFAULT_BOLD);
        setTextColor(getShadowColor());
        super.onDraw(canvas);
        setTextColor(getTextColors());

    }
}
