package com.example.theooswanditosw164.firstyone.dataclasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheoOswandi on 8/09/2017.
 */

public class SqliteTransportDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TransportDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String STOPS_TABLENAME = "stops_table";
    private static final String STOPS_STOPID = "stop_id";
    private static final String STOPS_SHORTNAME = "short_name";
    private static final String STOPS_LAT = "stop_lat";
    private static final String STOPS_LNG = "stop_lng";


    public SqliteTransportDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STOPS_TABLE = "CREATE TABLE " + STOPS_TABLENAME +
                "(" + STOPS_STOPID + "INTEGER PRIMARY KEY," +
                STOPS_SHORTNAME + " TEXT," +
                STOPS_LAT + " FLOAT," +
                STOPS_LNG + " FLOAT);";
        db.execSQL(CREATE_STOPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO for all tables created
        db.execSQL("DROP TABLE IF EXISTS " + STOPS_TABLENAME);


        onCreate(db);
    }

    public void createStop(String stop_id, String short_name, Double lat, Double lng){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STOPS_STOPID, stop_id);
        values.put(STOPS_SHORTNAME, short_name);
        values.put(STOPS_LAT, lat);
        values.put(STOPS_LNG, lng);

        db.insert(STOPS_TABLENAME, null, values);
        db.close();
    }

    public List<BusStop> getAllStops(){
        List<BusStop> all_stops = new ArrayList<BusStop>();

        String query = "SELECT * FROM " + STOPS_TABLENAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                BusStop stop = new BusStop();
                stop.setStopId(cursor.getString(0));
                stop.setShortName(cursor.getString(1));
                stop.setLat(cursor.getDouble(2));
                stop.setLng(cursor.getDouble(3));

                all_stops.add(stop);
            } while (cursor.moveToNext());
        }

        return all_stops;
    }
}
