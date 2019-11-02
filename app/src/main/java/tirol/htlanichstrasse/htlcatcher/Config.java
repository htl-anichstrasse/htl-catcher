package tirol.htlanichstrasse.htlcatcher;

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
    * The minimum speed the logo random generate may use
    */
   private int minLogoSpeed = 5;

   /**
    * Pixel margin from lower and upper bound of the screen for random Y coordinate of logos — make
    * sure to not use a too high value here as the margin might quickly be greater than the screen
    * height in landscape mode
    */
   private int logoMargin = 150;

   /**
    * Logo radius in pixels
    */
   private int logoRadius = 60;

   /**
    * The minimum delay between 2 logos spawning on the screen in seconds
    */
   private long minDelayBetweenLogos = 5;

   /**
    * The initial X position of the player cursor
    */
   private int initCursorX = 30;

}
