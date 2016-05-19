package com.github.nkzawa.socketio.androidchat;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by rubymobile on 19/05/16.
 */
public class VideoActivity extends AppCompatActivity {

    private View v_progress;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            url = extras.getString("URL");
        } else {
            url = null;
        }

        setupElements();
    }

    private void setupElements() {
        VideoView vv_drill_video = (VideoView) findViewById(R.id.vv_drill_video);
        v_progress = findViewById(R.id.v_progress);

        UtilsMethods.showProgress(true, this, v_progress, null);

        try {
            String link=url;
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(vv_drill_video);
            Uri video = Uri.parse(link);
            vv_drill_video.setMediaController(mediaController);
            vv_drill_video.setVideoURI(video);

            vv_drill_video.start();

            vv_drill_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    UtilsMethods.showProgress(false, getApplicationContext(), v_progress, null);
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
    }

}
