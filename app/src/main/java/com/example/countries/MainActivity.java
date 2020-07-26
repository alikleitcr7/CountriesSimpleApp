package com.example.countries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String USERNAME = "ali";
    private static final String PASSWORD = "123";

    public static final String USER_NAME = "USER_NAME";

    // Elements
    private EditText editText_username;
    private EditText editText_password;

    private TextView textView_wrongCredentials;

    private Button button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize
        editText_username = findViewById(R.id.editText_username);
        editText_password = findViewById(R.id.editText_password);

        textView_wrongCredentials = findViewById(R.id.textView_wrongCredentials);

        button_login = findViewById(R.id.button_login);


    }

    public void login(View view) {

        String username = editText_username.getText().toString();
        String password = editText_password.getText().toString();

        boolean isValid = username.equals(USERNAME) && password.equals(PASSWORD);

        editText_password.setText("");

        if (!isValid) {
            textView_wrongCredentials.setVisibility(View.VISIBLE);
        } else {

            Intent intent = new Intent(this, CountriesActivity.class);

            intent.putExtra(USER_NAME, username);

            startActivity(intent);
        }

    }
}
