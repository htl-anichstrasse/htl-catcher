package tirol.htlanichstrasse.htlcatcher.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import tirol.htlanichstrasse.htlcatcher.R;

/**
 * Shows instructions on how to play the game.
 *
 * @author Nicolaus Rossi
 * @since 31.10.2019
 */
public class InstructionActivity extends AppCompatActivity implements OnClickListener {

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_instruction);
      final View activeView = findViewById(R.id.instruction);
      activeView.findViewById(R.id.playButton).setOnClickListener(this);
   }

   @Override
   public void onClick(final View view) {
      switch (view.getId()) {
         case R.id.gameComponents:
            final Intent intent = new Intent(this, GameComponentsActivity.class);
            intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
            break;

         case R.id.jumpBack:
            startActivity(new Intent(this, MainActivity.class));
            break;
      }
   }

}
