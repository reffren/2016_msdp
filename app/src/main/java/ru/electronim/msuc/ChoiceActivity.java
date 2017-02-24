package ru.electronim.msuc;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChoiceActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnIncidents;
    Button btnWork;
    Button btnSync;
    TextView tvSync;

    int serverResponseCode = 0;
    int size;
    int plusSize=0;
    ProgressDialog dialog = null;

    DataBaseAdapter dataBaseAdapter;
    ArrayList<Integer> nameRecord;

    SendDataToServer s;

    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

         s = new SendDataToServer();


        btnIncidents = (Button) findViewById(R.id.btnIncidents);
        btnWork = (Button) findViewById(R.id.btnWork);
        btnSync = (Button) findViewById(R.id.btnSync);
        tvSync = (TextView) findViewById(R.id.tvSync);

        btnIncidents.setOnClickListener(this);
        btnWork.setOnClickListener(this);
        btnSync.setOnClickListener(this);

        dataBaseAdapter = new DataBaseAdapter(this);
        try {
            dataBaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnIncidents:
                Intent intent_inc = new Intent(this, IncidentsActivity.class);
                startActivity(intent_inc);
                break;
            case R.id.btnWork:
                 Intent intent_work = new Intent(this, WorkActivity.class);
                 startActivity(intent_work);
                break;
            case R.id.btnSync:
                verifyStoragePermissions(this); // start permission method
                // get id from DataBaseAdapter (equal date)
                nameRecord = dataBaseAdapter.getSyncData(); // получаем данные в виде аррай листа сверенные с текущей датой
                size = nameRecord.size();
                dialog = ProgressDialog.show(ChoiceActivity.this, "", "Синхронизация с сервером...", true);
                new Thread(new Runnable() {
                    public void run() {
                        // загрузка аудио файлов
                        if(size>0) {
                            for (int i = 0; i < size; i++) {
                                String idFile = String.valueOf(nameRecord.get(i));
                                fileName = "/sdcard/" + idFile + ".3gp";

                                uploadFile(fileName);
                            }
                        }
                        //загрузка данных БД по работе на месяц с сервера
                        if(dataBaseAdapter.downloadedTable()) { // проверяем загружены ли данные с сервера на текущий месяц
                            GetDataFromServer getData = new GetDataFromServer();
                            getData.getJSON(dataBaseAdapter); //записываем данные месячных работ из БД сервера в текущую БД
                        }
                        //отправка данных БД на сервер
                        SendDataToServer sData = new SendDataToServer();
                        try {
                            sData.sendData(dataBaseAdapter);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        UpdateDataWorkInServer update = new UpdateDataWorkInServer();
                        try {
                            update.sendData(dataBaseAdapter);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss(); // stop dialog
                        showToast(); // show dialog
                    }
                }).start();

                break;
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------
    // synchronization process
    // permission for reading of file

    private static final int REQUEST_EXTERNAL_STORAGE = 1; // For API 23+ you need to request the read/write permissions even if they are already in your manifest
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


//-----------------------------------------------------------------------------------------------------------------------
// upload file
    public int uploadFile(String sourceFileUri) {
      //  verifyStoragePermissions(this); // start permission method
        String upLoadServerUri = "http://electronim.ru/choice_activity.php";
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
           // Toast.makeText(ChoiceActivity.this, "Проблема с синхронизацией (файла не существует)", Toast.LENGTH_SHORT).show();
            Log.e("uploadFile", "Проблема с синхронизацией (файла не существует)");
            dialog.dismiss();
            return 0;
        }
        try { // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            if(serverResponseCode == 200){
                runOnUiThread(new Runnable() {
                    public void run() {
                        plusSize++;
                        if (size==plusSize) {
                            plusSize=0;
                            dialog.dismiss();
                            //Toast.makeText(ChoiceActivity.this, "Синхронизация успешно завершена", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            dialog.dismiss();
            ex.printStackTrace();
            Toast.makeText(ChoiceActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
           // Log.e("Загрузка Файла на сервер", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
            Toast.makeText(ChoiceActivity.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
           // Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
        }
        return serverResponseCode;
    }
//------------------------------------------------------------------------------------------------------------------------
   // show dialog
    public void showToast() {
        ChoiceActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ChoiceActivity.this, "Синхронизация успешно завершена", Toast.LENGTH_SHORT).show();
            }
        });
    }
}