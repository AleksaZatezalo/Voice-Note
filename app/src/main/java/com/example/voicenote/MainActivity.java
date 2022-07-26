package com.example.voicenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_AUDIO_PERMISSION_CODE=101;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    ImageView ibRecord;
    ImageView ibPlay;
    TextView tvTime;
    TextView tvRecordingPath;
    boolean isRecording=true;
    boolean isPlaying=false;

    int seconds=0;
    String path = null;
    int dummySeconds=0;
    int playableSeconds=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ibRecord = findViewById(R.id.ib_record);
        ibPlay = findViewById(R.id.ib_play);
        tvTime = findViewById(R.id.tv_recording_path);

        ibRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkRecordingPermission()) {

                } else {
                    requestRecordingPermission();
                }
            }
        });
    }

    private void requestRecordingPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    public boolean checkRecordingPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_DENIED){
            requestRecordingPermission();
            return false;
        }
        return true;
    }
}