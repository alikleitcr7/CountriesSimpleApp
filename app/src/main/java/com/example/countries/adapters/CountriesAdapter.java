package com.example.countries.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countries.R;
//import com.example.countries.adapters.listeners.OnRatingBarChangeListener;
import com.example.countries.helpers.CountriesOpenHelper;
import com.example.countries.models.Country;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountryViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private final CountriesOpenHelper _countriesOpenHelper;
    private final TextView _displayMessage;
    private LayoutInflater _inflater;
    private List<Country> _countries;
    Context _context;

    // extras
    public static final String EXTRA_CODE = "COUNTRY_CODE";

    // lists
    public CountriesAdapter(Context context, CountriesOpenHelper openHelper, TextView displayMessage) {
        _context = context;
        _inflater = LayoutInflater.from(context);
        //_countries = countries;
        _countriesOpenHelper = openHelper;
        _displayMessage = displayMessage;

        // initialize adapter data from sql open helper
        _countries = _countriesOpenHelper.getAll();
        setCountDisplay();
    }

    public void setCountDisplay() {
        _displayMessage.setText(String.format(_context.getString(R.string.showing_countries), _countries.size()));
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _item = _inflater.inflate(R.layout.countries_item,
                parent, false);

        return new CountryViewHolder(_item, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        Country country = _countries.get(position);

        holder.setCountry(country, position);
    }

    @Override
    public int getItemCount() {
        return _countries.size();
    }


    public void query(String keyword) {

        if (keyword == null || keyword.length() == 0) {
            _countries = _countriesOpenHelper.getAll();

        } else {
            _countries = _countriesOpenHelper.search(keyword);
        }

        setCountDisplay();

        this.notifyDataSetChanged();
    }

    public class CountryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CountriesAdapter _adapter;
        public ImageView _image;
        public TextView _name;
        public TextView _code;
        public RatingBar _rating;

        public CountryViewHolder(View itemView, CountriesAdapter adapter) {
            super(itemView);

            // set item members
            _image = itemView.findViewById(R.id.country_image);
            _name = itemView.findViewById(R.id.country_name);
            _code = itemView.findViewById(R.id.country_code);
            _rating = itemView.findViewById(R.id.ratingBar);

            //_rating.setIsIndicator(false);

            // set adapter
            _adapter = adapter;

            // attach events
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Get the position of the item that was clicked.
//            int mPosition = getLayoutPosition();
//
//            // Use that to access the affected item in mWordList.
//            Country element = _countriesOpenHelper.queryByPosition(mPosition);
//            // Change the word in the mWordList.
//            element.Name += " - Clicked";
//
//            Log.i(TAG, "Clicked item " + element.Name);
//
//            float nextRating = element.Rating == 3 ? 0 : element.Rating + 1;
//
//            //_countries.set(mPosition, element);
//            _countriesOpenHelper.updateRating(element.Code, nextRating);
//            // Notify the adapter, that the data has changed so it can
//            // update the RecyclerView to display the data.
//            _adapter.notifyDataSetChanged();
        }

        public void setCountry(Country country, int position) {

            _name.setText(country.Name);
            _code.setText(country.Code);
            _rating.setRating(country.Rating);


            final int activePosition = position;
            final String code = country.Code;

            _rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (fromUser) {
                        try {

                            int affected = _countriesOpenHelper.updateRating(code, rating);

                            if (affected > 0) {

                                _countries.get(activePosition).Rating = rating;

                                notifyItemChanged(activePosition);
                            }
                        } catch (Exception e) {
                            Log.i(TAG, e.getMessage());
                        }
                    }

                }
            });

            //_image = country.ImageUrl;
        }
    }
}
