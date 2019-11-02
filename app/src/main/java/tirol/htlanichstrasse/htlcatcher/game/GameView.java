package tirol.htlanichstrasse.htlcatcher.game;

import static org.htlanich.htlcatcher.R.mipmap.htllogo_round;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.game.stats.CatcherStatistics;
import tirol.htlanichstrasse.htlcatcher.util.ViewPoint;

/**
 * Manages the game's display, calculations for rendering take place here
 *
 * @author Nicolaus Rossi
 * @author Joshua Winkler
 * @author Albert Greinöcker
 * @since 06.11.17
 */
public class GameView extends View {

   /**
    * Holds the speed of HTL logos
    */
   @Getter
   private int speed = 0;

   /**
    * The first player icon bitmap
    */
   @Setter
   private Bitmap meBm;

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
    * linear gradient for canvas background
    */
   // private LinearGradient linearGradient;

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

   public GameView(final Context context) {
      super(context);
      final Bitmap decodedResource = BitmapFactory
          .decodeResource(context.getResources(), htllogo_round);
      this.htlLogo = Bitmap.createScaledBitmap(decodedResource, 40, 40, false);
      CatcherStatistics.reset(); // new game started, reset statistics
      CatcherStatistics.getInstance().setStartTime(System.currentTimeMillis());

      // Speed timer
      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            speed++;
         }
      }, 0, 100 * 30);

      this.logos = new ArrayList<>();
      int cx = this.getWidth() / 2;
      int cy = this.getHeight() / 2;
      this.cursorPoint = new ViewPoint(cx, cy);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      // linear-gradient as background
      Shader shader = new LinearGradient(0, 0, 0, getHeight(), Color.rgb(93, 106, 162),
          Color.rgb(29, 33, 50), TileMode.MIRROR);
      paint.setShader(shader);
      canvas.drawPaint(paint);

      // Mirror icons
      cursorPoint.x = cursorPoint.x % this.getWidth();
      cursorPoint.y = cursorPoint.y % this.getHeight();

      if (cursorPoint.x < 0) {
         cursorPoint.x = this.getWidth();
      }

      if (cursorPoint.y < 0) {
         cursorPoint.y = this.getHeight();
      }

      // Draw cursor
      canvas.drawBitmap(meBm, cursorPoint.x, cursorPoint.y, paint);

      // Move logos on canvas
      for (int i = 0; i < logos.size(); i++) {
         logos.get(i).x = logos.get(i).x - speed;
         canvas.drawBitmap(htlLogo, logos.get(i).x, logos.get(i).y, paint);
      }

      // Check caught logos (intersecting with cursor)
      for (final ViewPoint intersectingPoint : cursorPoint.intersect(logos, 100)) {
         synchronized (this) {
            logos.remove(intersectingPoint);
            CatcherStatistics.getInstance().incrementLogoCount();
         }
      }

      // Add new logos to screen if max amount of logos has not been reached (max logo amount = 10)
      for (int i = logos.size(); i < 10; i++) {
         logos.add(new ViewPoint(this.getWidth(), random.nextInt(this.getHeight())));
      }
      // TODO: Preallocate memory for new HTL logos
   }

   /**
    * Checks if the player has lost the game because a logo has reached the left side of the screen
    *
    * @return true if the player has lost, false otherwise
    */
   public synchronized boolean lost() {
      for (final ViewPoint logo : logos) {
         if (logo.x < 0) {
            return true;
         }
      }
      return false;
   }

}
