package tirol.htlanichstrasse.htlcatcher.game.component;

import android.graphics.Rect;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.util.Config;

/**
 * Represents an obstacle in the game's view
 *
 * @author Joshua Winkler
 * @since 06.11.2019
 */
@Getter
public final class Obstacle {

   /**
    * The upper part of the obstacle
    */
   private Rect upperPart;

   /**
    * The lower part of the obstacle
    */
   private Rect lowerPart;

   /**
    * Determines whether this obstacle is "alive"; it's not alive if it has reached the end of the
    * screen. Obstacles in the "not alive" state need to be "revived"/reset before they can be
    * reused
    */
   @Setter
   private boolean alive = false;

   /**
    * Creates a new dead obstacle on the GameView canvas
    */
   public Obstacle() {
      this.upperPart = new Rect();
      this.lowerPart = new Rect();
   }

   /**
    * Moves this obstacle to the left on the GameView canvas
    */
   public void move() {
      final int obstacleXDelta = Config.getInstance().getObstacleXDelta();
      upperPart.left -= obstacleXDelta;
      upperPart.right -= obstacleXDelta;
      lowerPart.left -= obstacleXDelta;
      lowerPart.right -= obstacleXDelta;
   }

   /**
    * Resets ("revives") this obstacle with given properties
    *
    * @param screenWidth the width of the viewport
    * @param topHeight the height of the upper obstacle part
    * @param gap the gap between the top obstacles
    */
   public void resetObstacle(final int screenWidth, final int screenHeight, final int topHeight,
       final int gap) {
      alive = true;

      // reset upper part
      upperPart.left = screenWidth;
      upperPart.top = 0;
      upperPart.right = screenWidth + Config.getInstance().getObstacleWidth();
      upperPart.bottom = topHeight;

      // reset lower part
      lowerPart.left = screenWidth;
      lowerPart.top = topHeight + gap;
      lowerPart.right = screenWidth + Config.getInstance().getObstacleWidth();
      lowerPart.bottom = screenHeight;
   }

}
