package tirol.htlanichstrasse.htlcatcher.game.component;

import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.game.component.Logo;

/**
 * A subtype of the normal logo which inverses gravity for a short duration.
 *
 * @author Nicolaus Rossi
 * @since 08.02.2020
 */

@Getter
@Setter
public class InvertLogo extends Logo {

   /**
    * Determines how long the buff is active.
    */
   private long duration = 0L;

   /**
    * Creates a new buffer-logo on the GameView canvas
    *
    * @param x the x coordinate of the logo
    * @param y the y coordinate of the logo
    */
   public InvertLogo(final int x, final int y, final int radius) {
      super(x, y, radius);
   }

}