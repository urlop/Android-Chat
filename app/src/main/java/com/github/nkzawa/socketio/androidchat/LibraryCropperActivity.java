package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubymobile on 11/05/16.
 */
public class LibraryCropperActivity extends Activity {
    private CropImageView civ_image;
    private TextView tv_load, tv_crop;
    private LinearLayout ll_load, ll_crop;
    private int count;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_cropper);

        setupView();
        setupAction();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            count = extras.getInt("count");
        }
    }

    private void setupView(){
        tv_load = (TextView) findViewById(R.id.tv_load);
        tv_crop = (TextView) findViewById(R.id.tv_crop);

        ll_load = (LinearLayout) findViewById(R.id.ll_load);
        ll_crop = (LinearLayout) findViewById(R.id.ll_crop);
        civ_image = (CropImageView) findViewById(R.id.civ_image);

    }

    private void setupAction(){
        ll_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });

        ll_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });
    }


    /**
     * On load image button click, start pick  image chooser activity.
     */
    public void loadImage() {
        startActivityForResult(getPickImageChooserIntent(), 200);
    }

    /**
     * Crop the image and set it back to the  cropping view.
     */
    public void cropImage() {
        Bitmap cropped =  civ_image.getCroppedImage(500, 500);
        if (cropped != null) {
            saveImage(cropped);

            Intent returnIntent = new Intent();
            Uri uri = getCaptureImageOutputUri();
            if (uri != null) {
                returnIntent.putExtra("result", uri.toString());
                setResult(Activity.RESULT_OK, returnIntent);
            }
        }
        finish();
    }

    public void saveImage(Bitmap myBitmap){
        File getImage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        //create a file to write bitmap data
        File f = new File(getImage.getPath(), Constants.IMAGE_FILE + count + Constants.IMAGE_FORMAT);
        try {
            f.createNewFile();

            //Convert bitmap to byte array
            Bitmap bitmap = myBitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }




    @Override
    protected void onActivityResult(int  requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            imageUri =  getPickImageResultUri(data);
            civ_image.setImageUri(imageUri);
            ll_crop.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Create a chooser intent to select the  source to get image from.<br/>
     * The source can be camera's  (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the  intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri =  getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager =  getPackageManager();

        // collect all camera intents
        Intent captureIntent = new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam =  packageManager.queryIntentActivities(captureIntent, 0);
        Intent intent = new  Intent(captureIntent);
        intent.setComponent(new ComponentName(listCam.get(0).activityInfo.packageName, listCam.get(0).activityInfo.name));
        intent.setPackage(listCam.get(0).activityInfo.packageName);
        if (outputFileUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        }
        allIntents.add(intent);

        // collect all gallery intents
        Intent galleryIntent = new  Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery =  packageManager.queryIntentActivities(galleryIntent, 0);
        intent = new  Intent(galleryIntent);
        intent.setComponent(new  ComponentName(listGallery.get(0).activityInfo.packageName, listGallery.get(0).activityInfo.name));
        intent.setPackage(listGallery.get(0).activityInfo.packageName);
        allIntents.add(intent);

        // the main intent is the last in the  list so pickup the useless one
        Intent mainIntent =  allIntents.get(allIntents.size() - 1);
        for (Intent i : allIntents) {
            if  (i.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity"))  {
                mainIntent = i;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main  intent
        Intent chooserIntent =  Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), Constants.IMAGE_FILE + count + Constants.IMAGE_FORMAT));
        }
        return outputFileUri;
    }


    /**geChooserIntent()}.<br/>
     * Will return the correct URI for camera  an
     * Get the URI of the selected image from  {@link # getPickImad gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public Uri getPickImageResultUri(Intent  data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ?  getCaptureImageOutputUri() : data.getData();
    }
}
