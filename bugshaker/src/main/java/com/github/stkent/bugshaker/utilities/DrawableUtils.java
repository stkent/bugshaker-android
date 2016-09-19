package com.github.stkent.bugshaker.utilities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by slmyldz on 19.09.2016.
 */
public class DrawableUtils {


    public static Drawable readDrawableFromUri(Activity activity ,Uri uri){
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
           return Drawable.createFromStream(inputStream, uri.toString());
        } catch (FileNotFoundException e) {
           return null;
        }
    }

}
