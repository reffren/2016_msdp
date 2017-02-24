package ru.electronim.msuc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.sql.SQLException;

public class PlanActivity extends AppCompatActivity {

    DataBaseAdapter dataBaseAdapter;
    private ListViewAdapterForWork adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        listView = (ListView) findViewById(R.id.ListPlan);

        dataBaseAdapter = new DataBaseAdapter(this);
        try {
            dataBaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       // dataBaseAdapter.writeDataWork("Проверка разъед", "8,5");

        displayResultList();
    }
    private void displayResultList() {
        adapter = new ListViewAdapterForWork(this, dataBaseAdapter.getAllData(),0);
        listView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_for_work, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.id_choose:
                UpdateAllDataInServer update = new UpdateAllDataInServer();
                update.updateData(dataBaseAdapter); // обновляем БД сервера, для отметки выбранных работ
                Intent intent = new Intent(this, WorkTodayActivity.class);
                startActivity(intent);
                finish();
        }
        return false;
    }

}
