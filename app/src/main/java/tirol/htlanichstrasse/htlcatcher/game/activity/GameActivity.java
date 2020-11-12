package tirol.htlanichstrasse.htlcatcher.game.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.q42.android.scrollingimageview.ScrollingImageView;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.GameState;
import tirol.htlanichstrasse.htlcatcher.game.GameView;
import tirol.htlanichstrasse.htlcatcher.game.component.Floor;
import tirol.htlanichstrasse.htlcatcher.game.logos.LogoModeManager;
import tirol.htlanichstrasse.htlcatcher.game.stage.GameStageThree;
import tirol.htlanichstrasse.htlcatcher.game.stage.GameStageTwo;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameStatistics;
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

   /**
    * GameStage and therefore difficulty level: '2'
    */
   private GameStageTwo gameStageTwo = new GameStageTwo(this);

   /**
    * GameStage and therefore difficulty level: '3'
    */
   private GameStageThree gameStageThree = new GameStageThree(this);

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

      // Randomisation of stage timers to add some more dynamic gameplay
      CatcherConfig.getInstance().setStage2Time(ThreadLocalRandom.current().nextLong(15000, 30000));
      CatcherConfig.getInstance().setStage3Time(ThreadLocalRandom.current().nextLong(45000, 60000));

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
                          (System.currentTimeMillis() - gameStatistics.getGameStarted()) / 1000L));
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
      switch (gameStage) {
         case INGAME:
            break;
         case INGAME2:
            gameStageTwo.onStage(GameState.INGAME2);
            break;
         case INGAME3:
            gameStageThree.onStage(GameState.INGAME3);
            break;
         default:
            throw new IllegalArgumentException("Can only change to ingame stages!");
      }
   }

   /*
    * Event handlers come after this
    */

   @Override
   public boolean onTouch(final View view, final MotionEvent event) {
      // Change game state
      if (gameView.getGameState() == GameState.START) {
         gameView.setGameState(GameState.INGAME);
         GameStatistics.getInstance().setGameStarted(System.currentTimeMillis());
         GameStatistics.getInstance().setGameStageChanged(System.currentTimeMillis());
      }

      // Set the cursors speed so that it jumps up
      final int cursorGravity = CatcherConfig.getInstance().getCursorJumpSpeed() * (LogoModeManager.getInstance().getCurrentModes().contains(LogoModeManager.Mode.INVERT) ? 1 : -1);
      gameView.getCursor().setYVelocity(cursorGravity);
      return true;
   }

   @Override
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.restartButton:
            // restart the game activity (this one)
            finish();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
            startActivity(intent);
            break;
         case R.id.backToMenu:
            // go back to the main activity
            finish();
            startActivity(new Intent(this, MainActivity.class));
            break;
         case R.id.leaderbordButton:
            new SubmitScoreFragment().show(getSupportFragmentManager(), "SubmitScore");
            break;
      }
   }

}

