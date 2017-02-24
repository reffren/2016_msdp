package ru.electronim.msuc;

import java.io.UnsupportedEncodingException;

/**
 * Created by Tim on 03.04.2016.
 */
public class UpdateAllDataInServer {

    DataBaseAdapter dataBase;

    public void updateData(DataBaseAdapter dataBaseAdapter) {

        dataBase=dataBaseAdapter;

        new Thread(new Runnable() {
            public void run() {
                //отправка данных БД на сервер
                SendDataToServer sData = new SendDataToServer();
                try {
                    sData.sendData(dataBase);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                UpdateDataWorkInServer update = new UpdateDataWorkInServer();
                try {
                    update.sendData(dataBase);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
