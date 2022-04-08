package com.artisan.un.utils.customView;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.artisan.un.R;

class TypeFactory {
    final Typeface regular;
    final Typeface bold;
    final Typeface semi_bold;
    final Typeface medium;
    final Typeface light;
    final Typeface thin;
    final Typeface extra_bold;
    final Typeface extra_light;

    public TypeFactory(Context context) {
        bold = ResourcesCompat.getFont(context, R.font.bold);
        semi_bold = ResourcesCompat.getFont(context, R.font.semi_bold);
        medium = ResourcesCompat.getFont(context, R.font.medium);
        regular = ResourcesCompat.getFont(context, R.font.regular);
        light = ResourcesCompat.getFont(context, R.font.light);
        thin = ResourcesCompat.getFont(context, R.font.thin);
        extra_bold = ResourcesCompat.getFont(context, R.font.extra_bold);
        extra_light = ResourcesCompat.getFont(context, R.font.extra_light);
    }

    public Typeface getTypeFace(int type) {
        switch (type) {
            case Constants.bold:
                return bold;
            case Constants.semi_bold:
                return semi_bold;
            case Constants.medium:
                return medium;
            case Constants.light:
                return light;
            case Constants.thin:
                return thin;
            case Constants.extra_bold:
                return extra_bold;
            case Constants.extra_light:
                return extra_light;
            default:
                return regular;
        }
    }

    interface Constants {
        int regular = 0, bold = 1, semi_bold = 2, medium = 3, light = 4, thin = 5, extra_bold = 6, extra_light = 7;
    }
}
