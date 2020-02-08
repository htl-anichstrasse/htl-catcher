package tirol.htlanichstrasse.htlcatcher.game.component;

import lombok.Getter;
import lombok.Setter;

/**
 * A subtype of the normal logo which grants a point-multiplier for a certain duration.
 *
 * @author Nicolaus Rossi
 * @since 02.02.2020
 */

@Getter
@Setter
public class MultiplierLogo extends Logo {

   /**
    * Determines the duration of the multiplier.
    */
   private long multiplierDuration = 0L;

   /**
    * Determines whether the buff is currently active or not.
    */
   private boolean isActive = false;

   /**
    * Creates a new multiplier-logo on the GameView canvas
    *
    * @param x the x coordinate of the logo
    * @param y the y coordinate of the logo
    */
   public MultiplierLogo(final int x, final int y, final int radius) {
      super(x, y, radius);
   }
}
