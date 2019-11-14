package tirol.htlanichstrasse.htlcatcher.game.stats;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class holding different kinds of statistics for a game of HTL Catcher
 *
 * @author Joshua Winkler
 * @since 24.10.19
 */
@Getter
@NoArgsConstructor
public class CatcherStatistics {

   /**
    * Singleton instance of CatcherStatistics, remember resetting before re-using
    */
   @Getter
   private static CatcherStatistics instance = new CatcherStatistics();

   /**
    * Statically resets the singleton instance, resetting every game
    */
   public static void reset() {
      CatcherStatistics.instance = new CatcherStatistics();
   }

   /**
    * Holds the total amount of points collected during gameplay
    */
   public AtomicInteger points = new AtomicInteger(0);

   /**
    * Holds the total amount of caught logos
    */
   public AtomicInteger caughtLogos = new AtomicInteger(0);

   /**
    * Increases the amount of points accordingly to the fulfilled action
    *
    * @param action the fulfilled action awarding the player points
    */
   public void increase(final StatisticsAction action) {
      if (action == StatisticsAction.LOGO) {
         caughtLogos.incrementAndGet();
      }
      this.points.addAndGet(action.getPoints());
   }

   /**
    * Enumerates all actions which add points to the game's total point counter
    *
    * @author Joshua Winkler
    * @since 05.11.2019
    */
   public enum StatisticsAction {
      SECOND(1), LOGO(10);

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
