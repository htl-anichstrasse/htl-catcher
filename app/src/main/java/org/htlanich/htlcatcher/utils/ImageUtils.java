package org.htlanich.htlcatcher.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by albert on 02.11.17.
 */

public class ImageUtils {


    public static Bitmap readBmFromFile(String filePath)
    {
        return BitmapFactory.decodeFile(filePath);
    }

    public static Bitmap scaleBm(Bitmap bm, int w, int h)
    {
        Bitmap b = Bitmap.createScaledBitmap(bm, w,h, false);
        return b;
    }

    public static void saveImage(String path, String fileName, Bitmap bm) throws IOException {
        File sd = new File(path); //.getExternalStorageDirectory();
        if (!sd.exists()) {
            sd.mkdir();
        }
        File dest = new File(sd, fileName);
        Log.d("PHOTO", dest.getAbsolutePath());

        FileOutputStream out = new FileOutputStream(dest);
        bm.compress(Bitmap.CompressFormat.PNG, 90, out);
        out.flush();
        out.close();
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        Bitmap finalBitmap = bitmap;
//        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
//            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
//                    false);
//        else
//            finalBitmap = bitmap;

        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(),
                finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f,
                finalBitmap.getHeight() / 2 + 0.7f,
                finalBitmap.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }
}
