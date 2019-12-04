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
 * @since 26.11.2019
 */

public class GameStageTwo implements GameStage {

   /**
    * Object of the respective activity as specific methods cannot be accessed in a static context
    * outside of an activity class.
    */
   private Activity activity;

   public GameStageTwo(final Activity activity) {
      this.activity = activity;
   }

   @Override
   public void onStage(final GameState gameStage) {
      final int shortAnimationDuration = activity.getResources()
          .getInteger(android.R.integer.config_shortAnimTime);

      final View yellowBackgroundView = activity.findViewById(R.id.scrolling_background_yellow);
      yellowBackgroundView.setVisibility(View.VISIBLE);
      yellowBackgroundView.setAlpha(0f);
      yellowBackgroundView.animate().alpha(1f).setDuration(shortAnimationDuration)
          .setListener(null);

      final View backgroundView = activity.findViewById(R.id.scrolling_background);
      backgroundView.animate().alpha(0f).setDuration(shortAnimationDuration)
          .setListener(new AnimatorListenerAdapter() {
             @Override
             public void onAnimationEnd(Animator animator) {
                backgroundView.setVisibility(View.GONE);
             }
          });

      final View stageTwoText = activity.findViewById(R.id.stage2);
      stageTwoText.setVisibility(View.VISIBLE);
      stageTwoText.setAlpha(0f);
      stageTwoText.animate().alpha(1f).translationY(0f).setDuration(shortAnimationDuration)
          .setInterpolator(new AccelerateDecelerateInterpolator())
          .setListener(new AnimatorListenerAdapter() {
             @Override
             public void onAnimationEnd(Animator animation) {
                new Handler()
                    .postDelayed(() -> activity.runOnUiThread(() -> stageTwoText.animate().alpha(0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                           @Override
                           public void onAnimationEnd(Animator animation) {
                              stageTwoText.setVisibility(View.GONE);
                           }
                        })), 1000L);
             }
          });
   }

}
