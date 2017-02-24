package ru.electronim.msuc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class WorkActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnPlan;
    Button btnWorkToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        btnPlan = (Button) findViewById(R.id.btnPlan);
        btnWorkToday = (Button) findViewById(R.id.btnWorkToday);

        btnPlan.setOnClickListener(this);
        btnWorkToday.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlan:
                Intent intentPlan = new Intent(WorkActivity.this, PlanActivity.class);
                startActivity(intentPlan);
                break;
            case R.id.btnWorkToday:
                Intent intentWorkToday = new Intent(WorkActivity.this, WorkTodayActivity.class);
                startActivity(intentWorkToday);
                break;
        }
    }
}
