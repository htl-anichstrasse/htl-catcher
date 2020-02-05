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

public class GameComponentsActivity extends AppCompatActivity implements OnClickListener {

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public void onClick(final View view) {
      switch(view.getId()) {
         case R.id.playButton:
            final Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
            startActivity(intent);
            break;

         case R.id.back:
            startActivity(new Intent(this, InstructionActivity.class));
            break;
      }
   }
}
