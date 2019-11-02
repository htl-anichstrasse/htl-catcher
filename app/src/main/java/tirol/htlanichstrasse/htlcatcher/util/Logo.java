package tirol.htlanichstrasse.htlcatcher.util;

import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.Config;

/**
 * Represents a logo on the GameView canvas
 *
 * @author Nicolaus Rossi
 * @since 02.11.2019
 */
@Getter
@Setter
public class Logo extends ViewPoint {

   /**
    * Determines whether this logo is "alive"; it's not alive if it has reached the end of the
    * screen. Logos in the "not alive" state need to be "revived"/reset before they can be reused
    */
   private boolean alive = true;

   /**
    * The speed of this logo
    */
   private int speed = 0;

   /**
    * Creates a new logo on the GameView canvas
    *
    * @param x the x coordinate of the logo
    * @param y the y coordinate of the logo
    */
   public Logo(final int x, final int y) {
      super(x, y);
   }

   /**
    * Resets ("revives") this logo with randomized properties (randomized y position and speed)
    */
   public void resetLogo(final int screenWidth, final int minY, final int maxY,
       final Random random) {
      this.x = screenWidth;
      this.y = random.nextInt(maxY - minY) + minY;
      this.speed = random.nextInt(3) + Config.getInstance().getMinLogoSpeed();
      this.alive = true;
   }

}
