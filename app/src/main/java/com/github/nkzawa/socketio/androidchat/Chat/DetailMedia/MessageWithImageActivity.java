package com.github.nkzawa.socketio.androidchat.Chat.DetailMedia;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.github.nkzawa.socketio.androidchat.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageWithImageActivity extends ActionBarActivity {

    public static int IMAGE_FILE = 0 ;
    public int VIDEO_FILE = 1;
    private EditText et_message;
    private ImageView iv_photo_to_send;
    private Button btn_send;
    private ByteArrayOutputStream imageBytes;
    private Uri fileToSend;
    private int typeFile;
    private LinearLayout ll_image, ll_video;
    private VideoView vv_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_with_image);

        setupView();
        setupActions();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Bundle bundle = extras.getBundle("imageData");
            onCaptureImageResult(bundle);
        }

        if(getIntent().getData() != null){
            onSelectFromGalleryResult(getIntent());
        }
    }

    public void setupView(){
        et_message = (EditText)findViewById(R.id.et_message);
        iv_photo_to_send = (ImageView)findViewById(R.id.iv_photo_to_send);
        btn_send = (Button)findViewById(R.id.btn_send);
        ll_image = (LinearLayout)findViewById(R.id.ll_image);
        ll_video = (LinearLayout)findViewById(R.id.ll_video);
        vv_video = (VideoView)findViewById(R.id.vv_video);
    }

    public void setupActions(){
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
//                createImageFile(imageBytes);
                if (fileToSend != null) {
                    if(typeFile == IMAGE_FILE){
                        createImageFile(imageBytes);
                        returnIntent.putExtra("result", fileToSend.toString());
                    }else{
                        returnIntent.putExtra("result", getPathFromVideoUri(fileToSend));
                    }
                    returnIntent.putExtra("typeFile", typeFile);
                    returnIntent.putExtra("message", et_message.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private void onCaptureImageResult(Bundle data) {
        typeFile = IMAGE_FILE;
        Bitmap thumbnail = (Bitmap) data.get("data");
        imageBytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, imageBytes);

        iv_photo_to_send.setImageBitmap(thumbnail);
    }

    private int typeMediaFile(Uri file){

        if (file.getPath().contains("/video/")) {
            Log.d(this.getClass().getName(), "Video");
            return VIDEO_FILE;
        }else{
            Log.d(this.getClass().getName(), "Image");
            return IMAGE_FILE;
        }

    }

    private void onSelectFromGalleryResult(Intent data) {
        fileToSend = data.getData();
//        File file= new File(getRealPathFromURI(fileToSend));

        Log.d("asdsdsadsad", "adasdasdad"+fileToSend.getPath());
        typeFile = typeMediaFile(fileToSend);
        if(typeFile == IMAGE_FILE){
            ll_video.setVisibility(View.GONE);
            ll_image.setVisibility(View.VISIBLE);

            Bitmap bm=null;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            imageBytes = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, imageBytes);



            iv_photo_to_send.setImageBitmap(bm);

        }else{
            ll_video.setVisibility(View.VISIBLE);
            ll_image.setVisibility(View.GONE);

            vv_video.setVideoURI(fileToSend);
            MediaController mc = new MediaController(this);
            mc.setAnchorView(vv_video);

            vv_video.setVideoURI(fileToSend);
            vv_video.setMediaController(mc);
            vv_video.requestFocus();
        }
    }

    private String getPathFromVideoUri(Uri selectedVideoUri){
        String selectedImagePath = null;
        Cursor cursor = getContentResolver().query(
                selectedVideoUri, null, null, null, null);
        if (cursor == null) {
            selectedImagePath = selectedVideoUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            selectedImagePath = cursor.getString(idx);
        }

        return  selectedImagePath;
    }


    // this Does not work
    private String getPathFromImageUri(Uri selectedImageUri){
        String selectedImagePath = null;
        Cursor cursor = getContentResolver().query(
                selectedImageUri, null, null, null, null);
        if (cursor == null) {
            selectedImagePath = selectedImageUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            selectedImagePath = cursor.getString(idx);
        }

        return  selectedImagePath;
    }


    public void createImageFile(ByteArrayOutputStream bytes){

            File destination = new File(Environment.getExternalStorageDirectory(),
            System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            fileToSend = Uri.fromFile(destination);
    }



}