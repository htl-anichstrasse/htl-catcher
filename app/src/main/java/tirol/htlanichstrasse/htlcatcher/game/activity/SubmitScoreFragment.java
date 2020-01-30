package tirol.htlanichstrasse.htlcatcher.game.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import java.util.Objects;
import org.json.JSONObject;
import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.stats.GameStatistics;
import tirol.htlanichstrasse.htlcatcher.util.CatcherConfig;

/**
 * Dialog box for submitting scores to the remote leaderboard server
 *
 * @author Joshua Winkler
 * @since 22.01.2020
 */
public class SubmitScoreFragment extends DialogFragment {

   /**
    * Submit score fragment context
    */
   private Context context;

   @Override
   public void onAttach(Context context) {
      this.context = context;
      super.onAttach(context);
   }

   @NonNull
   @Override
   public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

      builder.setView(View.inflate(getContext(), R.layout.activity_submit_score, null))
          .setPositiveButton(R.string.submitscore_submit, null) // handle in onclick
          .setNegativeButton(R.string.submitscore_cancel,
              (dialog, id) -> SubmitScoreFragment.this.getDialog().cancel());

      final AlertDialog dialog = builder.create();

      // add click events
      dialog.setOnShowListener(dialogInterface -> {
         final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

         positiveButton.setOnClickListener(view -> {
            // Send score to remote server
            final String nameText = ((EditText) Objects.requireNonNull(this.getDialog())
                .findViewById(R.id.name)).getText().toString().trim();
            final String messageText = ((EditText) Objects.requireNonNull(this.getDialog())
                .findViewById(R.id.message)).getText().toString().trim();

            if (nameText.length() == 0) {
               Toast.makeText(this.getContext(), getString(R.string.submitscore_error_noname),
                   Toast.LENGTH_SHORT).show();
            } else {
               if (isNetworkAvailable()) {
                  sendScore(nameText, GameStatistics.getInstance().getPoints().get(), messageText);
               } else {
                  // no network connection
                  Toast.makeText(this.getContext(),
                      getString(R.string.submitscore_error_noconnection),
                      Toast.LENGTH_SHORT).show();
               }
            }
         });
      });

      return dialog;
   }

   /**
    * Sends the player's score including their name + optional message to the remote leaderboard
    * webserver
    *
    * @param name the entered name of the player
    * @param score the reached score of the player
    * @param message the (optional) message of the player, may be empty
    */
   private void sendScore(final String name, final int score, final String message) {
      final CatcherConfig config = CatcherConfig.getInstance();
      final AsyncHttpClient client = new AsyncHttpClient();
      client.setBasicAuth(config.getAPIUser(), config.getAPIPassword());

      // Prepare request params
      final RequestParams requestParams = new RequestParams();
      requestParams.put("name", name);
      requestParams.put("score", score);
      requestParams.put("message", message);

      client.post(config.getAddAPIURL(), requestParams, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (isAdded()) {
               final Activity activity = getActivity();
               if (activity != null) {
                  activity.startActivity(new Intent(context, MainActivity.class));
                  activity.finish();
               }
               if (statusCode == 200) {
                  Toast.makeText(context, getString(R.string.submitscore_submitted),
                      Toast.LENGTH_SHORT)
                      .show();
               } else {
                  Toast.makeText(context, getString(R.string.submitscore_error_conn, statusCode),
                      Toast.LENGTH_SHORT)
                      .show();
               }
            }
         }
      });
   }

   /**
    * Checks if a network connection is currently available
    *
    * @return true if a connection is available, false otherwise
    */
   private boolean isNetworkAvailable() {
      if (getContext() == null) { // NPE check
         return false;
      }
      NetworkInfo activeNetworkInfo = ((ConnectivityManager) getContext()
          .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnected();
   }

}
