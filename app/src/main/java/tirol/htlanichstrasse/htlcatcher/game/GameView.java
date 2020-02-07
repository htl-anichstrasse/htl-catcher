package tirol.htlanichstrasse.htlcatcher.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.activity.GameActivity;
import tirol.htlanichstrasse.htlcatcher.game.component.Cursor;
import tirol.htlanichstrasse.htlcatcher.game.component.Logo;
import tirol.htlanichstrasse.htlcatcher.game.component.Obstacle;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameStatistics;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameStatistics.StatisticsAction;
import tirol.htlanichstrasse.htlcatcher.util.CatcherConfig;

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
   private Bitmap playerBitmap;

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
    * Paint instance used for text stroke on the canvas
    */
   private final Paint textStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

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
   private Cursor cursor = new Cursor(0, 0, CatcherConfig.getInstance().getCursorRadius());

   /**
    * Holds logo currently on the canvas
    */
   private Logo logo = new Logo(-999, -999, CatcherConfig.getInstance().getLogoRadius());

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
    * Holds a list of all obstacles currently on the screen
    */
   private Obstacle[] obstacles = new Obstacle[CatcherConfig.getInstance().getObstaclesMaxAmount()];

   /**
    * Timestamp when the last obstacle was spawned
    */
   private long lastObstacleSpawned = 0L;

   /**
    * Bitmap of the lower obstacle parts
    */
   private Bitmap obstacleBitmap;

   /**
    * Bitmap of the upper obstacle parts
    */
   private Bitmap flippedObstacleBitmap;

   /**
    * The activity for this view
    */
   @Setter
   private GameActivity activity;

   /**
    * Creates new GameView
    */
   public GameView(final Context context, @Nullable final AttributeSet attributeSet) {
      super(context, attributeSet);

      // initialize logo bitmap
      final Bitmap decodedResource = BitmapFactory
          .decodeResource(context.getResources(), R.mipmap.htllogo_round);
      this.htlLogo = Bitmap.createScaledBitmap(decodedResource, logo.getRadius() * 2,
          logo.getRadius() * 2, false);

      // initialize game statistics
      GameStatistics.reset();

      // initialize obstacles
      for (int i = 0; i < obstacles.length; i++) {
         obstacles[i] = new Obstacle();
      }
      this.obstacleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.obstacle);
      Matrix matrix = new Matrix();
      matrix.preScale(1.0F, -1.0F);
      this.flippedObstacleBitmap = Bitmap
          .createBitmap(obstacleBitmap, 0, 0, obstacleBitmap.getWidth(), obstacleBitmap.getHeight(),
              matrix, true);
   }

   @Override
   protected void onDraw(final Canvas canvas) {
      super.onDraw(canvas);

      // Initialize cursor and logo
      if (init) {
         cursor.x = CatcherConfig.getInstance().getCursorInitialX();
         cursor.y = getHeight() / 2 - cursor.getRadius();
         final int logoMargin = CatcherConfig.getInstance().getLogoMargin();
         logo.resetLogo(getWidth(), logoMargin + logo.getRadius(),
             getHeight() - (logoMargin + logo.getRadius()),
             random);
         init = false;
      }

      // Draw / move cursor
      renderCursor(canvas);

      // Only execute if game has already started
      if (GameState.isInGame(gameState)) {
         // Check game state
         final long delay =
             gameState == GameState.INGAME ? CatcherConfig.getInstance().getStage2Time()
                 : CatcherConfig.getInstance().getStage3Time();
         if (System.currentTimeMillis()
             > GameStatistics.getInstance().getGameStageChanged() + delay) {
            switch (gameState) {
               case INGAME:
                  gameState = GameState.INGAME2;
                  activity.changeGameStage(gameState);
                  break;
               case INGAME2:
                  gameState = GameState.INGAME3;
                  activity.changeGameStage(gameState);
                  break;
            }
            GameStatistics.getInstance().setGameStageChanged(System.currentTimeMillis());
         }

         // Check lose
         if (lost()) {
            gameState = GameState.END;
         }

         // Renders / moves obstacles on canvas
         renderObstacle(canvas);

         // Renders / moves logos on canvas
         renderLogo(canvas);

         // Renders statistics (point display)
         renderStatistics(canvas);
      }

      // Redraw
      if (gameState == GameState.END) {
         return;
      }
      postInvalidateOnAnimation();
   }

   /**
    * Renders the player cursor onto the canvas
    *
    * @param canvas the canvas to render the cursor on
    */
   private void renderCursor(final Canvas canvas) {
      if (gameState == GameState.START) {
         // Swap direction
         if (System.currentTimeMillis() > cursor.getStartLastTurn() + CatcherConfig.getInstance()
             .getCursorStartChangeDelay()) {
            cursor.setStartDirection(!cursor.isStartDirection());
            cursor.setStartLastTurn(System.currentTimeMillis());
         }
         cursor.y += cursor.isStartDirection() ? -1 : 1;
      } else {
         // Velocity / gravity calculation
         cursor.y += cursor.getYVelocity();
         cursor
             .setYVelocity(cursor.getYVelocity() + CatcherConfig.getInstance().getCursorGravity());
      }
      // Draw player bitmap on canvas
      canvas.drawBitmap(playerBitmap, cursor.x - cursor.getRadius(), cursor.y - cursor.getRadius(),
          paint);
   }

   /**
    * Renders obstacles onto the canvas (and moves them)
    *
    * @param canvas the canvas to render the obstacles on
    */
   private void renderObstacle(final Canvas canvas) {
      // Spawn new obstacle
      long obstacleSpawnDelay = CatcherConfig.getInstance().getObstacleSpawnDelay();
      if (gameState == GameState.INGAME3) {
         obstacleSpawnDelay /= 8;
      } else if (gameState == GameState.INGAME2) {
         obstacleSpawnDelay /= 4;
      }
      if (System.currentTimeMillis() > lastObstacleSpawned + obstacleSpawnDelay) {
         for (Obstacle obstacle : obstacles) {
            if (!obstacle.isAlive()) {
               // respawn obstacle!
               final int topHeight = random.nextInt(this.getHeight() / 6) + 50;
               final int gap =
                   random.nextInt(this.getHeight() / 6) + topHeight + CatcherConfig.getInstance()
                       .getObstacleMinGap();

               obstacle.resetObstacle(getWidth(), getHeight(), topHeight, gap);
               break;
            }
         }
         // set flag for delay
         lastObstacleSpawned = System.currentTimeMillis();
      }
      // Rendering; Move alive obstacles and kill obstacles which have left the screen
      for (Obstacle obstacle : obstacles) {
         if (obstacle.isAlive()) {
            // Draw upper part (flipped bitmap)
            final Rect upperPart = obstacle.getUpperPart();
            upperPart.top =
                (flippedObstacleBitmap.getHeight() - upperPart.bottom) * -1;  // no vert scale
            canvas.drawBitmap(flippedObstacleBitmap, null, upperPart, null);
            // Draw lower part
            final Rect lowerPart = obstacle.getLowerPart();
            lowerPart.bottom = lowerPart.top + obstacleBitmap.getHeight(); // no vert scale
            canvas.drawBitmap(obstacleBitmap, null, lowerPart, null);
            // Move to left
            obstacle.move(gameState);
            // Check if obstacle has passed player
            if (!obstacle.isDone() && obstacle.isPlayerThrough(this.cursor)) {
               obstacle.setDone(true);
               GameStatistics.getInstance().increase(StatisticsAction.OBSTACLE);
            }
            if (upperPart.right < 0) {
               obstacle.setAlive(false);
            }
         }
      }
   }

   /**
    * Renders the current logo
    *
    * @param canvas the canvas the render the logo on
    */
   private void renderLogo(final Canvas canvas) {
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
            GameStatistics.getInstance().increase(StatisticsAction.LOGO);
         }
         // Wiggle logo a bit
         if (System.currentTimeMillis() > logo.getLastTurn() + CatcherConfig.getInstance()
             .getCursorStartChangeDelay()) {
            logo.setYDirection(!logo.isYDirection());
            logo.setLastTurn(System.currentTimeMillis());
         }
         // Move upwards as long as logo is below floor
         if (logo.y + logo.getRadius() > this.getHeight() - activity.getFloor().getHeight()) {
            logo.setYDirection(true);
         }
         logo.y += logo.isYDirection() ? -2 : 2;
         // Draw logo on canvas
         canvas.drawBitmap(htlLogo, logo.x - logo.getRadius(), logo.y - logo.getRadius(), paint);
      } else {
         // Spawn new logo
         if (System.currentTimeMillis() > lastLogoDied + (
             (CatcherConfig.getInstance().getLogoMinDelay() + random.nextInt(3)) * 1000L)) {
            final int logoMargin = CatcherConfig.getInstance().getLogoMargin();
            logo.resetLogo(getWidth(), logoMargin + logo.getRadius(),
                getHeight() - (logoMargin + logo.getRadius()),
                random);
            if (gameState == GameState.INGAME2) {
               logo.setSpeed(logo.getSpeed() * 2);
            } else if (gameState == GameState.INGAME3) {
               logo.setSpeed(logo.getSpeed() * 3);
            }
         }
      }
   }

   /**
    * Renders the statistics text onto the canvas
    *
    * @param canvas the canvas the render the statistics on
    */
   private void renderStatistics(final Canvas canvas) {
      // Setup text paint & stroke paint
      textPaint.setColor(Color.WHITE);
      textPaint.setTextSize((int) TypedValue
          .applyDimension(TypedValue.COMPLEX_UNIT_SP, 60, getResources().getDisplayMetrics()));
      textPaint.setTextAlign(Align.LEFT);
      textPaint.setTypeface(Typeface.create("Courier New", Typeface.BOLD));
      textPaint.setFakeBoldText(true);
      textStrokePaint.setStyle(Style.STROKE);
      textStrokePaint.setStrokeWidth(8);
      textStrokePaint.setColor(Color.BLACK);
      textStrokePaint.setTextSize((int) TypedValue
          .applyDimension(TypedValue.COMPLEX_UNIT_SP, 60, getResources().getDisplayMetrics()));
      textStrokePaint.setTextAlign(Align.LEFT);
      textStrokePaint.setTypeface(Typeface.create("Courier New", Typeface.BOLD));
      textStrokePaint.setFakeBoldText(true);

      // Calculate position, width and height
      final FontMetrics metric = textPaint.getFontMetrics();
      final int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
      final int y = (int) (textHeight - metric.descent);
      final String text = String.valueOf(GameStatistics.getInstance().getPoints().get());

      // Draw text onto canvas
      canvas.drawText(text, getWidth() / 2.0f - textPaint.measureText(text) / 2.0f, y + 50,
          textPaint);
      canvas.drawText(text, getWidth() / 2.0f - textStrokePaint.measureText(text) / 2.0f,
          y + 50,
          textStrokePaint);
   }

   /**
    * Checks if the player has lost the game because the cursor has left the screen (or hit an
    * obstacle / the floor)
    *
    * @return true if the player has lost, false otherwise
    */
   public boolean lost() {
      // If cursor has left the screen
      final int cursorRadius = CatcherConfig.getInstance().getCursorRadius();
      boolean lost = cursor.x - cursorRadius < 0
          || cursor.x + cursorRadius > getWidth()
          || cursor.y - cursorRadius < 0
          || cursor.y + cursorRadius > getHeight();

      // Check floor collision
      lost |= activity.getFloor().isCursorCollided(cursor, this);

      // Don't check obstacle if player has left screen
      if (lost) {
         return true;
      }

      // If cursor has hit obstacle
      for (Obstacle obstacle : obstacles) {
         if (obstacle.isAlive()) {
            lost |= obstacle.isCursorCollided(cursor);
         }
      }

      // we done
      return lost;
   }

}
