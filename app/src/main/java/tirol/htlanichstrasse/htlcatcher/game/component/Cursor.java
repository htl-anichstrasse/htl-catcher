package tirol.htlanichstrasse.htlcatcher.game.component;

import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.util.ViewPoint;

/**
 * Representing the player cursor on the GameView canvas
 *
 * @author Joshua Winkler
 * @since 03.11.19
 */
@Getter
@Setter
public final class Cursor extends ViewPoint {

   /**
    * Birds velocity in y direction; positive values mean falling, negative values rising
    */
   private int yVelocity = 0;

   /**
    * UNIX timestamp of the last direction change in the start phase
    */
   private long startLastTurn = System.currentTimeMillis();

   /**
    * True is upwards movement, false downwards
    */
   private boolean startDirection = false;

   /**
    * Creates a new cursor on the GameView canvas
    *
    * @param x the x coordinate of the cursor
    * @param y the y coordinate of the cursor
    */
   public Cursor(final int x, final int y, final int radius) {
      super(x, y, radius);
   }

}
