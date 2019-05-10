package org.htlanich.htlcatcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.htlanich.htlcatcher.utils.ImageUtils;
import org.htlanich.htlcatcher.utils.NumberUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static org.htlanich.htlcatcher.R.mipmap.htllogo_round;

/**
 * Created by albert on 06.11.17.
 */

public class GameView extends View {
    private int speed = 0;
    private ArrayList<PlPoint> logosCatched = new ArrayList<>();
    private String meBmPath = "";
    private Bitmap meBm = null;
    private String meBmPath2 = "";
    private Bitmap meBm2 = null;
    private int noCollected = 0;
    private Bitmap htlLogo = null;

    private Paint p = new Paint();
    private PlPoint plP;
    private ArrayList<PlPoint> logos = new ArrayList<>();
    private int noLogos = 10;
    private boolean first = true;

    Timer timer = null;
    TimerTask myTimerTask = null;

    long start = new Date().getTime();
    boolean open = false;
    public GameView(Context context) {
        super(context);
        htlLogo = BitmapFactory.decodeResource(context.getResources(), htllogo_round);
        htlLogo = ImageUtils.scaleBm(htlLogo, 40, 40);

        timer = new Timer();
        myTimerTask = new TimerTask() {
            @Override
            public void run() {
                speed++;
                
            }
        };

        timer.schedule(myTimerTask,0, 100*30);

    }

    public int getSpeed() {
        return speed;
    }
    private void message(String msg)
    {
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

    public void init()
    {
        logos = new ArrayList<>();
        int cx = this.getWidth() / 2;
        int cy = this.getHeight() / 2;
        plP = new PlPoint(cx,cy);
        for (int i = 0; i < noLogos; i++)
        {
            int rndX = this.getWidth();
            int rndY = NumberUtils.rnd(this.getHeight());
            logos.add(new PlPoint(rndX, rndY));
        }
    }

    public int getCx()
    {
        return plP.x;
    }

    public int getCy()
    {
        return plP.y;
    }

    public void setPlPoint(int x, int y)
    {
        plP = new PlPoint(x,y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (first)
        {
            init();
            first = false;
        }

        canvas.drawColor(Color.BLACK);

        //p.setColor(Color.RED);
        //canvas.drawCircle(cx, cy, 30, p);
        plP.x = plP.x % this.getWidth();
        plP.y = plP.y % this.getHeight();

        if (plP.x < 0) plP.x = this.getWidth();
        if (plP.y < 0) plP.y = this.getHeight();

        long now = new Date().getTime();
        if (now - start > 500)
        {
            start = now;
            open = !open;
        }

        if (open)
        {
            canvas.drawBitmap(meBm, plP.x, plP.y, p);
        } else
        {
            canvas.drawBitmap(meBm2, plP.x, plP.y, p);
        }

        for (int i = 0; i < logos.size(); i++)
        {
            logos.set(i, new PlPoint(logos.get(i).x - speed , logos.get(i).y));
            canvas.drawBitmap(htlLogo, logos.get(i).x, logos.get(i).y, p);

        }

        ArrayList<PlPoint> toDel = new PlPoint(plP.x,plP.y).intersect(logos, 100);
        for (PlPoint td : toDel)
        {
            if (!logosCatched.contains(td))
            {
                logosCatched.add(td);
                logos.remove(td);
                //Toast.makeText(getContext(), "Catched:" + logosCatched.size(), Toast.LENGTH_SHORT).show();
            }
        }

        for (int i = logos.size(); i < noLogos;i++)
        {
            int rndX = this.getWidth();
            int rndY = NumberUtils.rnd(this.getHeight());
            logos.add(new PlPoint(rndX, rndY));
        }
    }

    public boolean lost()
    {
        for (PlPoint p : logos)
        {
            if (p.x < 0) return true;
        }
        return false;
    }

}
