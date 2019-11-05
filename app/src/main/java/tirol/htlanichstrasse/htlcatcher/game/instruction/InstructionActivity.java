package tirol.htlanichstrasse.htlcatcher.game.instruction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import org.htlanich.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.MainActivity;
import tirol.htlanichstrasse.htlcatcher.game.GameActivity;

/**
 * Shows instructions for playing the game
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
         case R.id.playButton:
            final Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
            startActivity(intent);
            break;
         case R.id.backToMainMenuButton:
            startActivity(new Intent(this, MainActivity.class));
            break;
      }
   }

}
