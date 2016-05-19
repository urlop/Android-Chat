package com.github.nkzawa.socketio.androidchat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rubymobile on 27/04/16.
 */
public class UtilsMethods {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public static Date parseDateFromString(String stringDate){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
            Date date=sdf.parse(stringDate);

            return date;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String getGalleryImagePath(Context context, Uri imageUri) {
        String path = "";
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(imageUri,
                filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
        }

        return path;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, Context context, View progress, View container){
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        try {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
            final View progressView = progress;
            final View containerView = container;

            if (container != null) {
                containerView.setVisibility(show ? View.GONE : View.VISIBLE);
                containerView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        containerView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });
            }

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
