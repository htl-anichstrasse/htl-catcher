package tirol.htlanichstrasse.htlcatcher.game.component;

import android.graphics.Rect;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.game.GameState;
import tirol.htlanichstrasse.htlcatcher.util.CatcherConfig;

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
    * true if the obstacle wiggles, false otherwise
    */
   private boolean wiggles = false;

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
    *
    * @param gameState the current gameState
    */
   public void move(final GameState gameState) {
      int obstacleXDelta = CatcherConfig.getInstance().getObstacleXDelta();
      if (gameState == GameState.INGAME3) {
         obstacleXDelta *= 4;
      } else if (gameState == GameState.INGAME2) {
         obstacleXDelta *= 2;
      }
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
      upperPart.right = screenWidth + CatcherConfig.getInstance().getObstacleWidth();
      upperPart.bottom = topHeight;

      // reset lower part
      lowerPart.left = screenWidth;
      lowerPart.top = topHeight + gap;
      lowerPart.right = screenWidth + CatcherConfig.getInstance().getObstacleWidth();
      lowerPart.bottom = screenHeight;

      // wiggles
      wiggles = new Random().nextBoolean();
   }

   /**
    * Checks if the given cursor is collided with this obstacle
    *
    * @param cursor the cursor to check collision with
    * @return true if the cursor is collided, false otherwise
    */
   public boolean isCursorCollided(final Cursor cursor) {
      boolean collided = false;

      // Upper obstacle
      for (Rect part : new Rect[]{upperPart, lowerPart}) {
         // calculate edges to test
         float testX = cursor.x;
         float testY = cursor.y;

         // x axis
         if (cursor.x < part.left) {
            testX = part.left;
         } else if (cursor.x > part.right) {
            testX = part.right;
         }
         // y axis
         if (cursor.y < part.top) {
            testY = part.top;
         } else if (cursor.y > part.bottom) {
            testY = part.bottom;
         }

         // calculate edge distance
         float distX = cursor.x - testX;
         float distY = cursor.y - testY;
         double distance = Math.sqrt((distX * distX) + (distY * distY));

         // check collision
         if (distance < cursor.getRadius()) {
            collided = true;
            break;
         }
      }

      // if ya didnt know then now u know
      return collided;
   }

}
