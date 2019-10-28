package org.htlanich.htlcatcher.game.stats;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class holding different kinds of statistics for a game of HTL Catcher
 *
 * @author Nicolaus Rossi
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
   * Holds the amount of caught logos
   */
  private AtomicInteger logoCount = new AtomicInteger();

  /**
   * Holds the start time of the game
   */
  @Setter
  private long startTime = 0L;

  /**
   * Safely increments the logo count by one
   */
  public void incrementLogoCount() {
    this.logoCount.incrementAndGet();
  }
}
