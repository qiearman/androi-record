package com.example.androidrecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView bt_Play;
    ImageView bt_Record;
    ImageView bt_RecordStop;
    Button bt_ListenRecorded;

    boolean mIsRecorded = false;

    MediaPlayer mMediaPlayer;
    MediaRecorder mRecorder;

    String mRecordedFile;
    final static int PERMIT_AUDIO_RECORD = 931;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_Play = (ImageView)findViewById(R.id.bt_Play);
        bt_Record = (ImageView)findViewById(R.id.bt_Record);
        bt_RecordStop = (ImageView)findViewById(R.id.bt_RecordStop);
        bt_ListenRecorded = (Button)findViewById(R.id.bt_ListenRecorded);


        mRecordedFile = getCacheDir().getAbsolutePath() + "/temp-record.3gp";


        bt_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer = MediaPlayer.create(view.getContext(), R.raw.asmarandana);
                try {
                    if(!mMediaPlayer.isPlaying()) mMediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bt_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                mRecorder.setOutputFile(mRecordedFile);

                try {
                    mRecorder.prepare();
                    mRecorder.start();

                    mIsRecorded = true;
                    bt_Record.setVisibility(View.GONE);
                    bt_RecordStop.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bt_RecordStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                if(mIsRecorded) mRecorder.stop();
                mIsRecorded = false;

                bt_Record.setVisibility(View.VISIBLE);
                bt_RecordStop.setVisibility(View.GONE);
            }
        });

        bt_ListenRecorded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }

                try {
                    if(mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(mRecordedFile);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        handlePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMIT_AUDIO_RECORD:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "AUDIO_RECORD User Granted");
                } else {
                    finish();
                }
                break;
        }
    }

    private void handlePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if not permission available and granted, then request permission
            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.RECORD_AUDIO
                }, PERMIT_AUDIO_RECORD);
            } else {
                // Permission has available and granted
                Log.d("MainActivity", "AUDIO_RECORD Granted");
            }
        }
    }

}
