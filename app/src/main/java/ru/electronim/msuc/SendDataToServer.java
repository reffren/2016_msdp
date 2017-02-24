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
 * Created by Tim on 25.02.2016.
 */
public class SendDataToServer {

    ArrayList brigada;
    int sizeBrig;
    String dataBrig;

    protected String sendData(DataBaseAdapter dataBaseAdapter) throws UnsupportedEncodingException {
        brigada = new ArrayList();
        brigada = dataBaseAdapter.getBrig(); //присваиваем полученный массив
        sizeBrig = brigada.size(); //определяем размер массива

        try {
            String link="http://electronim.ru/send_data_to_server.php";
          //  String username = "ura";
          //  String password = "ura";
            //String link="http://electronim.ru/sync.php";
           sizeBrig=brigada.size();
            for(int i=0; i<sizeBrig; i++) { // перебираем весь массив
                if(i==0) {  //для начала строки (строка начинается)
                    dataBrig = URLEncoder.encode(String.valueOf(i), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(brigada.get(i)), "UTF-8");
                } else { //продолжение строки (строка продолжается)
                    dataBrig += "&" + URLEncoder.encode(String.valueOf(i), "UTF-8") + "=" + URLEncoder.encode(String.valueOf(brigada.get(i)), "UTF-8");
                }
            }

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(dataBrig);
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