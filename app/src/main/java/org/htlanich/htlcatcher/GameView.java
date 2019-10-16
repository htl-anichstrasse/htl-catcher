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

  private List<ViewPoint> logosCatched = new ArrayList<>();
  private String meBmPath;
  private Bitmap meBm = null;
  private String meBmPath2;
  private Bitmap meBm2 = null;
  private final Bitmap htlLogo;
  private Paint p = new Paint();
  private ViewPoint plP;
  private List<ViewPoint> logos;
  private int noLogos = 10;

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
    this.plP = new ViewPoint(cx, cy);
    for (int i = 0; i < this.noLogos; i++) {
      int rndX = this.getWidth();
      int rndY = NumberUtils.rnd(this.getHeight());
      this.logos.add(new ViewPoint(rndX, rndY));
    }
  }

  private void message(String msg) {
    Toast.makeText(GameView.this.getContext(), msg, Toast.LENGTH_SHORT).show();
  }

  public void setMeBmPath(String meBmPath) {
    this.meBmPath = meBmPath;
    this.meBm = ImageUtils.readBmFromFile(meBmPath);

  }

  public void setMeBmPath2(String meBmPath2) {
    this.meBmPath2 = meBmPath2;
    this.meBm2 = ImageUtils.readBmFromFile(meBmPath2);

  }

  public int getCx() {
    return plP.x;
  }

  public int getCy() {
    return plP.y;
  }

  public void setPlPoint(int x, int y) {
    plP = new ViewPoint(x, y);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawColor(Color.BLACK);

    //p.setColor(Color.RED);
    //canvas.drawCircle(cx, cy, 30, p);
    plP.x = plP.x % this.getWidth();
    plP.y = plP.y % this.getHeight();

    if (plP.x < 0) {
      plP.x = this.getWidth();
    }
    if (plP.y < 0) {
      plP.y = this.getHeight();
    }

    long now = new Date().getTime();
    if (now - start > 500) {
      start = now;
      open = !open;
    }

    if (open) {
      canvas.drawBitmap(meBm, plP.x, plP.y, p);
    } else {
      canvas.drawBitmap(meBm2, plP.x, plP.y, p);
    }

    for (int i = 0; i < logos.size(); i++) {
      logos.set(i, new ViewPoint(logos.get(i).x - speed, logos.get(i).y));
      canvas.drawBitmap(htlLogo, logos.get(i).x, logos.get(i).y, p);

    }

    List<ViewPoint> toDel = new ViewPoint(plP.x, plP.y).intersect(logos, 100);
    for (ViewPoint td : toDel) {
      if (!logosCatched.contains(td)) {
        logosCatched.add(td);
        logos.remove(td);
        //Toast.makeText(getContext(), "Catched:" + logosCatched.size(), Toast.LENGTH_SHORT).show();
      }
    }

    for (int i = logos.size(); i < noLogos; i++) {
      int rndX = this.getWidth();
      int rndY = NumberUtils.rnd(this.getHeight());
      logos.add(new ViewPoint(rndX, rndY));
    }
  }

  public boolean lost() {
    for (ViewPoint p : logos) {
      if (p.x < 0) {
        return true;
      }
    }
    return false;
  }

}
