package com.github.nkzawa.socketio.androidchat.Chat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.github.nkzawa.socketio.androidchat.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageWithImageActivity extends ActionBarActivity {

    private int IMAGE_FILE = 0 ;
    private int VIDEO_FILE = 1;
    private EditText et_message;
    private ImageView iv_photo_to_send;
    private Button btn_send;
    private Uri fileToSend;

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
    }

    public void setupActions(){
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                if (fileToSend != null) {
                    returnIntent.putExtra("result", fileToSend.getPath());
                    returnIntent.putExtra("message", et_message.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private void onCaptureImageResult(Bundle data) {
        Bitmap thumbnail = (Bitmap) data.get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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

        iv_photo_to_send.setImageBitmap(thumbnail);
    }

    private int typeMediaFile(Uri file){
        if (file.toString().endsWith(".jpg")){
            return IMAGE_FILE;
        } else if (file.toString().endsWith(".mp4")) {
            return VIDEO_FILE;
        } else{
            return IMAGE_FILE;
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        fileToSend = data.getData();
        File file= new File(getRealPathFromURI(fileToSend));

        fileToSend = Uri.fromFile(file);
        Log.d("asdsdsadsad", "adasdasdad"+fileToSend.getPath());
        if(typeMediaFile(fileToSend) == IMAGE_FILE){
            Bitmap bm=null;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Glide.with(this)
                    .load(fileToSend)
                    .placeholder(R.drawable.shadow_picture)
                    .error(R.drawable.shadow_picture)
                    .into(iv_photo_to_send);
        }else{

        }


//        iv_photo_to_send.setImageBitmap(bm);
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}