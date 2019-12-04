package tirol.htlanichstrasse.htlcatcher.game.stage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.GameState;

/**
 * @author Nicolaus Rossi
 * @since 04.12.2019
 */

public class GameStageThree implements GameStage {

   private Activity activity;

   public GameStageThree(final Activity activity) {
      this.activity = activity;
   }

   @Override
   public void onStage(final GameState gameStage) {
      final int shortAnimationDuration = activity.getResources()
          .getInteger(android.R.integer.config_shortAnimTime);

      final View redBackgroundView = activity.findViewById(R.id.scrolling_background_red);
      redBackgroundView.setVisibility(View.VISIBLE);
      redBackgroundView.setAlpha(0f);
      redBackgroundView.animate().alpha(1f).setDuration(shortAnimationDuration)
          .setListener(null);

      final View yellowBackgroundView = activity.findViewById(R.id.scrolling_background_yellow);
      yellowBackgroundView.animate().alpha(0f)
          .setDuration(shortAnimationDuration)
          .setListener(new AnimatorListenerAdapter() {
             @Override
             public void onAnimationEnd(Animator animation) {
                yellowBackgroundView.setVisibility(View.GONE);
             }
          });

      final View stage3Text = activity.findViewById(R.id.stage3);
      stage3Text.setVisibility(View.VISIBLE);
      stage3Text.setAlpha(0f);
      stage3Text.animate().alpha(1f).rotation(0f).setDuration(shortAnimationDuration)
          .setInterpolator(new AccelerateDecelerateInterpolator())
          .setListener(new AnimatorListenerAdapter() {
             @Override
             public void onAnimationEnd(Animator animation) {
                new Handler()
                    .postDelayed(() -> activity.runOnUiThread(() -> stage3Text.animate().alpha(0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                           @Override
                           public void onAnimationEnd(Animator animation) {
                              stage3Text.setVisibility(View.GONE);
                           }
                        })), 1000L);
             }
          });
   }

}

