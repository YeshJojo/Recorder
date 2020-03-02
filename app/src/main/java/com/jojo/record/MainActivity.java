package com.jojo.record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageButton recordBtn, stopBtn, playBtn, stopPlay, pauseBtn;
    MediaRecorder recorder;
    MediaPlayer player;
    final int REQUEST_CODE = 999;
    final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/recorded_audio.3gp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordBtn = findViewById(R.id.recordBtn);
        stopBtn = findViewById(R.id.stopBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        playBtn = findViewById(R.id.playBtn);
        stopPlay = findViewById(R.id.stopPlay);

        recorder = new MediaRecorder();
        player = new MediaPlayer();

        recordBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        playBtn.setEnabled(false);
        stopPlay.setEnabled(false);

        requestPermissions();

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions()){
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(filePath);
                    try {
                        recorder.prepare();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    recordBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                    pauseBtn.setEnabled(true);
                    playBtn.setEnabled(false);
                    stopPlay.setEnabled(false);
                    recorder.start();
                    Toast.makeText(getApplicationContext(),"Record Started",Toast.LENGTH_LONG).show();
                } else {
                    requestPermissions();
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.release();
                recorder = null;
                recordBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                playBtn.setEnabled(true);
                stopPlay.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Record Stopped",Toast.LENGTH_LONG).show();
            }
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying())
                    player.pause();
                else
                    player.start();

                recordBtn.setEnabled(true);
                stopBtn.setEnabled(true);
                pauseBtn.setEnabled(true);
                playBtn.setEnabled(false);
                stopPlay.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Record Paused",Toast.LENGTH_LONG).show();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    player.setDataSource(filePath);
                    player.prepare();
                    player.start();
                    player.setLooping(true);
                } catch (IOException e){
                    e.printStackTrace();
                }
                recordBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                pauseBtn.setEnabled(true);
                playBtn.setEnabled(false);
                stopPlay.setEnabled(true);
                Toast.makeText(getApplicationContext(),"Playing...",Toast.LENGTH_LONG).show();
            }
        });
        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.release();
                player = null;
                recordBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                pauseBtn.setEnabled(false);
                playBtn.setEnabled(false);
                stopPlay.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Audio Stopped",Toast.LENGTH_LONG).show();
            }
        });

    }
    public boolean checkPermissions(){
        int audio_res = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storage_res = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return audio_res == PackageManager.PERMISSION_GRANTED && storage_res == PackageManager.PERMISSION_GRANTED;
    }
    public void requestPermissions(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE: {
                if(grantResults.length >0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        Log.d("Permission", "permissionsAllowed");
                    else
                        Log.d("Permission", "permissionsDenied");
                }
            }
        }
    }
}
