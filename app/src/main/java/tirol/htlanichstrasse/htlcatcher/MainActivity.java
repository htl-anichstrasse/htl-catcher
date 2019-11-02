package tirol.htlanichstrasse.htlcatcher;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.htlanich.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.game.GameActivity;
import tirol.htlanichstrasse.htlcatcher.game.instruction.InstructionActivity;

/**
 * Main activity executed on app launch
 *
 * @author Albert Greinöcker
 * @author Nicolaus Rossi
 * @since 31.10.2019
 */
public class MainActivity extends AppCompatActivity {

   /**
    * Static logging tag used for loggings from this class
    */
   private static String LOG_TAG = "MAIN_ACTIVITY";

   /**
    * Static unique request code for camera access & permission request
    */
   private static final int REQUEST_IMAGE_CAPTURE1 = 1;

   /**
    * Reference to image button for taking a picture (used as game cursor)
    */
   private ImageButton imageButton;

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // link xml
      setContentView(R.layout.activity_main);

      // get imageButton for game start
      imageButton = findViewById(R.id.takePhotoButton);

      // load saved image if already taken before
      final File img = new File(getFilesDir() + "/PHOTO", "me_disp.png");
      if (img.exists()) {
         imageButton.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
      }
   }

   @Override
   public void onRequestPermissionsResult(final int requestCode,
       @NonNull final String[] permissions,
       @NonNull final int[] grantResults) {
      if (requestCode == REQUEST_IMAGE_CAPTURE1) {
         // Check if users has declined camera permissions
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Dispatch picture request to camera
            dispatchTakePictureIntent(requestCode);
         } else {
            // App has no permission for using camera
            Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_LONG)
                .show();
         }
      }
   }

   @Override
   public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
      Log.d(LOG_TAG, "Received activity result code " + resultCode);
      // extract bitmap for intent extras
      final Bundle extras = data.getExtras();
      if (extras == null) {
         Log.e(LOG_TAG, "Could not fetch activity result extras bundle");
         return;
      }
      final Bitmap bitmap = (Bitmap) extras.get("data");
      if (bitmap == null) {
         Log.e(LOG_TAG, "Could not fetch activity result extras data");
         return;
      }

      // Handling activity result request code
      if (requestCode == REQUEST_IMAGE_CAPTURE1 && resultCode == RESULT_OK) {
         final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
         imageButton.setImageBitmap(scaledBitmap);
         saveImage(getFilesDir() + "/PHOTO", "me_disp.png", scaledBitmap);
         saveImage(getFilesDir() + "/PHOTO", "me.png",
             Bitmap.createScaledBitmap(getRoundedCroppedBitmap(bitmap), 100, 150, false));
      }
   }

   /**
    * Click handler for image button in main activity
    *
    * @param view the clicked button
    */
   public void onImageButtonClicked(final View view) {
      dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE1);
   }

   /**
    * Click handler for play button in main activity
    *
    * @param view the clicked button
    */
   public void onPlayButtonClicked(final View view) {
      if (new File(getFilesDir() + "/PHOTO", "me.png").exists()) {
         // Start new game activity and hand over image data
         final Intent intent = new Intent(this, GameActivity.class);
         intent.putExtra("player_bm", getFilesDir() + "/PHOTO/me.png");
         startActivity(intent);
      } else {
         Toast.makeText(this, getString(R.string.photo_first), Toast.LENGTH_LONG).show();
      }
   }

   /**
    * Click handler for instructions button in main activity
    *
    * @param view the clicked button
    */
   public void onInstructionsButtonClicked(final View view) {
      startActivity(new Intent(this, InstructionActivity.class));
   }

   /**
    * Tries to start a new camera activity which then returns the taken picture to the app, the
    * picture is then used as an image for the image button
    *
    * @param requestId id for request permission to camera & camera activity
    */
   private void dispatchTakePictureIntent(int requestId) {
      // Check permission and request if needed
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
         if (checkSelfPermission(permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.CAMERA}, requestId);
            return;
         }
      }

      // Start camera intent
      final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
         startActivityForResult(takePictureIntent, requestId);
      }
   }

   /**
    * Saves an image to the host's file system
    *
    * @param path the path to the directory for saving the image to
    * @param fileName the file name of the image
    * @param image the Bitmap object of the image
    * @return true if saving the image was successful, false otherwise
    */
   @SuppressWarnings("UnusedReturnValue")
   private boolean saveImage(final String path, final String fileName, final Bitmap image) {
      // Prepare directory
      final File saveDirectory = new File(path);
      if (!saveDirectory.exists()) {
         if (saveDirectory.mkdir()) {
            // Could not create directory for image
            return false;
         }
      }

      // Prepare output file
      final File destination = new File(saveDirectory, fileName);
      Log.d(LOG_TAG, "Saving image to: " + destination.getAbsolutePath());

      // Write file to disk
      try (final FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
         image.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
         fileOutputStream.flush();
      } catch (IOException e) {
         e.printStackTrace();
         return false;
      }

      // Save successful
      return true;
   }

   /**
    * Crops an input bitmap to a circle format
    *
    * @param bitmap the bitmap to be cropped
    * @return the cropped bitmap
    */
   public Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
      // Return bitmap
      final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
          bitmap.getHeight(), Config.ARGB_8888);
      final Rect rect = new Rect(0, 0, bitmap.getWidth(),
          bitmap.getHeight());

      // Initialize canvas
      final Canvas canvas = new Canvas(output);

      // Initialize paint
      final Paint paint = new Paint();
      paint.setAntiAlias(true);
      paint.setFilterBitmap(true);
      paint.setDither(true);

      // Draw circle and intersect
      canvas.drawARGB(0, 0, 0, 0);
      paint.setColor(Color.parseColor("#BAB399"));
      canvas.drawCircle(bitmap.getWidth() / 2.0f + 0.7f,
          bitmap.getHeight() / 2.0f + 0.7f,
          bitmap.getWidth() / 2.0f + 0.1f, paint);
      paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
      canvas.drawBitmap(bitmap, rect, rect, paint);

      // Return cropped image
      return output;
   }

}
