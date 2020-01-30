package tirol.htlanichstrasse.htlcatcher.game.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.github.dhaval2404.imagepicker.ImagePicker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.Setter;
import tirol.htlanichstrasse.htlcatcher.R;
import tirol.htlanichstrasse.htlcatcher.util.CatcherConfig;

/**
 * Main activity executed on app launch
 *
 * @author Nicolaus Rossi
 * @author Joshua Winkler
 * @author Albert Greinöcker
 * @since 31.10.2019
 */
public class MainActivity extends AppCompatActivity {

   /**
    * Static logging tag used for loggings from this class
    */
   private static String LOG_TAG = "MAIN_ACTIVITY";

   /**
    * Static unique request code for image picker
    */
   private static final int REQUEST_IMAGE_PICKER = 2;

   /**
    * Reference to image button for taking a picture (used as game cursor)
    */
   private ImageButton imageButton;

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // link xml
      setContentView(R.layout.activity_main);

      // reference to image button for choosing game cursor
      imageButton = findViewById(R.id.setPhotoButton);

      // load saved image if already taken before
      final File img = new File(getFilesDir() + "/PHOTO", "me.png");
      if (img.exists()) {
         imageButton.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
      }
   }

   @Override
   public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Log.d(LOG_TAG, "Received activity request code " + resultCode);

      // Check for correct request code
      if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
         final CatcherConfig catcherConfig = CatcherConfig.getInstance();

         // Extract bitmap from activity return data
         final Bitmap extractedBitmap;
         try (final InputStream imageStream = new FileInputStream(
             ImagePicker.Companion.getFile(data))) {
            extractedBitmap = BitmapFactory.decodeStream(imageStream);
         } catch (IOException ex) {
            Log.e(LOG_TAG, "Could not fetch selected file from intent data: " + ex.getMessage());
            return;
         }

         // check successful extraction
         if (extractedBitmap == null) {
            Log.e(LOG_TAG, "Could not fetch activity result extras data");
            return;
         }

         // adapt and save extracted bitmap to filesystem
         final Bitmap roundedScaledBitmap = Bitmap
             .createScaledBitmap(getRoundedCroppedBitmap(extractedBitmap),
                 catcherConfig.getCursorRadius() * 2,
                 catcherConfig.getCursorRadius() * 2, false);
         imageButton.setImageBitmap(roundedScaledBitmap);
         saveImage(getFilesDir() + "/PHOTO", "me.png", roundedScaledBitmap);
      }

      // handle error
      if (resultCode == ImagePicker.RESULT_ERROR) {
         Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
      }
   }

   /**
    * Click handler for image selection button
    *
    * @param view the clicked button
    */
   public void onImageSelectionButtonClicked(final View view) {
      final int logoLength = CatcherConfig.getInstance().getLogoRadius() * 2;
      ImagePicker.Companion.with(this)
          .cropSquare()
          .compress(1024)
          .maxResultSize(logoLength, logoLength)
          .start(REQUEST_IMAGE_PICKER);
   }

   /**
    * Click handler for instruction activity button
    *
    * @param view the clicked button
    */
   public void onInstructionsButtonClicked(final View view) {
      startActivity(new Intent(this, InstructionActivity.class));
   }

   /**
    * Click handler for play button
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
         Toast.makeText(this, getString(R.string.main_takephoto_toast_nophoto), Toast.LENGTH_LONG)
             .show();
      }
   }

   /**
    * Crops an input bitmap to a circle format
    *
    * @param bitmap the bitmap to be cropped
    * @return the cropped bitmap
    */
   public Bitmap getRoundedCroppedBitmap(final Bitmap bitmap) {
      // Return bitmap
      final int sideLength = Math.min(bitmap.getWidth(), bitmap.getHeight());
      final Bitmap output = Bitmap.createBitmap(sideLength, sideLength, Config.ARGB_8888);
      final Rect rect = new Rect(0, 0, sideLength, sideLength);

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
      canvas.drawCircle(sideLength / 2.0f,
          sideLength / 2.0f,
          sideLength / 2.0f, paint);
      paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
      canvas.drawBitmap(bitmap, rect, rect, paint);

      // Return cropped image
      return output;
   }

   /**
    * Saves an image to the host's file system
    *
    * @param path the path to the directory for saving the image to
    * @param fileName the file name of the image
    * @param image the Bitmap object of the image
    * @return true if saving the image was successful, false otherwise
    */
   @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
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

}
