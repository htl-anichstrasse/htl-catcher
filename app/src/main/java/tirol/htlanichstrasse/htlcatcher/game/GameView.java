package tirol.htlanichstrasse.htlcatcher.game;

import static org.htlanich.htlcatcher.R.mipmap.htllogo_round;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Shader.TileMode;
import android.util.TypedValue;
import android.view.View;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.htlanich.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.Config;
import tirol.htlanichstrasse.htlcatcher.game.stats.CatcherStatistics;
import tirol.htlanichstrasse.htlcatcher.game.stats.CatcherStatistics.StatisticsAction;
import tirol.htlanichstrasse.htlcatcher.util.Cursor;
import tirol.htlanichstrasse.htlcatcher.util.Logo;

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
    * Paint instance used for text on the canvas
    */
   private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

   /**
    * Random generator for game's rendering
    */
   private final Random random = new Random();

   /**
    * Holds a UNIX timestamp determining when the last logo died
    */
   private long lastLogoDied = 0L;

   /**
    * Holds an instance of the player cursor
    */
   @Getter
   @Setter
   private Cursor cursor = new Cursor(0, 0, Config.getInstance().getCursorRadius());

   /**
    * Holds logo currently on the canvas
    */
   private Logo logo = new Logo(-999, -999, Config.getInstance().getLogoRadius());

   /**
    * Determines whether this is the first draw on the screen
    */
   private boolean init = true;

   /**
    * Determines the current game state
    */
   @Getter
   @Setter
   public GameState gameState = GameState.START;

   /**
    * Timestamp when the last point for surviving was awarded
    */
   @Setter
   private long lastPointTimestamp = 0L;

   /**
    * Creates new GameView
    */
   public GameView(final Context context) {
      super(context);

      // initialize logo bitmap
      final Bitmap decodedResource = BitmapFactory
          .decodeResource(context.getResources(), htllogo_round);
      this.htlLogo = Bitmap.createScaledBitmap(decodedResource, this.logo.getRadius() * 2,
          this.logo.getRadius() * 2, false);

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
         cursor.x = Config.getInstance().getCursorInitialX();
         cursor.y = this.getHeight() / 2 - cursor.getRadius();
         final int logoMargin = Config.getInstance().getLogoMargin();
         logo.resetLogo(this.getWidth(), logoMargin + Config.getInstance().getLogoRadius(),
             this.getHeight() - (logoMargin + Config.getInstance().getLogoRadius()),
             random);
         init = false;
      }

      // Award point
      if (System.currentTimeMillis() > lastPointTimestamp + 1000L) {
         CatcherStatistics.getInstance().increase(StatisticsAction.SECOND);
         lastPointTimestamp = System.currentTimeMillis();
      }

      // Draw background
      canvas.drawPaint(paint);

      // Draw / move cursor
      if (gameState == GameState.START) {
         // Swap direction
         if (System.currentTimeMillis() > cursor.getStartLastTurn() + Config.getInstance()
             .getCursorStartChangeDelay()) {
            cursor.setStartDirection(!cursor.isStartDirection());
            cursor.setStartLastTurn(System.currentTimeMillis());
         }
         cursor.y += cursor.isStartDirection() ? -1 : 1;
      } else {
         cursor.y += cursor.getYVelocity();
         cursor.setYVelocity(cursor.getYVelocity() + Config.getInstance().getCursorGravity());
      }
      canvas.drawBitmap(meBm, cursor.x, cursor.y, paint);

      // Only execute if game has already started
      if (gameState == GameState.INGAME) {
         // Move logo on canvas (and redraw if alive)
         if (logo.isAlive()) {
            logo.x = logo.x - logo.getSpeed();
            // Check if logo has left the screen
            if (logo.x + logo.getRadius() < 0) {
               logo.setAlive(false);
               lastLogoDied = System.currentTimeMillis();
            }
            // Check caught logos (intersecting with cursor)
            if (cursor.intersect(logo)) {
               logo.setAlive(false);
               lastLogoDied = System.currentTimeMillis();
               CatcherStatistics.getInstance().increase(StatisticsAction.LOGO);
            }
            canvas.drawBitmap(htlLogo, logo.x, logo.y, paint);
         } else {
            // Spawn new logo
            if (System.currentTimeMillis() > lastLogoDied + (
                (Config.getInstance().getLogoMinDelay() + random.nextInt(3)) * 1000L)) {
               final int logoMargin = Config.getInstance().getLogoMargin();
               logo.resetLogo(this.getWidth(), logoMargin + logo.getRadius(),
                   this.getHeight() - (logoMargin + logo.getRadius()),
                   random);
            }
         }

         // Draw statistics
         textPaint.setColor(Color.WHITE);
         textPaint.setTextSize((int) TypedValue
             .applyDimension(TypedValue.COMPLEX_UNIT_SP, 50, getResources().getDisplayMetrics()));
         textPaint.setTextAlign(Align.LEFT);
         final FontMetrics metric = textPaint.getFontMetrics();
         final int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
         final int y = (int) (textHeight - metric.descent);
         canvas.drawText(getResources()
                 .getString(R.string.game_points, CatcherStatistics.getInstance().getPoints().get()),
             10, y, textPaint);
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
      return this.cursor.x < 0 || this.cursor.x > this.getWidth()
          || this.cursor.y < 0
          || this.cursor.y > this.getHeight();
   }

}
