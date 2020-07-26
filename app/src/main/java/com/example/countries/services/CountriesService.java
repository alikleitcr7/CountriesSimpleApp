package com.example.countries.services;

import android.util.Log;

import com.example.countries.models.Country;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountriesService {

    private static final String LOG_TAG = CountriesService.class.getSimpleName();

    private static final String BASE_URI = "https://restcountries.eu/rest/v2";


    private static String getUrl(String path) {
        return String.format("%s/%s", BASE_URI, path);
    }

    public static List<Country> getAll() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String json = null;

        Log.d(LOG_TAG, "requesting countries");

        List<Country> countries = new ArrayList<Country>();

        try {
            URL requestURL = new URL(getUrl("all"));

            // Open the network connection.
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Create a buffered reader from that input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use a StringBuilder to hold the incoming response.
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                // Add the current line to the string.
                builder.append(line);

                // Since this is JSON, adding a newline isn't necessary (it won't
                // affect parsing) but it does make debugging a *lot* easier
                // if you print out the completed buffer for debugging.
                builder.append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty.  Exit without parsing.
                return null;
            }

            json = builder.toString();
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject country = arr.getJSONObject(i);

                Country parsed = parse(country);

                if (parsed != null) {
                    countries.add(parsed);
                } else {
                    Log.e(LOG_TAG, "unable to parse country at " + i);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the connection and the buffered reader.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Write the final JSON response to the log
        Log.d(LOG_TAG, "parsed countries: " + countries.size());

        return countries;
    }

    private static Country parse(JSONObject country) {
        try {
            Country parsed = new Country();

            parsed.ImageUrl = country.getString("flag");
            parsed.Name = country.getString("name");
            parsed.Code = country.getString("alpha2Code");
            //parsed.Rating = 0;

            return parsed;
        } catch (Exception ex) {
            return null;
        }
    }
}
