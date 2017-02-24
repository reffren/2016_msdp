package ru.electronim.msuc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Tim on 27.02.2016.
 */
public class GetDataFromServer {

    DataBaseAdapter dataBase;

    private JSONArray data = null;

    private static final String JSON_ARRAY ="result";
    private static final String ID = "id";
    private static final String WORK_NAME= "work_name";
    private static final String WORK_TIME = "work_time";
    private static final String JSON_URL = "http://electronim.ru/get_data.php";

    protected void getJSON(DataBaseAdapter dataBaseAdapter) {

            dataBase = dataBaseAdapter;

        BufferedReader bufferedReader = null;

            try {
                URL url = new URL(JSON_URL);
                URLConnection con = url.openConnection();
                StringBuilder sb = new StringBuilder();

                SendIDToServer sendIDToServer = new SendIDToServer(); // отправляем id на сервер для получения данных именно для этого id
                sendIDToServer.sendLink(dataBase, con, JSON_URL); // БД для извлечения id, con - открываем соединение, и 3 параметр это ссылка на файл сервера

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream())); // с этого экземпляра начинаем получать данные с сервера согла вышеуказанного id

                String json;
                while((json = bufferedReader.readLine())!= null){
                    sb.append(json+"\n");
                }

                 extractJSON(sb.toString().trim());

            }catch(Exception e){
            }
    }
    private void extractJSON(String myJSONString){
        try {
            JSONObject jsonObject = new JSONObject(myJSONString);
            data = jsonObject.getJSONArray(JSON_ARRAY);
            for(int i=0; i<data.length(); i++) {
                insertDataToAndroidSql(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertDataToAndroidSql(int i) {
        try {
            JSONObject jsonObject = data.getJSONObject(i);

            int _id = jsonObject.getInt(ID);
            String workName = jsonObject.getString(WORK_NAME);
            String workTime = jsonObject.getString(WORK_TIME);
            String work_downloaded = new CurrentData().yearMonth();

            dataBase.writeDataWork(_id, workName, workTime, work_downloaded);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
