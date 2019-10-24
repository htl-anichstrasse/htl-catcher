package org.htlanich.htlcatcher.game.stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.htlanich.htlcatcher.R;

/**
 * Activity for game over screen
 *
 * @author Joshua Winkler
 * @since 23.10.19
 */
public class GameOverActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gameover);

    // Load stats into view
    final CatcherStatistics catcherStatistics = CatcherStatistics.getInstance();
    final LinearLayout linearLayout = findViewById(android.R.id.content)
        .findViewById(R.id.linearLayout);
    final TextView scoreText = new TextView(this);
    scoreText.setText(
        getString(R.string.game_over_score, catcherStatistics.getLogoCount().get()));
    scoreText.setGravity(Gravity.CENTER);
    linearLayout.addView(scoreText);
    final TextView timeText = new TextView(this);
    final int seconds = (int) ((System.currentTimeMillis() - catcherStatistics.getStartTime()) / 1000L);
    timeText.setText(getString(R.string.game_over_time, seconds));
    timeText.setGravity(Gravity.CENTER);
    linearLayout.addView(timeText);
  }
}
