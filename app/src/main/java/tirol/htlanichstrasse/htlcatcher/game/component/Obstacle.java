package tirol.htlanichstrasse.htlcatcher.game.component;

import android.graphics.Rect;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.game.GameState;
import tirol.htlanichstrasse.htlcatcher.game.GameView;
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
    * Obstacle random
    */
   private static Random random = new Random();

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
    * UNIX timestamp of the last wiggle direction change
    */
   private long lastObstacleTurn = 0L;

   /**
    * Boolean for wiggle direction
    */
   private boolean obstacleTurned = false;

   /**
    * Determines whether this obstacle is "alive"; it's not alive if it has reached the end of the
    * screen. Obstacles in the "not alive" state need to be "revived"/reset before they can be
    * reused
    */
   @Setter
   private boolean alive = false;

   /**
    * Determines whether the player has already received a point by dodging this obstacle
    */
   @Setter
   private boolean done = false;

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
         obstacleXDelta *= 2;
      } else if (gameState == GameState.INGAME2) {
         obstacleXDelta *= 1.5;
      }
      upperPart.left -= obstacleXDelta;
      upperPart.right -= obstacleXDelta;
      lowerPart.left -= obstacleXDelta;
      lowerPart.right -= obstacleXDelta;

      // wiggle for wiggling obstacles
      if (!wiggles) {
         return;
      }

      // lets go wiggling
      final int coefficient =
          CatcherConfig.getInstance().getObstacleWiggleDeltaY() * (obstacleTurned ? 1
              : -1);
      upperPart.top += coefficient;
      upperPart.bottom += coefficient;
      lowerPart.top -= coefficient;
      lowerPart.bottom -= coefficient;

      // change wiggle direction if time is over
      if (System.currentTimeMillis() > lastObstacleTurn + CatcherConfig.getInstance()
          .getObstacleWiggleDelay()) {
         lastObstacleTurn = System.currentTimeMillis();
         obstacleTurned = !obstacleTurned;
      }
   }

   /**
    * Resets ("revives") this obstacle with given properties (no new instance, saves RAM)
    *
    * @param screenWidth the width of the viewport
    * @param topHeight the height of the upper obstacle part
    * @param gap the gap between the top obstacles
    */
   public void resetObstacle(final int screenWidth, final int screenHeight, int topHeight,
       int gap) {
      // status
      alive = true;
      done = false;

      // wiggles (move top obstacle a bit to avoid)
      wiggles = random.nextBoolean();
      obstacleTurned = false;
      lastObstacleTurn = 0L;

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

   /**
    * Checks if the player has successfully dodged this obstacle
    *
    * @param cursor the player cursor
    * @return true if the player has dodged this obstacle, false otherwise
    */
   public boolean isPlayerThrough(final Cursor cursor) {
      return cursor.x > this.upperPart.centerX();
   }

}
