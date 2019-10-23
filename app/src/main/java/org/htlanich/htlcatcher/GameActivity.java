package org.htlanich.htlcatcher;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import org.htlanich.htlcatcher.utils.ImageUtils;

/**
 * Manages game controls
 *
 * @author Albert Greinöcher
 * @since 06.11.17
 */
@SuppressLint("ClickableViewAccessibility")
public class GameActivity extends AppCompatActivity implements SensorEventListener,
    View.OnTouchListener {

  private GameView gameView;
  private SensorManager sensorManager;
  private boolean on = true;

  float[] gravitation = null;
  float[] magnetField = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.gameView = new GameView(this);
    this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    if (getIntent().getExtras() != null) {
      this.gameView
          .setMeBm(ImageUtils.readBmFromFile(getIntent().getExtras().getString("player_bm")));
      this.gameView
          .setMeBm2(ImageUtils.readBmFromFile(getIntent().getExtras().getString("player_bm2")));
    }

    setContentView(gameView);
    gameView.setOnTouchListener(this);
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
      float[] werte = event.values.clone();
      switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
          gravitation = werte;

          break;
        case Sensor.TYPE_MAGNETIC_FIELD:
          magnetField = werte;
        default:
          return;
      }

      if (gravitation == null || magnetField == null) {
        return;
      }

      float[] rotationMatrix = new float[9];

      if (!SensorManager.getRotationMatrix(rotationMatrix, null, gravitation, magnetField)) {
        Log.d("HTL_CATCHER", "Getrotationmatrix_error");
        return;
      }

      float[] orientation = new float[3];
      SensorManager.getOrientation(rotationMatrix, orientation);

      float pitch = Math.round(Math.toDegrees(event.values[0]));
      float roll = Math.round(Math.toDegrees(event.values[1]));

      int x = (int) (gameView.getCursorPoint().x - pitch * 0.1);
      int y = (int) (gameView.getCursorPoint().y + roll * 0.1);
      gameView.setCursorPoint(new ViewPoint(x, y));

      if (gameView.lost()) {
        on = false;
        Toast.makeText(this, getString(R.string.game_lost), Toast.LENGTH_LONG).show();
      }
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
