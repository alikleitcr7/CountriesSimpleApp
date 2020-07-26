package com.example.countries.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.countries.R;
import com.example.countries.helpers.CountriesOpenHelper;
import com.example.countries.models.Country;
import com.example.countries.services.CountriesService;

import java.lang.ref.WeakReference;
import java.util.List;

public class CountriesAsyncTask extends AsyncTask<Void, Void, List<Country>> {

    private WeakReference<TextView> _textView_displayMessage;
    private WeakReference<TextView> _textView_downloadDescription;
    private WeakReference<Button> _button_loadNext;
    private WeakReference<Button> _button_download;
    private Context _context;
    private CountriesOpenHelper _openHelper;

    public CountriesAsyncTask(
            Context context,
            TextView textView_downloadDescription,
            Button button_download,
            TextView textView_displayMessage,
            Button button_loadNext,
            CountriesOpenHelper openHelper
    ) {

        _context = context;
        _textView_displayMessage = new WeakReference<>(textView_displayMessage);
        _textView_downloadDescription = new WeakReference<>(textView_downloadDescription);
        _button_loadNext = new WeakReference<>(button_loadNext);
        _button_download = new WeakReference<>(button_download);

        _openHelper = openHelper;
    }

    @Override
    protected List<Country> doInBackground(Void... voids) {
        return CountriesService.getAll();
    }

    protected void onPostExecute(List<Country> result) {

        if (result != null && result.size() > 0) {

            // clear db
            _openHelper.clear();

            // insert all countries in db
            int totalRecordsInserted = _openHelper.insert(result);

            String downloadedText = _context.getResources().getString(R.string.countries_downloaded);

            // show feedback
            _textView_displayMessage.get().setText(String.format(downloadedText, result.size(), totalRecordsInserted));
            _button_loadNext.get().setVisibility(View.VISIBLE);

        } else {
            _textView_displayMessage.get().setText(R.string.check_connection);
        }
    }
}
