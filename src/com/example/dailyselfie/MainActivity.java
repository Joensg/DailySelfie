package com.example.dailyselfie;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends ListActivity {

    private static final String TAG = "Daily Selfie";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    final Context context = this;

    PhotosListAdapter mAdapter;
    String mCurrentPhotoPath;
    String mFileName;
    File photoFile;
    String timeStamp;

    static PendingIntent pendingIntent;
    static AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "entered onItemClick");
                PhotoRecord photoRecord = (PhotoRecord) mAdapter.getItem(position);
                Bitmap bitmap = photoRecord.getmPhotoBitmap();

                Intent photoLargeIntent = new Intent(view.getContext(), PhotoActivity.class);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                photoLargeIntent.putExtra("byteArray", bs.toByteArray());
                photoLargeIntent.putExtra("fileName", mFileName);
                startActivity(photoLargeIntent);
            }
        });

        Intent intentsOpen = new Intent(this, AlarmSelfie.class);
        intentsOpen.setAction("com.example.dailyselfie.takephoto.alarm.ACTION");  
        pendingIntent = PendingIntent.getBroadcast(this, 111, intentsOpen, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //for auto setting alarm on aplication start
        fireAlarm();
        mAdapter = new PhotosListAdapter(getApplicationContext());
        mAdapter.addAllViews();
        setListAdapter(mAdapter);
    }
   
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "entered onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.i(TAG, "resultCode == RESULT_OK ");

            galleryAddPic();
            Bitmap imageBitmap = setPic();
            PhotoRecord photoRecord = new PhotoRecord(imageBitmap,mCurrentPhotoPath,mFileName,timeStamp);
            mAdapter.add(photoRecord);
            Log.i(TAG, "exit onActivityResult");
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
        // Create an image file name
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = new File(storageDir,imageFileName+".jpg");
        storageDir.mkdirs();

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mFileName = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap setPic(){
        int scaleFactor = 5;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mFileName, bmOptions);
        return bitmap;
    }

    public void fireAlarm() {
        
        final int TWO_MINUTES = 2 * 60 * 1000;
        //final int ONE_DAY = 24 * 60 * 60 * 1000;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TWO_MINUTES, TWO_MINUTES, pendingIntent);
    }

    public void stopAlarm(){
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.camera_icon:
            Log.i(TAG, "camera's icon pressed");
            dispatchTakePictureIntent();
            return true;

            case R.id.cancel_alarm:
            stopAlarm();
            Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show();
            return true;

            case R.id.set_alarm:
            fireAlarm();
            Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
            return true;

            case R.id.deletallpics:
            	//dialog asking the users if he wants to delete all selfie photos
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            	// Add the buttons
            	builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog1, int id) {
            	               // User clicked OK button
            	        	   mAdapter.deleteAllPics();
            	               mAdapter.removeAllViews();
            	               dialog1.dismiss();
            	           }
            	       });
            	builder.setMessage(R.string.dialog1_message)
                .setTitle(R.string.dialog1_title);
            	
            	builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog1, int id) {
            	               // User cancelled the dialog
            	        	   dialog1.dismiss();
            	           }
            	       });
            	// Create the AlertDialog
            	AlertDialog dialog1 = builder.create();
            	dialog1.show();
                return true;

            case R.id.about:
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle(R.string.about_text_title);
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}