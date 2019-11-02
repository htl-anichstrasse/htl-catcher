package tirol.htlanichstrasse.htlcatcher.game;

import static org.htlanich.htlcatcher.R.mipmap.htllogo_round;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.view.View;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.Config;
import tirol.htlanichstrasse.htlcatcher.game.stats.CatcherStatistics;
import tirol.htlanichstrasse.htlcatcher.util.Logo;
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
    * Holds a UNIX timestamp determining when the last logo died
    */
   private long lastLogoDied = 0L;

   /**
    * Holds the current position of the cursor
    */
   @Getter
   @Setter
   private ViewPoint cursorPoint = new ViewPoint(0, 0);

   /**
    * Holds logo currently on the canvas
    */
   private Logo logo = new Logo(-999, -999);

   /**
    * Determinse whether this is the first draw on the screen
    */
   private boolean init = true;

   /**
    * Creates new GameView
    */
   public GameView(final Context context) {
      super(context);

      // initialize logo bitmap
      final Bitmap decodedResource = BitmapFactory
          .decodeResource(context.getResources(), htllogo_round);
      this.htlLogo = Bitmap
          .createScaledBitmap(decodedResource, Config.getInstance().getLogoRadius() * 2,
              Config.getInstance().getLogoRadius() * 2, false);

      // initialize game statistics
      CatcherStatistics.reset();
      CatcherStatistics.getInstance().setStartTime(System.currentTimeMillis());

      // canvas background
      LinearGradient linearGradient = new LinearGradient(0, 0, 0, getHeight(),
          Color.rgb(93, 106, 162),
          Color.rgb(29, 33, 50), TileMode.MIRROR);
      this.paint.setShader(linearGradient);
   }

   @Override
   protected void onDraw(final Canvas canvas) {
      super.onDraw(canvas);

      // Initialize cursor and logo
      if (init) {
         this.cursorPoint.x = Config.getInstance().getInitCursorX();
         this.cursorPoint.y = this.getHeight() / 2;
         final int logoMargin = Config.getInstance().getLogoMargin();
         this.logo
             .resetLogo(this.getWidth(), logoMargin + Config.getInstance().getLogoRadius(),
                 this.getHeight() - (logoMargin + Config.getInstance().getLogoRadius()),
                 this.random);
         init = false;
      }

      // Draw background
      canvas.drawPaint(paint);

      // Draw cursor
      canvas.drawBitmap(meBm, cursorPoint.x, cursorPoint.y, paint);

      // Move logo on canvas (and redraw if alive)
      if (logo.isAlive()) {
         logo.x = logo.x - logo.getSpeed();
         canvas.drawBitmap(htlLogo, logo.x, logo.y, paint);
         if (logo.x + Config.getInstance().getLogoRadius() < 0) {
            logo.setAlive(false);
            this.lastLogoDied = System.currentTimeMillis();
         }
      }

      // Check caught logos (intersecting with cursor)
      if (cursorPoint.intersect(logo, 50)) {
         logo.setAlive(false);
         this.lastLogoDied = System.currentTimeMillis();
         CatcherStatistics.getInstance().incrementLogoCount();
      }

      // Spawn new logo
      if (!logo.isAlive()) {
         if (System.currentTimeMillis() > this.lastLogoDied + (
             (Config.getInstance().getMinDelayBetweenLogos() + this.random.nextInt(3)) * 1000L)) {
            final int logoMargin = Config.getInstance().getLogoMargin();
            logo.resetLogo(this.getWidth(), logoMargin + Config.getInstance().getLogoRadius(),
                this.getHeight() - (logoMargin + Config.getInstance().getLogoRadius()),
                this.random);
         }
      }

      // Redraw
      this.invalidate();
   }

   /**
    * Checks if the player has lost the game because the cursor has left the screen (or hit an
    * obstacle)
    *
    * @return true if the player has lost, false otherwise
    */
   public boolean lost() {
      return this.cursorPoint.x < 0 || this.cursorPoint.x > this.getWidth()
          || this.cursorPoint.y < 0
          || this.cursorPoint.y > this.getHeight();
   }

}
