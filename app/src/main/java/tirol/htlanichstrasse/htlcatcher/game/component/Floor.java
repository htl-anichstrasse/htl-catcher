package tirol.htlanichstrasse.htlcatcher.game.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import com.q42.android.scrollingimageview.ScrollingImageView;

/**
 * Represents the floor of the game
 *
 * @author Joshua Winkler
 * @since 13.11.2019
 */
public class Floor extends ScrollingImageView {

   public Floor(final Context context, final AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      final Bitmap bitmap = getBitmaps().get(0);
      System.out.println(bitmap.getHeight());
   }

}
