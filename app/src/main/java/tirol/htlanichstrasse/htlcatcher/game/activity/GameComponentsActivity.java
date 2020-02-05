package tirol.htlanichstrasse.htlcatcher.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import tirol.htlanichstrasse.htlcatcher.R;

/**
 * @author Nicolaus Rossi
 * @since 02.02.2020
 */

public class GameComponentsActivity extends AppCompatActivity {

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   public void onPlayButtonClicked(final View view) {
      final Intent intent = new Intent(this, GameActivity.class);
      intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
      startActivity(intent);
   }

   public void onBackButtonClicked(final View view) {
      startActivity(new Intent(this, InstructionActivity.class));
   }
}
