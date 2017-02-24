package ru.electronim.msuc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Tim on 08.01.2016.
 */
public class SendImageToServer {

    String fileName = new CurrentData().currentTime();
    String UPLOAD_KEY = "filename";

    String _userName;
    String _comment;
    String UPLOAD_NAME_FOLDER = "nameFolder";
    String COMMENT = "comment";

    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams, String userName, String comment) {

        _userName = userName; //извлекаем имя юзера для папки на сервере
        _comment = comment; // извлекаем запись инцидента

        URL url;

        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                while ((response = br.readLine()) != null){
                    sb.append(response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(UPLOAD_KEY, "UTF-8")); //передаем ключ имени файла для изображения
            result.append("=");
            result.append(URLEncoder.encode(fileName, "UTF-8")); //передаем имя файла изображения
            result.append("&"); //раздел между именем файла и именем юзера
            result.append(URLEncoder.encode(UPLOAD_NAME_FOLDER, "UTF-8")); //передаем ключ юзера
            result.append("=");
            result.append(URLEncoder.encode(_userName, "UTF-8")); //передаем имя юзера
            result.append("&"); //раздел между именем юзера comment
            result.append(URLEncoder.encode(COMMENT, "UTF-8")); //передаем ключ comment
            result.append("=");
            result.append(URLEncoder.encode(_comment, "UTF-8")); //передаем имя comment
            result.append("&"); //раздел между comment и самим изображением
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8")); //передаем ключ изображения
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8")); //передаем само изображение
        }

        return result.toString();
    }
}
