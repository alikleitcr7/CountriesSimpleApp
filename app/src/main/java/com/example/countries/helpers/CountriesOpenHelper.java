package com.example.countries.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.countries.models.Country;

import java.util.ArrayList;
import java.util.List;

public class CountriesOpenHelper extends SQLiteOpenHelper {

    // tag for logging
    private static final String TAG = CountriesOpenHelper.class.getSimpleName();

    // has to be 1 first time or app will crash
    private static final int DATABASE_VERSION = 1;
    private static final String COUNTRIES_TABLE = "countries";
    private static final String DATABASE_NAME = "countriesList";

    // Column names
    public static final String KEY_NAME = "name";
    public static final String KEY_CODE = "code";
    public static final String KEY_RATING = "rating";
    public static final String KEY_IMAGE_LINK = "imageLink";

    // array of columns
    private static final String[] COLUMNS = {KEY_NAME, KEY_CODE, KEY_RATING, KEY_IMAGE_LINK};

    // Build the SQL query that creates the table.
    private static final String COUNTRIES_TABLE_CREATE =
            String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s REAL, %s TEXT);",
                    COUNTRIES_TABLE, KEY_CODE, KEY_NAME, KEY_RATING, KEY_IMAGE_LINK);


    // SQLite read/write
    private SQLiteDatabase _writableDB;
    private SQLiteDatabase _readableDB;

    private int _maxCount;

    // Some initial data (not gonna use it here since we are downloading our data from an api)
    static List<Country> INIT_COUNTRIES = new ArrayList<Country>() {
        {
            add(new Country("Lebanon", "LB", 1, "https://restcountries.eu/data/lbn.svg"));
            add(new Country("Switzerland", "CH", 3, "https://cdn.countryflags.com/thumbs/switzerland/flag-800.png"));
        }
    };

    public CountriesOpenHelper(Context context, int maxCount) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _maxCount = maxCount;
        Log.d(TAG, "Construct CountriesListOpenHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COUNTRIES_TABLE_CREATE);
        //initializeData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + COUNTRIES_TABLE);
        onCreate(db);
    }

    public long insert(Country country) {

        long newId = 0;

        ContentValues values = new ContentValues();

        values.put(KEY_CODE, country.Code);
        values.put(KEY_NAME, country.Name);
        values.put(KEY_RATING, country.Rating);
        values.put(KEY_IMAGE_LINK, country.ImageUrl);

        try {
            if (_writableDB == null) {
                _writableDB = getWritableDatabase();
            }
            newId = _writableDB.insert(COUNTRIES_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        return newId;
    }

    public int insert(List<Country> countries) {

        int numberOfRecrodsAdded = 0;

        for (Country country : countries) {
            if (insert(country) > 0) {
                numberOfRecrodsAdded++;
            }
        }

        return numberOfRecrodsAdded;
    }


    public int updateRating(String code, float rating) {

        int mNumberOfRowsUpdated = -1;

        try {

            if (_writableDB == null) {
                _writableDB = getWritableDatabase();
            }

            ContentValues values = new ContentValues();

            values.put(KEY_RATING, rating);

            mNumberOfRowsUpdated = _writableDB.update(COUNTRIES_TABLE, //table to change
                    values, // new values to insert
                    KEY_CODE + " = ?", // selection criteria for row (in this case, the _id column)
                    new String[]{code}); //selection args; the actual value of the id

        } catch (Exception e) {
            Log.d(TAG, "UPDATE EXCEPTION! " + e.getMessage());
        }
        return mNumberOfRowsUpdated;
    }

    public List<Country> search(String keyword) {

        // filter by name or code that matches the keyword
        String query = String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%' OR %s LIKE '%%%s%%' ORDER BY %s ASC LIMIT %d",
                COUNTRIES_TABLE, KEY_NAME, keyword, KEY_CODE, keyword, KEY_CODE, _maxCount);

        //" ORDER BY " + KEY_WORD + " ASC " +
        //"LIMIT " + position + ",1";

        Cursor cursor = null;
        List<Country> countries = new ArrayList<Country>();

        try {

            if (_readableDB == null) {
                _readableDB = getReadableDatabase();
            }

            cursor = _readableDB.rawQuery(query, null);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                //cursor.moveToFirst();
                countries.add(parse(cursor));
            }

        } catch (Exception e) {
            Log.d(TAG, "QUERY EXCEPTION! " + e.getMessage());
        } finally {
            // Must close cursor and db now that we are done with it.
            cursor.close();
            return countries;
        }
    }

    public List<Country> getAll() {

        String query = String.format("SELECT * FROM %s ORDER BY %s ASC LIMIT %d", COUNTRIES_TABLE, KEY_CODE, _maxCount);

        //" ORDER BY " + KEY_WORD + " ASC " +
        //"LIMIT " + position + ",1";

        Cursor cursor = null;
        List<Country> countries = new ArrayList<Country>();

        try {

            if (_readableDB == null) {
                _readableDB = getReadableDatabase();
            }

            cursor = _readableDB.rawQuery(query, null);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                //cursor.moveToFirst();
                countries.add(parse(cursor));
            }


        } catch (Exception e) {
            Log.d(TAG, "GET ALL EXCEPTION! " + e.getMessage());
        } finally {
            // Must close cursor and db now that we are done with it.
            cursor.close();
            return countries;
        }
    }

    public void clear() {
        try {
            if (_writableDB == null) {
                _writableDB = getWritableDatabase();
            }

            _writableDB.delete(COUNTRIES_TABLE, null, null);

        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
    }

    public long count() {
        if (_readableDB == null) {
            _readableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(_readableDB, COUNTRIES_TABLE);
    }

    private Country parse(Cursor cursor) {
        try {
            Country entry = new Country();
            entry.Code = GetStringColumnFromCursor(cursor, KEY_CODE);
            entry.Name = GetStringColumnFromCursor(cursor, KEY_NAME);
            entry.ImageUrl = GetStringColumnFromCursor(cursor, KEY_IMAGE_LINK);
            entry.Rating = GetFloatColumnFromCursor(cursor, KEY_RATING);
            return entry;
        } catch (Exception ex) {
            return null;
        }
    }

    private String GetStringColumnFromCursor(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndex(name));
    }

    private float GetFloatColumnFromCursor(Cursor cursor, String name) {
        return cursor.getFloat(cursor.getColumnIndex(name));
    }

    private void initializeData(SQLiteDatabase db) {

        // Create a container for the data.
        ContentValues values = new ContentValues();

        for (int i = 0; i < INIT_COUNTRIES.size(); i++) {
            Country country = INIT_COUNTRIES.get(i);

            // Put column/value pairs into the container. put() overwrites existing values.
            values.put(KEY_CODE, country.Code);
            values.put(KEY_NAME, country.Name);
            values.put(KEY_RATING, country.Rating);
            values.put(KEY_IMAGE_LINK, country.ImageUrl);

            db.insert(COUNTRIES_TABLE, null, values);
        }
    }

    public void setCount(int count) {
        _maxCount = count;
    }
}
