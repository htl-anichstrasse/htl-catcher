package tirol.htlanichstrasse.htlcatcher.game.component;

import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.util.CatcherConfig;
import tirol.htlanichstrasse.htlcatcher.util.ViewPoint;

/**
 * Represents a logo on the GameView canvas
 *
 * @author Joshua Winkler
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
    * True if the logo is currently flying upwards, false otherwise
    */
   private boolean yDirection = true;

   /**
    * UNIX timestamp of the last turn in y direction
    */
   private long lastTurn = 0L;

   /**
    * Creates a new logo on the GameView canvas
    *
    * @param x the x coordinate of the logo
    * @param y the y coordinate of the logo
    */
   public Logo(final int x, final int y, final int radius) {
      super(x, y, radius);
   }

   /**
    * Resets ("revives") this logo with randomized properties (randomized y position and speed)
    */
   public void resetLogo(final int screenWidth, final int minY, final int maxY,
       final Random random) {
      this.x = screenWidth;
      this.y = random.nextInt(maxY - minY) + minY;
      this.speed = random.nextInt(3) + CatcherConfig.getInstance().getLogoMinSpeed();
      this.alive = true;
   }

}
