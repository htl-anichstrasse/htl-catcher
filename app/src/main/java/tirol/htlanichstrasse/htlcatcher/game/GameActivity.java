package tirol.htlanichstrasse.htlcatcher.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.component.Floor;
import tirol.htlanichstrasse.htlcatcher.game.stats.CatcherStatistics;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameOverActivity;
import tirol.htlanichstrasse.htlcatcher.util.Config;

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

   /**
    * The floor of this view
    */
   @Getter
   private Floor floor;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Set window fullscreen
      this.requestWindowFeature(Window.FEATURE_NO_TITLE);
      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);

      // link xml
      setContentView(R.layout.activity_game);

      // Instantiate view for activity
      floor = findViewById(R.id.scrolling_floor);
      gameView = findViewById(R.id.gameView);
      gameView.setActivity(this);

      // Set icons
      if (getIntent().getExtras() != null) {
         this.gameView
             .setPlayerBitmap(
                 BitmapFactory.decodeFile(getIntent().getExtras().getString("player_bm")));
      } else {
         Log.e(LOG_TAG, "Could not fetch intent extras bundle");
      }
      gameView.setOnTouchListener(this);

      // Register game timer
      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            // Check loss
            if (gameView.gameState == GameState.END) {
               cancel();
               finish();
               startActivity(new Intent(GameActivity.this, GameOverActivity.class));
            }
         }
      }, 0, 50);
   }

   /**
    * Changes the game stage to another ingame stage
    *
    * @param gameStage the ingame stage to change to
    * @throws IllegalArgumentException if provided gameStage is not an ingame stage
    */
   public void changeGameStage(final GameState gameStage) {
      switch (gameStage) {
         case INGAME:
         case INGAME2:
            break;
         case INGAME3:
            final int shortAnimationDuration = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);
            final View redBackgroundView = findViewById(R.id.scrolling_background_red);
            redBackgroundView.setVisibility(View.VISIBLE);
            redBackgroundView.setAlpha(0f);
            redBackgroundView.animate().alpha(1f).setDuration(shortAnimationDuration)
                .setListener(null);
            final View backgroundView = findViewById(R.id.scrolling_background);
            backgroundView.animate().alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                   @Override
                   public void onAnimationEnd(Animator animation) {
                      backgroundView.setVisibility(View.GONE);
                   }
                });
            final View suddenDeathView = findViewById(R.id.suddenDeath);
            suddenDeathView.setVisibility(View.VISIBLE);
            suddenDeathView.setAlpha(0f);
            suddenDeathView.animate().alpha(1f).rotation(0f).setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                   @Override
                   public void onAnimationEnd(Animator animation) {
                      new Handler()
                          .postDelayed(() -> runOnUiThread(() -> suddenDeathView.animate().alpha(0f)
                              .setDuration(shortAnimationDuration)
                              .setListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                    suddenDeathView.setVisibility(View.GONE);
                                 }
                              })), 1000L);
                   }
                });
            break;
         default:
            throw new IllegalArgumentException("Can only change to ingame stages!");
      }
   }

   @Override
   public boolean onTouch(final View view, final MotionEvent event) {
      // Change game state
      if (gameView.getGameState() == GameState.START) {
         gameView.setLastPointTimestamp(System.currentTimeMillis());
         gameView.setGameState(GameState.INGAME);
         CatcherStatistics.getInstance().setGameStageChanged(System.currentTimeMillis());
      }

      // Set the cursors speed so that it jumps up
      gameView.getCursor().setYVelocity(-Config.getInstance().getCursorJumpSpeed());
      return true;
   }

}
