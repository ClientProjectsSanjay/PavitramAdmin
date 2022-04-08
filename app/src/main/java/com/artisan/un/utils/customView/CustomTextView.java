package com.artisan.un.utils.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

import com.artisan.un.R;

public class CustomTextView extends AppCompatTextView {

    private TypeFactory mFontFactory;

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    public CustomTextView(Context context) {
        super(context);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomWidget,
                0, 0);
        int typefaceType;
        try {
            typefaceType = array.getInteger(R.styleable.CustomWidget_font_name, 0);
        } finally {
            array.recycle();
        }
        if (!isInEditMode()) {
            if (mFontFactory == null)
                mFontFactory = new TypeFactory(getContext());
            setTypeface(mFontFactory.getTypeFace(typefaceType));
        }
    }
}
