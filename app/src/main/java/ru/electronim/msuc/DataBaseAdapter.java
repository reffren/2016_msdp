package ru.electronim.msuc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tim on 03.01.2016.
 */
public class DataBaseAdapter {
    private static final String DATABASE_TABLE = "acsm_user";
    public static final String KEY_USER_ID = "_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private static final String DATABASE_WORK_TABLE = "acsm_work";
    public static final String KEY_WORK_ID = "_id";
    public static final String KEY_WORK_DATE = "work_date";
    public static final String KEY_WORK_NAME = "work_name";
    public static final String KEY_WORK_TIME = "work_time";
    public static final String KEY_WORK_STATE = "work_state";
    public static final String KEY_WORK_END_TIME = "work_end_time";
    public static final String KEY_WORK_CHECK = "work_check";
    public static final String KEY_WORK_DATE_CHECK = "work_date_check";
    public static final String KEY_DATE_DOWNLOADED = "work_date_downloaded";

    private static final String DATABASE_BRIG_TABLE = "acsm_brig";
    public static final String KEY_BRIG_OTV = "otv_ruk_name";
    public static final String KEY_BRIG_NABL = "nablyoud_name";
    public static final String KEY_BRIG_DOPUSK = "dopusk_name";
    public static final String KEY_BRIG_PROIZV = "proizv_name";
    public static final String KEY_BRIG_VIDAYOUSH = "vidayoush_name";
    public static final String KEY_BRIG_BRIGADA = "brigada_name";
    public static final String KEY_ID_WORK = "id_work";

    public String currentDate = new CurrentData().yearMonth();


    SQLiteDatabase sqLiteDatabase;
    Context context;
    DataBaseHelper dataBaseHelper;

    ContentValues contentValues = new ContentValues();

    public DataBaseAdapter(Context context) {
        this.context = context;

    }

    public DataBaseAdapter open() throws SQLException {
        dataBaseHelper = new DataBaseHelper(context);
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dataBaseHelper.close();
    }

    //put data into login table, it is for LoginActivity
    public long register(int id, String user, String pass) {
        contentValues.put(KEY_USER_ID, id);
        contentValues.put(KEY_USERNAME, user);
        contentValues.put(KEY_PASSWORD, pass);
        return sqLiteDatabase.insert(DATABASE_TABLE, null, contentValues);
    }

    //put data into work table, it is for PlanActivity
    public long writeDataWork(int _id, String work_name, String work_time, String work_date_downloaded) { //записываем данные с сервера по работе на месяц
        contentValues.put(KEY_WORK_ID, _id);
        contentValues.put(KEY_WORK_NAME, work_name);
        contentValues.put(KEY_WORK_TIME, work_time);
        contentValues.put(KEY_DATE_DOWNLOADED, work_date_downloaded);
        return sqLiteDatabase.insert(DATABASE_WORK_TABLE, null, contentValues);
    }

    //select data from login table, it is for LoginActivity
    public boolean Login(String username, String password) throws SQLException {
        Cursor mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?", new String[]{username, password});
        if (mCursor != null) {
            if (mCursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    //update work table for mark boolean value
    public void updateWorkDB(long id, String curDate, boolean check) {
        contentValues.put(KEY_WORK_CHECK, check);
        contentValues.put(KEY_WORK_DATE_CHECK, curDate);
        sqLiteDatabase.update(DATABASE_WORK_TABLE, contentValues, KEY_WORK_ID + "=" + id, null);
    }

    public void writeBrig(List<EditText> allBrig, String _etOtvRuk, String _etNabl, String _etDopusk, String _etProizv, String _etVidayoush, String id) {
        String brig = "";
        contentValues.put(KEY_BRIG_OTV, _etOtvRuk);
        contentValues.put(KEY_BRIG_NABL, _etNabl);
        contentValues.put(KEY_BRIG_DOPUSK, _etDopusk);
        contentValues.put(KEY_BRIG_PROIZV, _etProizv);
        contentValues.put(KEY_BRIG_VIDAYOUSH, _etVidayoush);
        for (int i = 0; i < allBrig.size(); i++) {
            brig += (String) (allBrig.get(i).getText().toString());
            brig += ", ";
        }
        contentValues.put(KEY_BRIG_BRIGADA, brig);
        contentValues.put(KEY_ID_WORK, id);
        sqLiteDatabase.insert(DATABASE_BRIG_TABLE, null, contentValues);
    }

    public void writeTime(String date, String idForName, float time, String workEnd) {
        int id = Integer.parseInt(idForName);
        contentValues.put(KEY_WORK_DATE, date);
        contentValues.put(KEY_WORK_END_TIME, time);
        contentValues.put(KEY_WORK_STATE, workEnd);
        contentValues.put(KEY_WORK_CHECK, 0);
        sqLiteDatabase.update(DATABASE_WORK_TABLE, contentValues, KEY_WORK_ID + "=" + id, null);
    }

    public void writeState(String workState, String id) {
        contentValues.put(KEY_WORK_STATE, workState);
        sqLiteDatabase.update(DATABASE_WORK_TABLE, contentValues, KEY_WORK_ID + "=" + id, null);
    }

    // get all data from work db for fill work list
    public Cursor getAllData() {
        return sqLiteDatabase.rawQuery("SELECT " + KEY_WORK_ID + "," + KEY_WORK_NAME + "," + KEY_WORK_TIME + "," + KEY_WORK_END_TIME + " FROM " + DATABASE_WORK_TABLE + " WHERE " + KEY_DATE_DOWNLOADED + " LIKE " + currentDate, null);

    }

    public Cursor getChosenDataForWorkToday() {
        return sqLiteDatabase.rawQuery("SELECT * FROM " + DATABASE_WORK_TABLE + " WHERE " + KEY_WORK_CHECK + "=1", null);
    }

    public String getDataForWork(String _id) {
        int id = Integer.valueOf(_id);
        Cursor data = sqLiteDatabase.rawQuery("SELECT " + KEY_WORK_NAME + " FROM " + DATABASE_WORK_TABLE + " WHERE " + KEY_WORK_ID + "=" + id, null);
        data.moveToFirst();
        String name = data.getString(data.getColumnIndex(KEY_WORK_NAME));
        return name;
    }

    public ArrayList<Integer> getSyncData() {
        // current date
        ArrayList<Integer> allRecord = new ArrayList<Integer>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String curDate = sdf.format(new Date());

        Cursor c = sqLiteDatabase.rawQuery("SELECT " + KEY_WORK_ID + " FROM " + DATABASE_WORK_TABLE + " WHERE " + KEY_WORK_DATE + " LIKE " + curDate, null);
        if (c.moveToFirst()) {
            do {
                int idRecord = c.getInt(c.getColumnIndex(KEY_WORK_ID));
                allRecord.add(idRecord);
            } while (c.moveToNext());
        }
        return allRecord;
    }

    public Boolean downloadedTable() { // проверяем существуют ли в таблице данные
        Cursor downloaded = sqLiteDatabase.rawQuery("SELECT " + KEY_DATE_DOWNLOADED + " FROM " + DATABASE_WORK_TABLE + " WHERE " + KEY_DATE_DOWNLOADED + " LIKE " + currentDate, null);

        if (downloaded.moveToFirst()) { // если нет данных в таблице то возвращаем true
            return false; // если есть то false
        } else {
            return true;
        }
    }

    public String getUserName() {
        Cursor user = sqLiteDatabase.rawQuery("SELECT " + KEY_USERNAME + " FROM " + DATABASE_TABLE, null);
        if (user.moveToFirst()) {
            String userName = user.getString(user.getColumnIndex(KEY_USERNAME));
            return userName;
        } else {
            return "noName";
        }
    }

    public ArrayList getBrig() { //извлекаем все данные из таблицы бригада и помещаем в массив
        ArrayList brigada = new ArrayList();
        Cursor brig = sqLiteDatabase.rawQuery("SELECT * FROM " + DATABASE_BRIG_TABLE, null);
        if (brig.moveToFirst()) {
            do {
                String otv = brig.getString(brig.getColumnIndex(KEY_BRIG_OTV));
                brigada.add(otv);
                String nabl = brig.getString(brig.getColumnIndex(KEY_BRIG_NABL));
                brigada.add(nabl);
                String dopusk = brig.getString(brig.getColumnIndex(KEY_BRIG_DOPUSK));
                brigada.add(dopusk);
                String proizv = brig.getString(brig.getColumnIndex(KEY_BRIG_PROIZV));
                brigada.add(proizv);
                String vid = brig.getString(brig.getColumnIndex(KEY_BRIG_VIDAYOUSH));
                brigada.add(vid);
                String br = brig.getString(brig.getColumnIndex(KEY_BRIG_BRIGADA));
                brigada.add(br);
                Integer idWork = brig.getInt(brig.getColumnIndex(KEY_ID_WORK));
                brigada.add(idWork);
            } while (brig.moveToNext());
        }
        return brigada;
    }

    public ArrayList getWorkForUpdate() { // извлекаем данные из даблицы acsm_work для апдейта БД на сервере
        ArrayList getWork = new ArrayList();
        Cursor work = sqLiteDatabase.rawQuery("SELECT * FROM " + DATABASE_WORK_TABLE, null);
        if (work.moveToFirst()) {
            do {
                int workId = work.getInt(work.getColumnIndex(KEY_WORK_ID));
                getWork.add(workId);
                String workDate = work.getString(work.getColumnIndex(KEY_WORK_DATE));
                getWork.add(workDate);
                String workState = work.getString(work.getColumnIndex(KEY_WORK_STATE));
                getWork.add(workState);
                String workEndTime = work.getString(work.getColumnIndex(KEY_WORK_END_TIME));
                getWork.add(workEndTime);
                String workCheck = work.getString(work.getColumnIndex(KEY_WORK_CHECK));
                getWork.add(workCheck);
                String workDateCheck = work.getString(work.getColumnIndex(KEY_WORK_DATE_CHECK));
                getWork.add(workDateCheck);
                String workDateDownloaded = work.getString(work.getColumnIndex(KEY_DATE_DOWNLOADED));
                getWork.add(workDateDownloaded);
            } while (work.moveToNext());
        }
        return getWork;
    }

    public Integer getIdToServer() {
        Cursor getId = sqLiteDatabase.rawQuery("SELECT " + KEY_USER_ID + " FROM " + DATABASE_TABLE, null);
        int getIdToServer=0;
        if (getId.moveToFirst()) {
            getIdToServer = getId.getInt(getId.getColumnIndex(KEY_USER_ID));
        }
        return getIdToServer;
    }
}