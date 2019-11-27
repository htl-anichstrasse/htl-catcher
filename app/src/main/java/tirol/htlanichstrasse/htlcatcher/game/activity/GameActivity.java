package tirol.htlanichstrasse.htlcatcher.game.activity;

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
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import com.q42.android.scrollingimageview.ScrollingImageView;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import tirol.htlanichstrasse.htlcatcher.MainActivity;
import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.GameState;
import tirol.htlanichstrasse.htlcatcher.game.GameStatistics;
import tirol.htlanichstrasse.htlcatcher.game.GameView;
import tirol.htlanichstrasse.htlcatcher.game.component.Floor;
import tirol.htlanichstrasse.htlcatcher.game.GameStatistics.StatisticsAction;
import tirol.htlanichstrasse.htlcatcher.util.CatcherConfig;

/**
 * Manages game controls
 *
 * @author Nicolaus Rossi
 * @author Albert Greinöcker
 * @since 06.11.17
 */
@SuppressWarnings("FieldCanBeLocal")
@SuppressLint("ClickableViewAccessibility")
public class GameActivity extends AppCompatActivity implements View.OnTouchListener,
    OnClickListener {

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
               runOnUiThread(() -> {
                  final int shortAnimationDuration = getResources()
                      .getInteger(android.R.integer.config_shortAnimTime);
                  final View gameOverView = findViewById(R.id.gameOver);
                  gameOverView.setVisibility(View.VISIBLE);
                  gameOverView.setAlpha(0f);
                  gameOverView.animate().alpha(1f).setDuration(shortAnimationDuration)
                      .setListener(null);
                  ((ScrollingImageView) findViewById(R.id.scrolling_background)).setSpeed(0f);
                  ((ScrollingImageView) findViewById(R.id.scrolling_background_yellow))
                      .setSpeed(0f);
                  ((ScrollingImageView) findViewById(R.id.scrolling_background_red)).setSpeed(0f);
                  ((ScrollingImageView) findViewById(R.id.scrolling_floor)).setSpeed(0f);
                  ((ScrollingImageView) findViewById(R.id.scrolling_clouds)).setSpeed(0f);

                  // Load stats into view
                  final GameStatistics gameStatistics = GameStatistics.getInstance();
                  ((TextView) findViewById(R.id.pointsView)).setText(
                      getString(R.string.gameover_score, gameStatistics.getPoints().get()));
                  ((TextView) findViewById(R.id.timeView)).setText(
                      getString(R.string.gameover_gametime,
                          gameStatistics.points.get() - (gameStatistics.caughtLogos.get()
                              * StatisticsAction.LOGO.getPoints())));
               });
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
      final int shortAnimationDuration = getResources()
          .getInteger(android.R.integer.config_shortAnimTime);
      switch (gameStage) {
         case INGAME:
         case INGAME2: {
            final View yellowBackgroundView = findViewById(R.id.scrolling_background_yellow);
            yellowBackgroundView.setVisibility(View.VISIBLE);
            yellowBackgroundView.setAlpha(0f);
            yellowBackgroundView.animate().alpha(1f).setDuration(shortAnimationDuration)
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
            final View stage2Text = findViewById(R.id.stage2);
            stage2Text.setVisibility(View.VISIBLE);
            stage2Text.setAlpha(0f);
            stage2Text.animate().alpha(1f).translationY(0f).setDuration(shortAnimationDuration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(
                    new AnimatorListenerAdapter() {
                       @Override
                       public void onAnimationEnd(Animator animation) {
                          new Handler()
                              .postDelayed(() -> runOnUiThread(() -> stage2Text.animate().alpha(0f)
                                  .setDuration(shortAnimationDuration)
                                  .setListener(new AnimatorListenerAdapter() {
                                     @Override
                                     public void onAnimationEnd(Animator animation) {
                                        stage2Text.setVisibility(View.GONE);
                                     }
                                  })), 1000L);
                       }
                    });
         }
         break;
         case INGAME3: {
            final View redBackgroundView = findViewById(R.id.scrolling_background_red);
            redBackgroundView.setVisibility(View.VISIBLE);
            redBackgroundView.setAlpha(0f);
            redBackgroundView.animate().alpha(1f).setDuration(shortAnimationDuration)
                .setListener(null);
            final View yellowBackgroundView = findViewById(R.id.scrolling_background_yellow);
            yellowBackgroundView.animate().alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                   @Override
                   public void onAnimationEnd(Animator animation) {
                      yellowBackgroundView.setVisibility(View.GONE);
                   }
                });
            final View stage3Text = findViewById(R.id.stage3);
            stage3Text.setVisibility(View.VISIBLE);
            stage3Text.setAlpha(0f);
            stage3Text.animate().alpha(1f).rotation(0f).setDuration(shortAnimationDuration)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                   @Override
                   public void onAnimationEnd(Animator animation) {
                      new Handler()
                          .postDelayed(() -> runOnUiThread(() -> stage3Text.animate().alpha(0f)
                              .setDuration(shortAnimationDuration)
                              .setListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                    stage3Text.setVisibility(View.GONE);
                                 }
                              })), 1000L);
                   }
                });
         }
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
         GameStatistics.getInstance().setGameStageChanged(System.currentTimeMillis());
      }

      // Set the cursors speed so that it jumps up
      gameView.getCursor().setYVelocity(-CatcherConfig.getInstance().getCursorJumpSpeed());
      return true;
   }

   @Override
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.restartButton:
            finish();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
            startActivity(intent);
            break;
         case R.id.backToMenu:
            finish();
            startActivity(new Intent(this, MainActivity.class));
            break;
      }
   }

}
