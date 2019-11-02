package tirol.htlanichstrasse.htlcatcher;

import lombok.Data;

/**
 * Config class holding general application constants for the HTL Catcher game
 *
 * @author Joshua Winkler
 * @since 02.11.19
 */
@Data
public class Config {

   /**
    * Application-wide config instance (singleton)
    */
   private static final Config INSTANCE = new Config();

   /**
    * The max amount of logos to exist on screen simultaneously
    */
   private int maxLogos = 30;

}
