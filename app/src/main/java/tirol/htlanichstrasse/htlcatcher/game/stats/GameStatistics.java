package tirol.htlanichstrasse.htlcatcher.game.stats;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class holding different kinds of statistics for a game of HTL Catcher
 *
 * @author Joshua Winkler
 * @since 24.10.19
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStatistics {

   /**
    * Singleton instance of GameStatistics, remember resetting before re-using
    */
   @Getter
   private static GameStatistics instance = new GameStatistics();

   /**
    * Statically resets the singleton instance, resetting every game
    */
   public static void reset() {
      GameStatistics.instance = new GameStatistics();
   }

   /**
    * Holds a UNIX timestamp of when the game stage was changed the last time (or the game was
    * started)
    */
   @Setter
   private long gameStageChanged = 0L;

   /**
    * Holds a UNIX timestamp of the game's start
    */
   @Setter
   private long gameStarted = 0L;

   /**
    * Holds the total amount of points collected during gameplay
    */
   public AtomicInteger points = new AtomicInteger(0);

   /**
    * Holds the total amount of caught logos
    */
   public AtomicInteger logos = new AtomicInteger(0);

   /**
    * Increases the amount of points accordingly to the fulfilled action
    *
    * @param action the fulfilled action awarding the player points
    */
   public void increase(final StatisticsAction action) {
      if (action == StatisticsAction.LOGO) {
         logos.incrementAndGet();
      }
      points.addAndGet(action.getPoints());
   }

   /**
    * Enumerates all actions which add points to the game's total point counter
    *
    * @author Joshua Winkler
    * @since 05.11.2019
    */
   public enum StatisticsAction {
      SECOND(1), OBSTACLE(1), LOGO(10), BONUSLOGO(10);


      /**
       * Determines the worth of this action
       */
      @Getter
      private int points;

      StatisticsAction(final int points) {
         this.points = points;
      }
   }

}
