package ru.electronim.msuc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    DataBaseAdapter dataBaseAdapter;
    EditText textLogin;
    EditText textPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dataBaseAdapter = new DataBaseAdapter(this);
        try {
            dataBaseAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataBaseAdapter.register(2,"admin", "rzd");

        textLogin = (EditText) findViewById(R.id.textLogin);
        textPassword= (EditText) findViewById(R.id.textPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(textLogin.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(textPassword.getWindowToken(), 0);
        String username = textLogin.getText().toString();
        String password = textPassword.getText().toString();
        if (username.length() > 0 && password.length() > 0) {
            try {

                if (dataBaseAdapter.Login(username, password)) {

                    Intent intent = new Intent(LoginActivity.this, ChoiceActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this,
                            "Неверно введен логин или пароль",
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, "Упс, что-то пошло не так",
                        Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(LoginActivity.this,
                    "Пожалуйста заполните поля Логин и Пароль", Toast.LENGTH_LONG).show();
        }
    }
}
