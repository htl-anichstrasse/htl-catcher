package tirol.htlanichstrasse.htlcatcher;

import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
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
   private static final int REQUEST_IMAGE_CAPTURE = 1;

   /**
    * Static unique request code for gallery access & permission request
    */
   private static final int REQUEST_GALLERY_CAPTURE = 2;

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
      imageButton = findViewById(R.id.setPhotoButton);
      imageButton.setOnClickListener(view -> showSelectionDialog());
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
      if (requestCode == REQUEST_IMAGE_CAPTURE) {
         // Check if users has declined camera permissions
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Dispatch picture request to camera
            dispatchTakePictureIntent(requestCode);
         } else {
            // App has no permission for using camera
            Toast.makeText(this, getString(R.string.main_takephoto_toast_nopermission),
                Toast.LENGTH_LONG)
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

      // Handling activity result request code on IMAGE_CAPTURE_REQUEST
      try {
         if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            final tirol.htlanichstrasse.htlcatcher.util.Config config = tirol.htlanichstrasse.htlcatcher.util.Config
                .getInstance();
            final Bitmap roundedBitmap = getRoundedCroppedBitmap(bitmap);
            final Bitmap roundedScaledBitmap = Bitmap
                .createScaledBitmap(roundedBitmap, config.getCursorRadius() * 2,
                    config.getCursorRadius() * 2, false);
            imageButton.setImageBitmap(roundedBitmap);
            saveImage(getFilesDir() + "/PHOTO", "me_disp.png", roundedBitmap);
            saveImage(getFilesDir() + "/PHOTO", "me.png", roundedScaledBitmap);
            System.out.println(roundedBitmap.getHeight() + " W: " + roundedBitmap.getWidth());
         }
      }
      catch (Exception exception) {
         Log.e(LOG_TAG, "Image Capture Error", exception);
      }

      // Handling activity result code on GALLERY_CAPTURE_REQUEST
      try {
         if (requestCode == REQUEST_GALLERY_CAPTURE && resultCode == RESULT_OK) {
            final tirol.htlanichstrasse.htlcatcher.util.Config config = tirol.htlanichstrasse.htlcatcher.util.Config
             .getInstance();
            final InputStream inputStream = getContentResolver().openInputStream(data.getData());
            final Bitmap decodedInputStreamBitmap = BitmapFactory.decodeStream(inputStream);
            final Bitmap roundedBitmap = getRoundedCroppedBitmap(decodedInputStreamBitmap);
            final Bitmap roundedScaledBitmap = Bitmap
                .createScaledBitmap(roundedBitmap, config.getCursorRadius() * 2,
                    config.getCursorRadius() * 2, false);
            imageButton.setImageBitmap(roundedBitmap);
            saveImage(getFilesDir() + "/PHOTO", "me_disp.png", roundedBitmap);
            saveImage(getFilesDir() + "/PHOTO", "me_disp.png", roundedScaledBitmap);
            System.out.println(roundedBitmap.getHeight() + " W: " + roundedBitmap.getHeight());
         }
      }
      catch (Exception exception) {
         Log.e(LOG_TAG, "File Selcection Error", exception);
      }
   }
   /**
    * Creates the dialog to choose between device camera and local gallery to set avatar image.
    */
   private void showSelectionDialog() {
      final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
      final AlertDialog alertDialog = dialogBuilder.create();

      // creates the dialog with two options
      alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getText(R.string.pick_picture_from_gallery),
          (dialogInterface, id) -> {
             dispatchPickGalleryPictureIntent(REQUEST_GALLERY_CAPTURE);
             dialogInterface.dismiss();
          });

      alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getText(R.string.take_picture_using_camera),
          (dialogInterface, id) -> {
             dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
             dialogInterface.dismiss();
          });
      alertDialog.show();

      // layout changes to set button alignment to 'centered'
      final Button buttonGallery = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
      final Button buttonCamera = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

      // sets dialog background to black; sets button font colour to white
      Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_dark);
      alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
      alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);

      // centers button alignment
      final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) buttonGallery.getLayoutParams();
      layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

      buttonGallery.setLayoutParams(layoutParams);
      buttonCamera.setLayoutParams(layoutParams);
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
         Toast.makeText(this, getString(R.string.main_takephoto_toast_nophoto), Toast.LENGTH_LONG)
             .show();
      }
   }

   /**
    * Switches to instruction view, which provides a description of the game and explains how to
    * play it.
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
         }
      }

      // Start camera intent
      final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
         startActivityForResult(takePictureIntent, requestId);
      }
   }


   /**
    * Attempts to start a new gallery activity in which the user has to choose a local picture,
    * which is then returned to the app and set as avatar as well as icon of the image button in
    * the main activity.
    *
    * @param requestId id for request permission to start new gallery activity.
    */
   private void dispatchPickGalleryPictureIntent(int requestId) {
      // Check permission and request if required:
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
         if (checkSelfPermission(permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.READ_EXTERNAL_STORAGE}, requestId);
         }
      }

      // Starts the gallery intent:
      final Intent pickPictureFromGallery = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
      if (pickPictureFromGallery.resolveActivity((getPackageManager())) != null) {
         startActivityForResult(pickPictureFromGallery, requestId);
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

}
