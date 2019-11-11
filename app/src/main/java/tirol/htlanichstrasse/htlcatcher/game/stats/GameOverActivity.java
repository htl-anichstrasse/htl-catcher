package tirol.htlanichstrasse.htlcatcher.game.stats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.htlanich.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.MainActivity;
import tirol.htlanichstrasse.htlcatcher.game.GameActivity;
import tirol.htlanichstrasse.htlcatcher.game.stats.CatcherStatistics.StatisticsAction;

/**
 * Activity for game over screen
 *
 * @author Nicolaus Rossi
 * @author Joshua Winkler
 * @since 23.10.2019
 */
public class GameOverActivity extends AppCompatActivity implements OnClickListener {

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_gameover);
      final View currentView = findViewById(android.R.id.content);

      // Load stats into view
      final CatcherStatistics catcherStatistics = CatcherStatistics.getInstance();
      final LinearLayout linearLayout = currentView.findViewById(R.id.linearLayout);
      final TextView scoreText = new TextView(this);
      scoreText
          .setText(getString(R.string.gameover_score, catcherStatistics.getPoints().get()));
      scoreText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

      final TextView timeText = new TextView(this);
      final int seconds = catcherStatistics.points.get() - (catcherStatistics.caughtLogos.get()
          * StatisticsAction.LOGO.getPoints());
      timeText.setText(getString(R.string.gameover_gametime, seconds));
      timeText.setGravity(Gravity.CENTER);

      linearLayout.addView(scoreText);
      linearLayout.addView(timeText);

      // Restart-button listener
      currentView.findViewById(R.id.restartButton).setOnClickListener(this);
      // currentView.findViewById(R.id.backToMenu).setOnClickListener(this);
   }

   /**
    * Called on restart button click to start a new game.
    *
    * @param view the restart-button
    */

   @Override
   public void onClick(final View view) {
      Intent intent = new Intent(this, GameActivity.class);
      intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
      startActivity(intent);
   }

   /**
    * Called on main-menu button click to jump back to the main menu.
    * @param view the main-menu button
    */

   public void backToMainMenu(final View view) {
      startActivity(new Intent(this, MainActivity.class));
   }

}