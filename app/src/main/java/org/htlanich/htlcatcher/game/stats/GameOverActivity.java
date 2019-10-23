package org.htlanich.htlcatcher.game.stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
  }
}
