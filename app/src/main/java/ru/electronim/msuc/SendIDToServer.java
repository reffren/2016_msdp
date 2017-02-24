package ru.electronim.msuc;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Tim on 01.04.2016.
 */
public class SendIDToServer { //Отправляем id юзера на сервер, для загрузки контента для определенного юзера, id в каждом скомпиллированном приложении одинаковое, что и на сервере

    int getIdToServer;
    String link;
    String sendIdToServer;
    private static final String USER_KEY = "user_id";

    protected void sendLink(DataBaseAdapter dataBaseAdapter, URLConnection con, String getlink) throws IOException {

        getIdToServer = dataBaseAdapter.getIdToServer();
        link = getlink;

        sendIdToServer = URLEncoder.encode(USER_KEY, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(getIdToServer), "UTF-8");
        con.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(sendIdToServer);
        wr.flush();
    }
}

