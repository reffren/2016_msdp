package ru.electronim.msuc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tim on 03.01.2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_acsm";
    private static final String TABLE_USER_NAME = "acsm_user";
    private static final String TABLE_WORK_NAME = "acsm_work";
    private static final String TABLE_BRIG_NAME = "acsm_brig";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_USER_CREATE = "create table "+TABLE_USER_NAME+"( " +"_id"+" integer(20),"+ "username varchar(20), password varchar(20));";
    private static final String DATABASE_WORK_CREATE =  "create table "+TABLE_WORK_NAME+"( " +"_id"+" integer(20),"+ "work_date varchar(8), work_name text, work_time varchar(20), work_state varchar(10) default 'не в работе', work_end_time dec(6,4), work_check boolean default 0, work_date_check int(8), work_date_downloaded int(8));";
    private static final String DATABASE_BIG_CREATE =  "create table "+TABLE_BRIG_NAME+"( " +"_id"+" integer primary key autoincrement,"+ "otv_ruk_name varchar(50), nablyoud_name varchar(50), dopusk_name varchar(50), proizv_name varchar(50), vidayoush_name varchar(50), brigada_name text, id_work integer(20));";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_USER_CREATE);
        db.execSQL(DATABASE_WORK_CREATE);
        db.execSQL(DATABASE_BIG_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
