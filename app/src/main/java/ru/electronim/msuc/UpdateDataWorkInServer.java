package ru.electronim.msuc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Tim on 06.03.2016.
 */
public class UpdateDataWorkInServer extends SendDataToServer {

    ArrayList work;
    int sizeWork;
    String dataWork;

    protected String sendData(DataBaseAdapter dataBaseAdapter) throws UnsupportedEncodingException {
        work = new ArrayList();
        work = dataBaseAdapter.getWorkForUpdate(); //присваиваем полученный массив
        sizeWork = work.size(); //определяем размер массива

        try {
            String link="http://electronim.ru/update_data_work.php";

            for(int c=0; c<sizeWork; c++) {
                if(c==0) { //для начала строки (строка начинается)
                    dataWork = URLEncoder.encode(String.valueOf(c), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(work.get(c)), "UTF-8");
                } else { //продолжение строки (строка продолжается)
                    dataWork += "&" + URLEncoder.encode(String.valueOf(c), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(work.get(c)), "UTF-8");
                }
            }

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(dataWork);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString();
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

}
