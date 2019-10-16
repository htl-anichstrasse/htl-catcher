package org.htlanich.htlcatcher;

import static org.htlanich.htlcatcher.R.mipmap.htllogo_round;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import org.htlanich.htlcatcher.utils.ImageUtils;
import org.htlanich.htlcatcher.utils.NumberUtils;

/**
 * Created by albert on 06.11.17.
 */
public class GameView extends View {

  @Getter
  private int speed = 0;

  private final int MAX_LOGO_AMOUNT = 10;

  private List<ViewPoint> logosCaught = new ArrayList<>();
  private Bitmap meBm = null;
  private Bitmap meBm2 = null;
  private final Bitmap htlLogo;
  private Paint paint = new Paint();
  private ViewPoint cursorPoint;
  private List<ViewPoint> logos;

  long start = new Date().getTime();
  boolean open = false;

  public GameView(final Context context) {
    super(context);
    final Bitmap decodedResource = BitmapFactory
        .decodeResource(context.getResources(), htllogo_round);
    this.htlLogo = ImageUtils.scaleBm(decodedResource, 40, 40);

    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        speed++;
      }
    }, 0, 100 * 30);

    this.logos = new ArrayList<>();
    int cx = this.getWidth() / 2;
    int cy = this.getHeight() / 2;
    this.cursorPoint = new ViewPoint(cx, cy);
    for (int i = 0; i < this.MAX_LOGO_AMOUNT; i++) {
      int rndX = this.getWidth();
      int rndY = NumberUtils.rnd(this.getHeight());
      this.logos.add(new ViewPoint(rndX, rndY));
    }
  }

  private void message(String msg) {
    Toast.makeText(GameView.this.getContext(), msg, Toast.LENGTH_SHORT).show();
  }

  public void setMeBm(String meBmPath) {
    this.meBm = ImageUtils.readBmFromFile(meBmPath);

  }

  public void setMeBm2(String meBmPath2) {
    this.meBm2 = ImageUtils.readBmFromFile(meBmPath2);
  }

  public int getCx() {
    return cursorPoint.x;
  }

  public int getCy() {
    return cursorPoint.y;
  }

  public void setCursorPoint(int x, int y) {
    this.cursorPoint = new ViewPoint(x, y);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawColor(Color.BLACK);

    //paint.setColor(Color.RED);
    //canvas.drawCircle(cx, cy, 30, paint);
    cursorPoint.x = cursorPoint.x % this.getWidth();
    cursorPoint.y = cursorPoint.y % this.getHeight();

    if (cursorPoint.x < 0) {
      cursorPoint.x = this.getWidth();
    }
    if (cursorPoint.y < 0) {
      cursorPoint.y = this.getHeight();
    }

    long now = new Date().getTime();
    if (now - start > 500) {
      start = now;
      open = !open;
    }

    if (open) {
      canvas.drawBitmap(meBm, cursorPoint.x, cursorPoint.y, paint);
    } else {
      canvas.drawBitmap(meBm2, cursorPoint.x, cursorPoint.y, paint);
    }

    for (int i = 0; i < logos.size(); i++) {
      logos.set(i, new ViewPoint(logos.get(i).x - speed, logos.get(i).y));
      canvas.drawBitmap(htlLogo, logos.get(i).x, logos.get(i).y, paint);

    }

    for (ViewPoint intersectingPoints : cursorPoint.intersect(logos, 100)) {
      if (!logosCaught.contains(intersectingPoints)) {
        logosCaught.add(intersectingPoints);
        logos.remove(intersectingPoints);
      }
    }

    for (int i = logos.size(); i < MAX_LOGO_AMOUNT; i++) {
      int rndX = this.getWidth();
      int rndY = NumberUtils.rnd(this.getHeight());
      logos.add(new ViewPoint(rndX, rndY));
    }
  }

  public boolean lost() {
    return logos.stream().anyMatch(logo -> logo.x < 0);
  }

}
