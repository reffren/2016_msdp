package ru.electronim.msuc;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class StartStopWorkActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStartWork;
    Chronometer chronometer;

    long time;
    float fullTime;
    String idForName;
    String workState = "в работе";
    String workEnd = "работа завершена";
    String currentDate;
    int sumBrig; // количество человек в бригаде для заполнения поля "факт чел.ч"

    DataBaseAdapter dataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop_work);

        dataBaseAdapter = new DataBaseAdapter(this);
        try {
            dataBaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if(intent != null) {
            idForName = intent.getStringExtra("id");
            sumBrig=Integer.parseInt(intent.getStringExtra("sumBrig").toString());
        }

        btnStartWork = (Button) findViewById(R.id.btnStartWork);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setText("00:00:00");

        btnStartWork.setTag(1);

        btnStartWork.setOnClickListener(this);

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer cArg) {
                long t = SystemClock.elapsedRealtime() - cArg.getBase() - SimpleTimeZone.getDefault().getRawOffset(); //to get the offset in milliseconds from UTC of this time zone's standard time
                cArg.setText(DateFormat.format("kk:mm:ss", t));
            }
        });
    }

    @Override
    public void onClick(View v) {
        UpdateAllDataInServer update = new UpdateAllDataInServer();
        switch (v.getId()) {
            case R.id.btnStartWork:
                final int status = (Integer) v.getTag();
                if(status==1) {
                    chronometer.start();
                    btnStartWork.setText("Закончить работу");
                    btnStartWork.setTag(2);
                    dataBaseAdapter.writeState(workState, idForName);
                    update.updateData(dataBaseAdapter); // обновляем БД на сервере, чтобы появился статус "в работе"
                } else {
                    chronometer.stop();
                    this.getTime();
                    dataBaseAdapter.writeTime(currentDate, idForName, fullTime, workEnd);
                    update.updateData(dataBaseAdapter); // обновляем БД на сервере, чтобы появился статус "работа завершена"
                    Intent intent = new Intent(this, ChoiceActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    public void getTime() {
        if (chronometer.getBase() > 0) {
            time = SystemClock.elapsedRealtime() - chronometer.getBase();
            fullTime = (float) (time / 3600000f) * sumBrig; // В скобках рассчитываем общее, затраченное время, а далее умножаем это время на количество бригады

            // current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            currentDate = sdf.format(new Date());
           /* int minutes = (int) (time - hours * 3600000) / 60000;
            int seconds = (int) (time - hours * 3600000 - minutes * 60000) / 1000;

            if(hours>0 && minutes>0 && seconds>0) {
                fullTime = (String) (hours + " ч. " + minutes + " мин. " + seconds + " сек. ");
            }
            if(hours==0 && minutes==0) {
                fullTime = (String) (seconds + " сек. ");
            }
            if(hours==0) {
                fullTime = (String) (minutes + " мин. " + seconds + " сек. ");
            }
            if(hours>0.1) {
                fullTime = hours;
            }
            { if(minutes<6) {
                fullTime=minutes;
            }
                fullTime=seconds;
            }*/
        }
    }
}
