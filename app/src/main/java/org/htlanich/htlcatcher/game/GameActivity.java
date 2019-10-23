package org.htlanich.htlcatcher.game;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;
import org.htlanich.htlcatcher.util.ViewPoint;

/**
 * Manages game controls
 *
 * @author Albert Greinöcker
 * @since 06.11.17
 */
@SuppressLint("ClickableViewAccessibility")
public class GameActivity extends AppCompatActivity implements SensorEventListener,
    View.OnTouchListener {

  private static String LOG_TAG = "GAME_ACTIVITY";

  private GameView gameView;
  private SensorManager sensorManager;
  private boolean on = true;

  float[] gravitation = null;
  float[] magnetField = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Instantiate view for activity
    this.gameView = new GameView(this);

    // Sensor manager, set icons
    this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    if (getIntent().getExtras() != null) {
      this.gameView
          .setMeBm(BitmapFactory.decodeFile(getIntent().getExtras().getString("player_bm")));
      this.gameView
          .setMeBm2(BitmapFactory.decodeFile(getIntent().getExtras().getString("player_bm2")));
    } else {
      Log.e(LOG_TAG, "Could not fetch intent extras bundle");
    }
    gameView.setOnTouchListener(this);

    // Register game timer
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        // Redraw canvas
        gameView.invalidate();

        // Check loss
        if (gameView.lost()) {
          on = false;
          // Toast.makeText(this, getString(R.string.game_lost), Toast.LENGTH_LONG).show();
        }
      }
    }, 0, 10);

    // Register view
    setContentView(gameView);
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Register sensor listeners for catcher controls
    sensorManager.registerListener(this,
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        SensorManager.SENSOR_DELAY_NORMAL);
    sensorManager.registerListener(this,
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onPause() {
    super.onPause();

    // Don't use sensors in paused app state
    sensorManager.unregisterListener(this);
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (on) {
      float[] sensorValues = event.values.clone();
      switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
          gravitation = sensorValues;

          break;
        case Sensor.TYPE_MAGNETIC_FIELD:
          magnetField = sensorValues;
        default:
          return;
      }

      if (gravitation == null || magnetField == null) {
        return;
      }

      float[] rotationMatrix = new float[9];

      if (!SensorManager.getRotationMatrix(rotationMatrix, null, gravitation, magnetField)) {
        Log.d(LOG_TAG, "Getrotationmatrix_error");
        return;
      }

      float[] orientation = new float[3];
      SensorManager.getOrientation(rotationMatrix, orientation);

      float pitch = Math.round(Math.toDegrees(event.values[0]));
      float roll = Math.round(Math.toDegrees(event.values[1]));

      int x = (int) (gameView.getCursorPoint().x - pitch * 0.1);
      int y = (int) (gameView.getCursorPoint().y + roll * 0.1);
      gameView.setCursorPoint(new ViewPoint(x, y));
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // might use this for debug purposes, for now, ignore sensor accuarcy changes
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    gameView.setCursorPoint(new ViewPoint((int) event.getX(), (int) event.getY()));
    return true;
  }
}
