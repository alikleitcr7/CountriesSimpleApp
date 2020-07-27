package com.example.countries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_login);


        // Initialize
        editText_username = findViewById(R.id.editText_username);
        editText_password = findViewById(R.id.editText_password);

        textView_wrongCredentials = findViewById(R.id.textView_wrongCredentials);

        button_login = findViewById(R.id.button_login);

        // saved instances
        if (savedInstanceState != null) {
            String textView_wrongCredentials_text = savedInstanceState.getString("textView_wrongCredentials_text");

            if (textView_wrongCredentials_text != null) {
                textView_wrongCredentials.setVisibility(View.VISIBLE);
                textView_wrongCredentials.setText(textView_wrongCredentials_text);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String textView_wrongCredentials_text = textView_wrongCredentials.getText().toString();

        if (textView_wrongCredentials_text.length() != 0) {
            outState.putString("textView_wrongCredentials_text", textView_wrongCredentials_text);
        }
    }

    public void login(View view) {

        String username = editText_username.getText().toString();
        String password = editText_password.getText().toString();

        boolean isValid = username.equals(USERNAME) && password.equals(PASSWORD);

        editText_password.setText("");

        writeLog(username);

        if (!isValid) {
            textView_wrongCredentials.setVisibility(View.VISIBLE);
        } else {

            Intent intent = new Intent(this, CountriesActivity.class);

            intent.putExtra(USER_NAME, username);

            startActivity(intent);
        }

    }


    public void writeLog(String username) {

        String log = String.format("Username: %s at %s", username, Calendar.getInstance().getTime().toString());

        PrintWriter pw = null;
        try {

            FileOutputStream out = null;
            out =  openFileOutput("logs.txt",MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(out);
            osw.write(log );
            osw.flush();
            osw.close();
        }
        catch(IOException ex){
            System.err.println(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, log, Toast.LENGTH_LONG).show();
    }

    public void goToLogs(View view) {
        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }
}
