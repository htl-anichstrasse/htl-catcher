package org.htlanich.htlcatcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import org.htlanich.htlcatcher.utils.ImageUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE1 = 1;
    static final int REQUEST_IMAGE_CAPTURE2 = 2;
    ImageButton ib = null;
    ImageButton ib2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ib = (ImageButton)findViewById(R.id.takePhotoBt);
        ib2 = (ImageButton)findViewById(R.id.takePhotoBt2);

        File img = new File(getFilesDir() + "/PHOTO", "me_disp.png");
        if (img.exists())
        {
            ib.setImageBitmap(ImageUtils.readBmFromFile(img.getAbsolutePath()));
        }

        File img2 = new File(getFilesDir() + "/PHOTO", "me_disp2.png");
        if (img2.exists())
        {
            ib2.setImageBitmap(ImageUtils.readBmFromFile(img2.getAbsolutePath()));
        }
    }

    public void photo1(View view)
    {
        dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE1);
    }


    public void photo2(View view) {
        dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE2);
    }
    private void dispatchTakePictureIntent(int id) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, id);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE1 && resultCode == RESULT_OK) {
            Log.d("PHOTO", "1");
            Bundle extras = data.getExtras();
            Bitmap bm = (Bitmap) extras.get("data");

            try {
                Bitmap meBm = ImageUtils.scaleBm(bm, 200, 200);
                ib.setImageBitmap(meBm);
                ImageUtils.saveImage(getFilesDir() + "/PHOTO", "me_disp.png", meBm);
                Bitmap newBm = ImageUtils.getRoundedCroppedBitmap(bm);
                newBm = ImageUtils.scaleBm(newBm, 100, 150);
                ImageUtils.saveImage(getFilesDir() + "/PHOTO", "me.png", newBm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE2 && resultCode == RESULT_OK) {
                Log.d("PHOTO", "1");
                Bundle extras = data.getExtras();
                Bitmap bm = (Bitmap) extras.get("data");

                try {
                    Bitmap meBm = ImageUtils.scaleBm(bm, 200, 200);
                    ib2.setImageBitmap(meBm);
                    ImageUtils.saveImage(getFilesDir() + "/PHOTO", "me_disp2.png", meBm);
                    Bitmap newBm = ImageUtils.getRoundedCroppedBitmap(bm);
                    newBm = ImageUtils.scaleBm(newBm, 100, 150);
                    ImageUtils.saveImage(getFilesDir() + "/PHOTO", "me2.png", newBm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    public void play(View view) {
        File img = new File(getFilesDir() + "/PHOTO", "me.png");
        if (img.exists()) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
            intent.putExtra("player_bm2", getFilesDir() + "/PHOTO/me2.png");
            startActivity(intent);
        } else
        {
            Toast.makeText(this,getString(R.string.photo_first), Toast.LENGTH_LONG).show();
        }
    }

}
