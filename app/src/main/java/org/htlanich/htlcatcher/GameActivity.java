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

@SuppressLint("ClickableViewAccessibility")
public class GameActivity extends AppCompatActivity implements SensorEventListener,
    View.OnTouchListener {

  private GameView gameView;
  private SensorManager sensorManager;
  private boolean on = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.gameView = new GameView(this);
    this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    if (getIntent().getExtras() != null) {
      this.gameView.setMeBm(getIntent().getExtras().getString("player_bm"));
      this.gameView.setMeBm2(getIntent().getExtras().getString("player_bm2"));
    }

    setContentView(gameView);
    gameView.setOnTouchListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();

    //Sensor acc = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    //sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);

    Sensor accS = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(this, accS, SensorManager.SENSOR_DELAY_NORMAL);

    Sensor mfS = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    sensorManager.registerListener(this, mfS, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onPause() {
    super.onPause();
    sensorManager.unregisterListener(this);
  }

  float[] gravitation = null;
  float[] magnetField = null;

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

      int x = (int) (gameView.getCx() - pitch * 0.1);
      int y = (int) (gameView.getCy() + roll * 0.1);
      gameView.setCursorPoint(x, y);

      if (gameView.lost()) {
        on = false;
        Toast.makeText(this, getString(R.string.game_lost), Toast.LENGTH_LONG).show();
      }
    }
  }

  //@Override
  public void onSensorChanged2(SensorEvent event) {
    if (on) {
      //TODO just add pitch and roll to gameview, not the point
      float pitch = event.values[2];
      float roll = event.values[1];
      int x = (int) (gameView.getCx() - pitch * (gameView.getSpeed() * 10000 + 1));
      int y = (int) (gameView.getCy() - roll * (gameView.getSpeed() * 10000 + 1));
      gameView.setCursorPoint(x, y);
      gameView.invalidate();

      if (gameView.lost()) {
        on = false;
        Toast.makeText(this, getString(R.string.game_lost), Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
    // dismiss
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    gameView.setCursorPoint((int) event.getX(), (int) event.getY());
    return true;
  }
}
