package ru.electronim.msuc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.SQLException;

/**
 * Created by Tim on 20.01.2016.
 */
public class ListViewAdapterForEditWorkToday extends ListViewAdapterForWork {
    String curDate;
    TextView workEndTime;
    public ListViewAdapterForEditWorkToday(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void bindView(View view, Context context, Cursor cursor) {

        final Context cont = context;

        workName = (TextView) view.findViewById(R.id.workName);
        workTime = (TextView) view.findViewById(R.id.workTime);
        checkBox = (CheckBox) view.findViewById(R.id.check);
        workEndTime = (TextView) view.findViewById(R.id.workEndTime);

        final String name = cursor.getString(cursor.getColumnIndex("work_name"));
        final String time = cursor.getString(cursor.getColumnIndex("work_time"));
        final long id = cursor.getLong(cursor.getColumnIndex("_id"));

        workName.setText(name);
        workName.setTextColor(Color.parseColor("#000000"));
        workTime.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.45f));
        workTime.setText(time);
        workTime.setTextColor(Color.parseColor("#000000"));
        workTime.setPadding(0, 0, 70, 0);
        workTime.setGravity(Gravity.CENTER);
        workEndTime.setVisibility(view.GONE);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataBaseAdapter = new DataBaseAdapter(cont);
                try {
                    dataBaseAdapter.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (isChecked) {
                    curDate = new CurrentData().yearMonthDay();
                    dataBaseAdapter.updateWorkDB(id, curDate, false);
                } else {
                    curDate="";
                    dataBaseAdapter.updateWorkDB(id, curDate, true);
                }
            }
        });
    }
}
