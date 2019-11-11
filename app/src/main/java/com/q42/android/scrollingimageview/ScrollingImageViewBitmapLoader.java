package com.q42.android.scrollingimageview;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * From: https://github.com/Q42/AndroidScrollingImageView
 *
 * @author thijs
 * @since 22.03.2016
 */
public interface ScrollingImageViewBitmapLoader {

   Bitmap loadBitmap(Context context, int resourceId);

}