package ru.electronim.msuc;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tim on 20.01.2016.
 */
public class ListViewAdapterForWorkToday extends ListViewAdapterForWork {

    ArrayList<Integer> arrayListID = new ArrayList<Integer>();

    public ListViewAdapterForWorkToday(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fill_for_work_list_today, parent, false);
        return view;
    }

    public void bindView(View view, Context context, Cursor cursor) {

        final Context cont = context;

        workName = (TextView) view.findViewById(R.id.workName);
        workTime = (TextView) view.findViewById(R.id.workTime);

        final String name = cursor.getString(cursor.getColumnIndex("work_name"));
        final String time = cursor.getString(cursor.getColumnIndex("work_time"));
        final int id = cursor.getInt(cursor.getColumnIndex("_id"));

        arrayListID.add(id); // передаем в WorkTodayActivity и затем в слушатель ListView (определяем нажатие по id)

        workName.setText(name);
        workTime.setText(time);
    }
}
