package org.htlanich.htlcatcher.game.stats;

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
import org.htlanich.htlcatcher.game.GameActivity;

/**
 * Activity for game over screen
 * @author Nicolaus Rossi
 * @author Joshua Winkler
 * @since 23.10.19
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
    scoreText.setText(getString(R.string.game_over_score, catcherStatistics.getLogoCount().get()));
    scoreText.setGravity(Gravity.CENTER);
    linearLayout.addView(scoreText);
    final TextView timeText = new TextView(this);
    final int seconds = (int) ((System.currentTimeMillis() - catcherStatistics.getStartTime()) / 1000L);
    timeText.setText(getString(R.string.game_over_time, seconds));
    timeText.setGravity(Gravity.CENTER);
    linearLayout.addView(timeText);

    // Button listener
    currentView.findViewById(R.id.restartButton).setOnClickListener(this);
  }

  /**
   * Called on restart button click
   *
   * @param view the button
   */
  public void onClick(final View view) {
    Intent intent = new Intent(this, GameActivity.class);
    intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
    startActivity(intent);
  }
}
