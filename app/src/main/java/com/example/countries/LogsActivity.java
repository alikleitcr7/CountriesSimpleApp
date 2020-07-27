package com.example.countries;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

public class LogsActivity extends AppCompatActivity {

    private TextView textView_logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        textView_logs = findViewById(R.id.textView_logs);

        try {

            FileInputStream raw = openFileInput("logs.txt");

            StringBuilder display = new StringBuilder();

            textView_logs.setText("");
            BufferedReader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
            String line = "";
            while ((line = is.readLine()) != null) {
                display.append(line);
            }

            textView_logs.setText(display.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
