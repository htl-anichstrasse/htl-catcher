package org.htlanich.htlcatcher.game.instruction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import org.htlanich.htlcatcher.R;
import org.htlanich.htlcatcher.game.GameActivity;
import org.htlanich.htlcatcher.intro.MainActivity;

/**
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
      startActivity(new Intent(this, GameActivity.class));
   }

   public void jumpBackToMenu(final View view) {
      startActivity(new Intent(this, MainActivity.class));
   }

}
