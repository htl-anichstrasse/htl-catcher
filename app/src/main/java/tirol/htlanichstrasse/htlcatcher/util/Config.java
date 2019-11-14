package tirol.htlanichstrasse.htlcatcher.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Config class holding general application constants for the HTL Catcher game
 *
 * @author Joshua Winkler
 * @since 02.11.19
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

   /**
    * Application-wide config instance (singleton)
    */
   @Getter
   private static final Config instance = new Config();

   /**
    * The time for a player to be in a stage for the game to become harder
    */
   private long stageTime = 10000;

   /**
    * The time for the background transition
    */
   private int stageAnimationTime = 500;

   /*
      LOGO CONFIGURATION
    */

   /**
    * The minimum speed the logo random generate may use
    */
   private int logoMinSpeed = 7;

   /**
    * Pixel margin from lower and upper bound of the screen for random Y coordinate of logos — make
    * sure to not use a too high value here as the margin might quickly be greater than the screen
    * height in landscape mode
    */
   private int logoMargin = 150;

   /**
    * Logo radius in pixels
    */
   private int logoRadius = 70;

   /**
    * The minimum delay between 2 logos spawning on the screen in seconds
    */
   private long logoMinDelay = 5;

   /**
    * The direction delay for logos in y direction
    */
   private long logoStartChangeDelay = 500L;

   /*
      CURSOR CONFIGURATION
    */

   /**
    * The initial X position of the player cursor
    */
   private int cursorInitialX = 200;

   /**
    * Cursor radius in pixels
    */
   private int cursorRadius = 40;

   /**
    * Constant gravity value
    */
   private int cursorGravity = 1;

   /**
    * Y-Velocity of the cursor in the negative direction on touch
    */
   private int cursorJumpSpeed = 13;

   /**
    * The direction delay in the start phase in y direction
    */
   private long cursorStartChangeDelay = 1000L;

   /*
    * OBSTACLE CONFIGURATION
    */

   /**
    * The speed of obstacles for moving to the left; increases over time
    */
   private int obstacleXDelta = 6;

   /**
    * Minimum gap between two obstacles in pixels
    */
   private int obstacleMinGap = 350;

   /**
    * Maximum gap between two obstacles in pixels
    */
   private int obstacleMaxGap = 550;

   /**
    * Delay in milliseconds between two obstacles spawning
    */
   private long obstacleSpawnDelay = 4500L;

   /**
    * Max amount of obstacles to be on screen simultaneously
    */
   private int obstaclesMaxAmount = 40;

   /**
    * Constant width for all obstacles
    */
   private int obstacleWidth = 150;

}
