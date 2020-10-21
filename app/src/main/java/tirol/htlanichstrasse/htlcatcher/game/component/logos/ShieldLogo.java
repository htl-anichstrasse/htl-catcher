package tirol.htlanichstrasse.htlcatcher.game.component.logos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.component.Logo;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameStatistics;

/**
 * A subtype of the normal logo which grants a shield for a short duration / until the player
 * collides with any form of terrain. (Basically an extra life)
 *
 * @author Nicolaus Rossi
 * @since 22.01.2020
 */

@Getter
@Setter
public class ShieldLogo extends Logo {

   /**
    * Determines how long the buff is active.
    */
   private long buffDuration = 0L;

   /**
    * Determines whether the buff is currently active or not.
    */
   private boolean isActive = false;

   /**
    * Creates a new shield-logo on the GameView canvas
    *
    * @param x the x coordinate of the logo
    * @param y the y coordinate of the logo
    */
   public ShieldLogo(final int x, final int y, final int radius) {
       super(x, y, radius);
   }

   @Override
   public void onCollided() {
      this.setAlive(false);
      GameStatistics.getInstance().increase(GameStatistics.StatisticsAction.LOGO);
   }

   @Override
   public Bitmap getLogoBitmap(Context context) {
      final Bitmap decodedResource = BitmapFactory
              .decodeResource(context.getResources(), R.mipmap.shield_logo);
      return Bitmap.createScaledBitmap(decodedResource, this.getRadius() * 2,
              this.getRadius() * 2, false);
   }
}
