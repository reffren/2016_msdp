package ru.electronim.msuc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Created by Tim on 20.01.2016.
 */
public class ListViewAdapterForWork extends CursorAdapter {

    String timeEndString;
    String color = "#696969";
    String curDate;

    TextView workName;
    TextView workTime;
    TextView workEndTime;
    CheckBox checkBox;
    DataBaseAdapter dataBaseAdapter;

    public ListViewAdapterForWork(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fill_for_work_list, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        DecimalFormat decimalFormat = new DecimalFormat("##.###");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);

        final Context cont = context;

        workName = (TextView) view.findViewById(R.id.workName);
        workTime = (TextView) view.findViewById(R.id.workTime);
        checkBox = (CheckBox) view.findViewById(R.id.check);
        workEndTime = (TextView) view.findViewById(R.id.workEndTime);

        workEndTime.setEnabled(false);

        final String name = cursor.getString(cursor.getColumnIndex("work_name"));
        final String time = cursor.getString(cursor.getColumnIndex("work_time"));
        final long id = cursor.getLong(cursor.getColumnIndex("_id"));
        final float timeEnd = cursor.getFloat(cursor.getColumnIndex("work_end_time"));

            timeEndString = decimalFormat.format(timeEnd); // обрезает до тысячной

        if(!time.isEmpty()) { //если времени у работы нет, то значит это вид ремонта (привода, линия отсоса итд)
            workName.setText(name);
            workName.setTextColor(Color.parseColor("#000000"));
            workName.setTypeface(null, Typeface.NORMAL); // устанавливает нормальный шрифт, т.к. в else мы установили жирный (// изменения в else от if(!time.isEmpty()))
            workTime.setText(time);
            workTime.setVisibility(View.VISIBLE);
            workTime.setTextColor(Color.parseColor("#000000"));

            if (timeEnd > 0) { // если время окончания работы существует, то помечаем поле невидимым
                workEndTime.setEnabled(true); // тоже самое здесь если в if установили данное значение, то устанавливаем тот же тип и в else
                workName.setTextColor(Color.parseColor(color));
                workTime.setTextColor(Color.parseColor(color));
                workTime.setVisibility(View.VISIBLE); // изменения в else от if(!time.isEmpty())
                workEndTime.setTextColor(Color.parseColor(color));
                workEndTime.setText(timeEndString); // установили текст, в else устанавливаем тоже   if (timeEnd > 0)
                workEndTime.setVisibility(View.VISIBLE); // изменения в else от if(!time.isEmpty())
                checkBox.setVisibility(View.VISIBLE); // изменения в else от if(!time.isEmpty())
                checkBox.setEnabled(false);
            } else {   // обязательно (ставим else) должно все заполняться по порядку, иначе при скроле листа данные постоянно меняются и прыгают туда-сюда, поэтому добавляем else, т.е. в случае если timeEnd==0, строка заполнялась, а не оставалась пустой
                workEndTime.setEnabled(false); // в if установили true, здесь обязательно устанавливаем тот же тип (true or false)   if (timeEnd > 0)
                workName.setTextColor(Color.parseColor("#000000"));
                workTime.setTextColor(Color.parseColor("#000000"));
                workEndTime.setText(""); // используем тот же метод, что и в if (timeEnd > 0)
                workEndTime.setVisibility(View.VISIBLE); // изменения в else от if(!time.isEmpty())
                workEndTime.setTextColor(Color.parseColor("#000000"));
                checkBox.setVisibility(View.VISIBLE); // изменения в else от if(!time.isEmpty())
                checkBox.setEnabled(true);
                checkBox.setChecked(false);
            }

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
                        dataBaseAdapter.updateWorkDB(id, curDate, true);
                    } else {
                        curDate="";
                        dataBaseAdapter.updateWorkDB(id, curDate, false);
                    }
                }
            });

        } else { //правила для заголовка вида работы (текущий ремонт, межрем итд)

            workName.setText(name);
            workName.setTextColor(Color.parseColor("#E21A1A"));
            workName.setTypeface(null, Typeface.BOLD); // устанавливает жирный шрифт
            workTime.setVisibility(View.GONE); // если здесь убираем view, то в if устанавливаем значение этого же TextView как Visible
            workEndTime.setVisibility(View.GONE); // тоже самое здесь, если что-то меняем, то и в if нужно установить тоже самое
            checkBox.setVisibility(View.GONE); // все должно быть пропорционально, что-то добавил в else, добавляем и в if
        }
        }
    }
