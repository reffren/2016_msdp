package ru.electronim.msuc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StartWorkActivity extends AppCompatActivity implements View.OnClickListener {

    int position = 0;
    int idBrigada = 1;
    int sumBrig; //считаем количество бригады для заполнения поля "факт чел.ч"

    String id;
    String name;
    String nameForFile; //for putextra
    String fullName = "Текущая работа: ";

    Button btnNextRecordAudio;
    TextView tvName;
    EditText editText,etOtvRuk,etNabl,etDopusk,etProizv,etVidayoush,etBrigada;
    String _etOtvRuk,_etNabl,_etDopusk,_etProizv,_etVidayoush,_etBrigada;

    LinearLayout linearLayout;

    List<EditText> allBrig;

    DataBaseAdapter dataBaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_work);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", position);
        id = String.valueOf(position);

        dataBaseAdapter = new DataBaseAdapter(this);
        try {
            dataBaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        allBrig = new ArrayList<EditText>();
        name = dataBaseAdapter.getDataForWork(id);
        nameForFile = name;
        fullName += name;

        btnNextRecordAudio = (Button) findViewById(R.id.btnNextRecordAudio);
        tvName = (TextView) findViewById(R.id.tvName);
        linearLayout = (LinearLayout) findViewById(R.id.llStartWork);

        etOtvRuk = (EditText) findViewById(R.id.etOtvRuk);
        etNabl = (EditText) findViewById(R.id.etNabl);
        etDopusk = (EditText) findViewById(R.id.etDopusk);
        etProizv = (EditText) findViewById(R.id.etProizv);
        etVidayoush = (EditText) findViewById(R.id.etVidayoush);
        etBrigada = (EditText) findViewById(R.id.etBrigada);

        tvName.setText(fullName);

        btnNextRecordAudio.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNextRecordAudio:
                if(writeToDataBase()) {
                    Intent intent = new Intent(this, /*RecordAudioActivity.class*/ StartStopWorkActivity.class);
                    intent.putExtra("nameForFile", nameForFile);
                    intent.putExtra("id", id);
                    intent.putExtra("sumBrig", String.valueOf(sumBrig));
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_for_start_work, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.id_addBrigada:
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                editText = new EditText(this);
                allBrig.add(editText);
                editText.setId(idBrigada);
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                editText.requestFocus();
                editText.setHint("Член бригады");
                linearLayout.addView(editText, layoutParams);
                idBrigada++;
        }
        return false;
    }

    public boolean writeToDataBase() {

        boolean ret = true;
        allBrig.add(etBrigada);
        _etOtvRuk = etOtvRuk.getText().toString();
        _etNabl = etNabl.getText().toString();
        _etDopusk = etDopusk.getText().toString();
        _etProizv = etProizv.getText().toString();
        _etVidayoush = etVidayoush.getText().toString();
        _etBrigada = etBrigada.getText().toString();
        if (!_etDopusk.isEmpty() && !_etProizv.isEmpty() && !_etBrigada.isEmpty() && !_etVidayoush.isEmpty()) {
            if(idBrigada==1) { //если idBrigada = 1, то значит доп. бригаду не выбирали и следов-то существуют только произв. работ и член бригады
                sumBrig=2;
            }
            else { // в противном случае считаем idBrigada и плюс 1 чел - это производитель
                sumBrig=idBrigada+1;
            }
            dataBaseAdapter.writeBrig(allBrig, _etOtvRuk, _etNabl, _etDopusk, _etProizv, _etVidayoush, id);
        } else {
            Toast.makeText(StartWorkActivity.this,
                    "Введите необходимый состав бригады", Toast.LENGTH_LONG).show();
            ret = false;
        }
        return ret;
    }
}

