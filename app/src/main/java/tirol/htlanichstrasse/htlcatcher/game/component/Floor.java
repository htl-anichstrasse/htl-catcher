package tirol.htlanichstrasse.htlcatcher.game.component;

import android.content.Context;
import android.util.AttributeSet;
import com.q42.android.scrollingimageview.ScrollingImageView;
import tirol.htlanichstrasse.htlcatcher.game.GameView;

/**
 * Represents the floor of the game
 *
 * @author Joshua Winkler
 * @since 13.11.2019
 */
public class Floor extends ScrollingImageView {

   /**
    * The relative height of the floor
    */
   private final int floorHeight;

   public Floor(final Context context, final AttributeSet attrs) {
      super(context, attrs);
      this.floorHeight = getBitmaps().get(0).getHeight();
   }

   /**
    * Checks if the given cursor is collided with the floor
    *
    * @param cursor the cursor to check collision with
    * @param view the GameView
    * @return true if the cursor is collided, false otherwise
    */
   public boolean isCursorCollided(final Cursor cursor, final GameView view) {
      return cursor.y + cursor.getRadius() > view.getHeight() - this.floorHeight;
   }

}