package org.htlanich.htlcatcher.game;

import static org.htlanich.htlcatcher.R.mipmap.htllogo_round;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import lombok.Setter;
import org.htlanich.htlcatcher.R;
import org.htlanich.htlcatcher.util.ViewPoint;

/**
 * Manages the game's display, calculations for rendering take place here
 *
 * @author Joshua Winkler
 * @author Albert Greinöcher
 * @since 06.11.17
 */
public class GameView extends View {

  /**
   * Holds the speed of HTL logos
   */
  @Getter
  private int speed = 0;

  /**
   * Ticks the game for redrawing the canvas
   */
  @Getter
  private Timer gameTicker;

  /**
   * Contains a list with all caught logos
   */
  private List<ViewPoint> logosCaught = new ArrayList<>();

  /**
   * The first player icon bitmap
   */
  @Setter
  private Bitmap meBm;

  /**
   * The second player icon bitmap
   */
  @Setter
  private Bitmap meBm2;

  /**
   * Contains a bitmap for the HTL logo (these will be spawned at the right side of the screen)
   */
  private final Bitmap htlLogo;

  /**
   * Paint instance used for the canvas
   */
  private final Paint paint = new Paint();

  /**
   * Random generator for game's rendering
   */
  private final Random random = new Random();

  /**
   * Holds the current position of the cursor
   */
  @Getter
  @Setter
  private ViewPoint cursorPoint;

  /**
   * Logos currently on the canvas
   */
  @Getter
  private List<ViewPoint> logos;

  /**
   * Contains a time value determining when the cursor icon was swapped for the last time
   */
  private long iconTimestamp = System.currentTimeMillis();

  /**
   * Determines the cursor icon to use, true if the first icon should be used, false otherwise
   */
  private boolean open = false;

  public GameView(final Context context) {
    super(context);
    final Bitmap decodedResource = BitmapFactory
        .decodeResource(context.getResources(), htllogo_round);
    this.htlLogo = Bitmap.createScaledBitmap(decodedResource, 40, 40, false);

    // Speed timer
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        speed++;
      }
    }, 0, 100 * 30);

    // Redraw timer
    final GameView gameView = this;
    this.gameTicker = new Timer();
    this.gameTicker.schedule(new TimerTask() {
      @Override
      public void run() {
        gameView.invalidate();
      }
    }, 0, 10);

    this.logos = new ArrayList<>();
    int cx = this.getWidth() / 2;
    int cy = this.getHeight() / 2;
    this.cursorPoint = new ViewPoint(cx, cy);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    // Set color
    canvas.drawColor(Color.BLACK);

    // Mirror icons
    cursorPoint.x = cursorPoint.x % this.getWidth();
    cursorPoint.y = cursorPoint.y % this.getHeight();
    if (cursorPoint.x < 0) {
      cursorPoint.x = this.getWidth();
    }
    if (cursorPoint.y < 0) {
      cursorPoint.y = this.getHeight();
    }

    // Swap first and second icon every 0.5s
    long now = System.currentTimeMillis();
    if (now - iconTimestamp > 500) {
      iconTimestamp = now;
      open = !open;
    }
    if (open) {
      canvas.drawBitmap(meBm, cursorPoint.x, cursorPoint.y, paint);
    } else {
      canvas.drawBitmap(meBm2, cursorPoint.x, cursorPoint.y, paint);
    }

    // Move logos on canvas
    for (int i = 0; i < logos.size(); i++) {
      logos.get(i).x = logos.get(i).x - speed;
      canvas.drawBitmap(htlLogo, logos.get(i).x, logos.get(i).y, paint);
    }

    // Check caught logos (intersecting with cursor)
    for (final ViewPoint intersectingPoint : cursorPoint.intersect(logos, 100)) {
      if (!logosCaught.contains(intersectingPoint)) {
        logosCaught.add(intersectingPoint);
        logos.remove(intersectingPoint);
      }
    }

    // Add new logos to screen if max amount of logos has not been reached (max logo amount = 10)
    for (int i = logos.size(); i < 10; i++) {
      // TODO: Preallocate memory for new HTL logos
      logos.add(new ViewPoint(this.getWidth(), random.nextInt(this.getHeight())));
    }
  }

  /**
   * Checks if the player has lost the game because a logo has passed the left side of the screen
   *
   * @return true if the player has lost, false otherwise
   */
  public boolean lost() {
    for (final ViewPoint logo : logos) {
      if (logo.x < 0) {
        return true;
      }
    }
    return false;
  }

}
