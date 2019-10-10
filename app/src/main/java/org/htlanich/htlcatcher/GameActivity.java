package org.htlanich.htlcatcher;

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

public class GameActivity extends AppCompatActivity implements SensorEventListener,
    View.OnTouchListener {

  private GameView mv;
  private SensorManager sm;
  private boolean on = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String bmPath = getIntent().getExtras().getString("player_bm");
    String bmPath2 = getIntent().getExtras().getString("player_bm2");
    mv = new GameView(this);
    setContentView(mv);
    mv.setMeBmPath(bmPath);
    mv.setMeBmPath2(bmPath2);
    mv.setOnTouchListener(this);
    //setContentView(R.layout.activity_game);
  }

  @Override
  protected void onResume() {
    super.onResume();
    sm = (SensorManager) getSystemService(SENSOR_SERVICE);

    //Sensor acc = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    //sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);

    Sensor accS = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sm.registerListener(this, accS, SensorManager.SENSOR_DELAY_NORMAL);

    Sensor mfS = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    sm.registerListener(this, mfS, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onPause() {
    super.onPause();
    sm.unregisterListener(this);
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

      int x = (int) (mv.getCx() - pitch * 0.1);
      int y = (int) (mv.getCy() + roll * 0.1);
      mv.setPlPoint(x, y);
      mv.invalidate();

      if (mv.lost()) {
        on = false;
        Toast.makeText(this, "lost!!", Toast.LENGTH_LONG).show();
      }
    }
  }

  //@Override
  public void onSensorChanged2(SensorEvent event) {
    if (on) {
      //TODO just add pitch and roll to gameview, not the point
      float pitch = event.values[2];
      float roll = event.values[1];
      int x = (int) (mv.getCx() - pitch * (mv.getSpeed() * 10000 + 1));
      int y = (int) (mv.getCy() - roll * (mv.getSpeed() * 10000 + 1));
      mv.setPlPoint(x, y);
      mv.invalidate();

      if (mv.lost()) {
        on = false;
        Toast.makeText(this, "lost!!", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
    //DO NOTHING
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {

    mv.setPlPoint((int) event.getX(), (int) event.getY());
    mv.invalidate();
    return true;
  }
}
