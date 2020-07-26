package com.example.countries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.countries.adapters.CountriesAdapter;
import com.example.countries.helpers.CountriesOpenHelper;
import com.example.countries.tasks.CountriesAsyncTask;

public class CountriesActivity extends AppCompatActivity {

    // elements
    private TextView textView_downloadDescription;
    private TextView textView_downloadProgress;
    private TextView textView_displayMessage;

    private TextView editText_search;

    private Button button_download;
    private Button button_load;

    private RadioButton radio_count10;
    private RadioButton radio_count20;

    private ConstraintLayout view_downloadContainer;
    private ConstraintLayout view_mainContainer;

    private RecyclerView _recyclerView;

    // adapters
    private CountriesAdapter _countriesAdapter;

    // helpers
    private CountriesOpenHelper _countriesOpenHelper;

    // Shared preferences object
    private SharedPreferences _preferences;

    private String maxCountKey = "MAX_COUNT";
    private int _maxCount;

    // Name of shared preferences file
    private String sharedPrefFile = "com.example.android.CountriesPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);


        // init elements
        textView_downloadDescription = findViewById(R.id.textView_downloadDescription);
        textView_downloadProgress = findViewById(R.id.textView_downloadProgress);
        textView_displayMessage = findViewById(R.id.textView_displayMessage);

        editText_search = findViewById(R.id.editText_search);

        button_download = findViewById(R.id.button_download);
        button_load = findViewById(R.id.button_load);

        radio_count10 = findViewById(R.id.radio_count10);
        radio_count20 = findViewById(R.id.radio_count20);

        view_downloadContainer = findViewById(R.id.view_downloadContainer);
        view_mainContainer = findViewById(R.id.view_mainContainer);

        _recyclerView = findViewById(R.id.recyclerView_countries);


        // shared pref
        _preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        _maxCount = _preferences.getInt(maxCountKey, 10);


        // set radio for maxCount
        radio_count10.setChecked(_maxCount == 10);
        radio_count20.setChecked(_maxCount == 20);

        // init helpers
        _countriesOpenHelper = new CountriesOpenHelper(this, _maxCount);

        long count = _countriesOpenHelper.count();

        // init adapters
        //_countriesAdapter =

        setDownloadedStatus(count > 0);

    }

    public void initAdapter() {

        // Create an adapter and supply the data to be displayed.
        _countriesAdapter = new CountriesAdapter(this, _countriesOpenHelper,textView_displayMessage);
        // Connect the adapter with the recycler view.
        _recyclerView.setAdapter(_countriesAdapter);
        // Give the recycler view a default layout manager.
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void doSearch(View view) {

        hideKeyboard(this);

        if (_countriesAdapter == null) {
            initAdapter();
        }

        _countriesAdapter.query(editText_search.getText().toString());
    }

    public void download(View view) {

        // hide download button and text
        button_download.setVisibility(View.INVISIBLE);
        textView_downloadDescription.setVisibility(View.INVISIBLE);

        //  show downloading...
        textView_downloadProgress.setVisibility(View.VISIBLE);
        textView_downloadProgress.setText(R.string.downloading);

        new CountriesAsyncTask(this, textView_downloadDescription, button_download, textView_downloadProgress, button_load, _countriesOpenHelper).execute();
    }

    public void load(View view) {

        initAdapter();
        setDownloadedStatus(true);
    }

    private void setDownloadedStatus(boolean isDownloaded) {

        if (isDownloaded) {
            view_downloadContainer.setVisibility(View.GONE);
            view_mainContainer.setVisibility(View.VISIBLE);

        } else {
            view_downloadContainer.setVisibility(View.VISIBLE);
            view_mainContainer.setVisibility(View.GONE);

            // show download button and text
            button_download.setVisibility(View.VISIBLE);
            textView_downloadDescription.setVisibility(View.VISIBLE);

            // hide downloading
            textView_downloadProgress.setVisibility(View.INVISIBLE);
            button_load.setVisibility(View.INVISIBLE);
        }
    }

    public void reset(View view) {
        hideKeyboard(this);
        editText_search.setText("");
        _countriesOpenHelper.clear();
        setDownloadedStatus(false);
    }

    public void setCount(View view) {

        switch (view.getId()) {
            case (R.id.radio_count10):
                _maxCount = 10;
                break;
            case (R.id.radio_count20):
                _maxCount = 20;
                break;
        }

        _countriesOpenHelper.setCount(_maxCount);

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Callback for activity pause.  Shared preferences are saved here.
     */
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor preferencesEditor = _preferences.edit();
        preferencesEditor.putInt(maxCountKey, _maxCount);
        preferencesEditor.apply();
    }
}
