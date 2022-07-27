package com.example.voicenote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    Handler handler;

    int seconds=0;
    String path = null;
    int dummySeconds=0;
    int playableSeconds=0;

    ExecutorService executorService= Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ibRecord = findViewById(R.id.ib_record);
        ibPlay = findViewById(R.id.ib_play);
        tvTime = findViewById(R.id.tv_recording_path);
        getSupportActionBar().hide();

        ibRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkRecordingPermission()) {
                    if (!isRecording){
                        isRecording=true;
                        executorService.execute(new Runnable() {
                            public void run() {
                                mediaRecorder= new MediaRecorder();
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                mediaRecorder.setOutputFile(getRecordingFilePath());
                                path=getRecordingFilePath();
                                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                                try {
                                    mediaRecorder.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                mediaRecorder.start();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        playableSeconds=0;
                                        seconds=0;
                                        dummySeconds=0;
                                        runTimer();
                                    }
                                });
                            }

                        });
                    } else {
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                mediaRecorder.stop();
                                mediaRecorder.release();
                                mediaRecorder=null;
                                playableSeconds=seconds;
                                dummySeconds=seconds;
                                isRecording=false;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.removeCallbackAndMessages(null);

                                    }
                                });
                            }
                        });
                    }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_AUDIO_PERMISSION_CODE){
            if (grantResults.length>0){
                boolean permissionToRecord=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if (permissionToRecord){
                    Toast.makeText(getApplicationContext(), "Permission Granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getRecordingFilePath(){
        ContextWrapper contextWrapper= new ContextWrapper(getApplicationContext());
        File music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(music, "testFile" + ".mp3");
        return file.getPath();
    }
}