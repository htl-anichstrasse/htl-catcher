package com.q42.android.scrollingimageview;

import static java.lang.Math.abs;
import static java.util.Collections.singletonList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.R;

/**
 * From: https://github.com/Q42/AndroidScrollingImageView
 *
 * @author thijs
 * @since 08.06.2015
 */
public class ScrollingImageView extends View {

   public static ScrollingImageViewBitmapLoader BITMAP_LOADER = (context, resourceId) ->
       BitmapFactory.decodeResource(context.getResources(), resourceId);

   @Getter
   private List<Bitmap> bitmaps;
   @Setter
   private float speed;
   private int[] scene;
   private int arrayIndex = 0;
   private int maxBitmapHeight = 0;

   private Rect clipBounds = new Rect();
   private float offset = 0;

   private boolean isStarted;

   public ScrollingImageView(final Context context, final AttributeSet attrs) {
      super(context, attrs);
      TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollingImageView, 0, 0);
      int initialState;
      try {
         initialState = ta.getInt(R.styleable.ScrollingImageView_initialState, 0);
         speed = ta.getDimension(R.styleable.ScrollingImageView_speed, 10);
         int sceneLength = ta.getInt(R.styleable.ScrollingImageView_sceneLength, 1000);
         final int randomnessResourceId = ta
             .getResourceId(R.styleable.ScrollingImageView_randomness, 0);
         int[] randomness = new int[0];
         if (randomnessResourceId > 0) {
            randomness = getResources().getIntArray(randomnessResourceId);
         }

         int type = isInEditMode() ? TypedValue.TYPE_STRING
             : ta.peekValue(R.styleable.ScrollingImageView_src).type;
         if (type == TypedValue.TYPE_REFERENCE) {
            int resourceId = ta.getResourceId(R.styleable.ScrollingImageView_src, 0);
            TypedArray typedArray = getResources().obtainTypedArray(resourceId);
            try {
               int bitmapsSize = 0;
               for (int r : randomness) {
                  bitmapsSize += r;
               }

               bitmaps = new ArrayList<>(Math.max(typedArray.length(), bitmapsSize));

               for (int i = 0; i < typedArray.length(); i++) {
                  int multiplier = 1;
                  if (randomness.length > 0 && i < randomness.length) {
                     multiplier = Math.max(1, randomness[i]);
                  }

                  Bitmap bitmap = BITMAP_LOADER
                      .loadBitmap(getContext(), typedArray.getResourceId(i, 0));
                  for (int m = 0; m < multiplier; m++) {
                     bitmaps.add(bitmap);
                  }

                  maxBitmapHeight = Math.max(bitmap.getHeight(), maxBitmapHeight);
               }

               Random random = new Random();
               this.scene = new int[sceneLength];
               for (int i = 0; i < this.scene.length; i++) {
                  this.scene[i] = random.nextInt(bitmaps.size());
               }
            } finally {
               typedArray.recycle();
            }
         } else if (type == TypedValue.TYPE_STRING) {
            final Bitmap bitmap = BITMAP_LOADER
                .loadBitmap(getContext(), ta.getResourceId(R.styleable.ScrollingImageView_src, 0));
            if (bitmap != null) {
               bitmaps = singletonList(bitmap);
               scene = new int[]{0};
               maxBitmapHeight = bitmaps.get(0).getHeight();
            } else {
               bitmaps = Collections.emptyList();
            }
         }
      } finally {
         ta.recycle();
      }

      if (initialState == 0) {
         start();
      }
   }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxBitmapHeight);
   }

   @Override
   public void onDraw(Canvas canvas) {
      if (!isInEditMode()) {
         super.onDraw(canvas);
         if (canvas == null || bitmaps.isEmpty()) {
            return;
         }

         canvas.getClipBounds(clipBounds);

         while (offset <= -getBitmap(arrayIndex).getWidth()) {
            offset += getBitmap(arrayIndex).getWidth();
            arrayIndex = (arrayIndex + 1) % scene.length;
         }

         float left = offset;
         for (int i = 0; left < clipBounds.width(); i++) {
            Bitmap bitmap = getBitmap((arrayIndex + i) % scene.length);
            int width = bitmap.getWidth();
            canvas.drawBitmap(bitmap, getBitmapLeft(width, left), 0, null);
            left += width;
         }

         if (isStarted && speed != 0) {
            offset -= abs(speed);
            postInvalidateOnAnimation();
         }
      }
   }

   private Bitmap getBitmap(int sceneIndex) {
      return bitmaps.get(scene[sceneIndex]);
   }

   private float getBitmapLeft(float layerWidth, float left) {
      if (speed < 0) {
         return clipBounds.width() - layerWidth - left;
      } else {
         return left;
      }
   }

   /**
    * Start the animation
    */
   public void start() {
      if (!isStarted) {
         isStarted = true;
         postInvalidateOnAnimation();
      }
   }

}