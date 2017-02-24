package ru.electronim.msuc;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class RecordAudioActivity extends AppCompatActivity implements View.OnClickListener {

    String fileName;
    String idFile;
    int sumBrig; // бригаде для заполнения поля "факт чел.ч"

    Button btnInstruction;
    Button btnPlay;
    Button btnAudioNext;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private String outputFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);

        Intent intent = getIntent();
        if(intent != null) {
            fileName = intent.getStringExtra("nameForFile");
            idFile = intent.getStringExtra("id");
            sumBrig=Integer.parseInt(intent.getStringExtra("sumBrig").toString());
        }

        btnInstruction = (Button) findViewById(R.id.btnInstruction);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnAudioNext = (Button) findViewById(R.id.btnAudioNext);

        btnInstruction.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);

        btnInstruction.setTag(1);
        btnPlay.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + idFile + ".3gp";

        mRecorder=new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(outputFile);
    }

    private void stopRecording() {
        mRecorder.release();
        mRecorder = null;
        Toast.makeText(getApplicationContext(), "Инструктаж записан",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnInstruction:
                final int status = (Integer) v.getTag();
                if(status==1) {
                    try {
                        mRecorder.prepare();
                        mRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Запись инструктажа началась", Toast.LENGTH_LONG).show();
                    btnInstruction.setText("Стоп");
                    btnInstruction.setTag(2);
                } else {
                    stopRecording();
                    btnInstruction.setEnabled(false);
                    btnPlay.setEnabled(true);
                }
                break;
            case R.id.btnPlay:
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(outputFile);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mPlayer.prepare();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayer.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnAudioNext:
                Intent intent = new Intent(this, StartStopWorkActivity.class);
                intent.putExtra("id", idFile);
                intent.putExtra("sumBrig", String.valueOf(sumBrig));
                startActivity(intent);
                finish();
        }
    }
}
