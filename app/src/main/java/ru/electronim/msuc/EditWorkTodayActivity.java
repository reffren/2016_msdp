package ru.electronim.msuc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.sql.SQLException;

public class EditWorkTodayActivity extends AppCompatActivity {

    private ListViewAdapterForEditWorkToday adapter;
    private ListView listView;
    DataBaseAdapter dataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_work_today);

        listView = (ListView) findViewById(R.id.ListEdit);

        dataBaseAdapter = new DataBaseAdapter(this);
        try {
            dataBaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        displayResultList();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_for_edit_work_today, menu);
        return true;

    }

    private void displayResultList() {
        adapter = new ListViewAdapterForEditWorkToday(this, dataBaseAdapter.getChosenDataForWorkToday(),0);
        listView.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.id_delete:
                UpdateAllDataInServer update = new UpdateAllDataInServer();
                update.updateData(dataBaseAdapter); // обновляем БД сервера, для удаления отметки выбранных работ
                Intent intent = new Intent(this, PlanActivity.class);
                startActivity(intent);
                finish();
        }
        return false;
    }

}
