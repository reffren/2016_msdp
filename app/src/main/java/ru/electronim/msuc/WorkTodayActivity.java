package ru.electronim.msuc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

public class WorkTodayActivity extends AppCompatActivity {

    DataBaseAdapter dataBaseAdapter;
    private ListViewAdapterForWorkToday adapter;
    private ListView listView;
    ArrayList<Integer> arrayList = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_today);

        listView = (ListView) findViewById(R.id.ListPlan);

        dataBaseAdapter = new DataBaseAdapter(this);
        try {
            dataBaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        displayResultList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                arrayList = adapter.arrayListID; // get the id from database and ListViewAdapterForWorkToday
                int _id = arrayList.get(position);

                    Intent intent = new Intent(WorkTodayActivity.this, StartWorkActivity.class);
                    intent.putExtra("position", _id);
                    startActivity(intent);
                finish();
            }
        });
    }



    private void displayResultList() {
        adapter = new ListViewAdapterForWorkToday(this, dataBaseAdapter.getChosenDataForWorkToday(),0);
        listView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_for_work_today, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.id_edit:
                Intent intent = new Intent(this, EditWorkTodayActivity.class);
                startActivity(intent);
        }
        return false;
    }

}
