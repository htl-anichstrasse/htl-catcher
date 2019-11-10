package tirol.htlanichstrasse.htlcatcher.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.util.Timer;
import java.util.TimerTask;
import tirol.htlanichstrasse.htlcatcher.util.Config;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameOverActivity;
import tirol.htlanichstrasse.htlcatcher.game.component.Cursor;

/**
 * Manages game controls
 *
 * @author Nicolaus Rossi
 * @author Albert Greinöcker
 * @since 06.11.17
 */
@SuppressWarnings("FieldCanBeLocal")
@SuppressLint("ClickableViewAccessibility")
public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

   /**
    * Static logging tag used for loggings from this class
    */
   private static String LOG_TAG = "GAME_ACTIVITY";

   /**
    * GameView class attached to this activity
    */
   private GameView gameView;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Instantiate view for activity
      this.gameView = new GameView(this);

      // Set icons
      if (getIntent().getExtras() != null) {
         this.gameView
             .setPlayerBitmap(BitmapFactory.decodeFile(getIntent().getExtras().getString("player_bm")));
      } else {
         Log.e(LOG_TAG, "Could not fetch intent extras bundle");
      }
      gameView.setOnTouchListener(this);

      // Register game timer
      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            // Check loss
            if (gameView.lost()) {
               cancel();
               finish();
               gameView.setGameState(GameState.END);
               startActivity(new Intent(GameActivity.this, GameOverActivity.class));
            }
         }
      }, 0, 50);

      // Set window fullscreen
      this.requestWindowFeature(Window.FEATURE_NO_TITLE);
      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);

      // Register view
      setContentView(gameView);
   }

   @Override
   public boolean onTouch(final View view, final MotionEvent event) {
      // Change game state
      if (gameView.getGameState() == GameState.START) {
         gameView.setLastPointTimestamp(System.currentTimeMillis());
         gameView.setGameState(GameState.INGAME);
      }

      // Accelerate cursor in y position
      final Cursor gameCursor = gameView.getCursor();
      final int acceleration =
          gameCursor.getYVelocity() < 0 ? 5 : Config.getInstance().getCursorAcceleration();
      gameCursor.setYVelocity(
          gameCursor.getYVelocity() - acceleration);
      return true;
   }

}