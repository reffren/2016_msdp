package ru.electronim.msuc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.HashMap;

public class IncidentsActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnTakePhoto;
    Button btnSendToWork;
    EditText etComment;
    ImageView imageView;
    Bitmap bitMap;
    static int TAKE_PICTURE = 1;
    public static final String UPLOAD_URL = "http://electronim.ru/incidents_activity.php";
    public static final String UPLOAD_KEY = "image";
    DataBaseAdapter dataBaseAdapter;

    String userName;
    int anchor=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intcedents);

        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        btnSendToWork = (Button) findViewById(R.id.btnSendToWork);
        etComment = (EditText) findViewById(R.id.etComment);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnTakePhoto.setOnClickListener(this);
        btnSendToWork.setOnClickListener(this);

        dataBaseAdapter = new DataBaseAdapter(this);
        try {
            dataBaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userName = dataBaseAdapter.getUserName();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnTakePhoto:
                anchor=1;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PICTURE);
                break;
            case R.id.btnSendToWork:
                if(anchor==0) {
                    Toast.makeText(IncidentsActivity.this, "Пожалуйста сделайте фотографию", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
                break;
        }
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK && intent != null) {
            // get bundle
            Bundle extras = intent.getExtras();

            // get
            bitMap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitMap);

        }
    }


    private void uploadImage() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            SendImageToServer sdts = new SendImageToServer();

            public String getStringImage(Bitmap bmp) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                return encodedImage;
            }

            @Override
            protected String doInBackground(Bitmap... params) {

                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                HashMap<String, String> data = new HashMap<>();

                data.put(UPLOAD_KEY, uploadImage);
                String comment = etComment.getText().toString(); // извлекаем замечание инцидента
                String result = sdts.sendPostRequest(UPLOAD_URL, data, userName, comment);
                return result;
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(IncidentsActivity.this, "Данные отправляются на сервер...", null, true, true);
            }

        }

        UploadImage ui = new UploadImage();
        ui.execute(bitMap);

    }
}