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
    private static int DATABASE_VERSION = 2;

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
                "(" + STOPS_STOPID + " INTEGER PRIMARY KEY," +
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

    /**
     * Method that should reset database contents
     */
    public void forceUpgrade(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION+1);
//        DATABASE_VERSION += 1;
    }

    /**
     * Goes through stops table and prints column names
     */
    public void printColumnNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(STOPS_TABLENAME, null, null, null, null, null, null);
        for (String s: cursor.getColumnNames()){
            System.out.println("col \t" + s);
        }
        cursor.close();
        db.close();
    }

    public void createStop(String stop_id, String short_name, Double lat, Double lng){
        //Check if stopID already exist in the database
        if (this.getStop(stop_id) == null) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(STOPS_STOPID, stop_id);
            values.put(STOPS_SHORTNAME, short_name);
            values.put(STOPS_LAT, lat);
            values.put(STOPS_LNG, lng);

            db.insert(STOPS_TABLENAME, null, values);
            db.close();
        }
    }

    /**
     * @return number of stops in StopsTable
     */
    public int countStops(){
     return getAllStops().size();
    }

    /**
     * Method that gets single stop object from database.
     * @param id representing stop_id column in database
     * @return relevant BusStop object containing short name, and latlng
     */
    public BusStop getStop(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        BusStop stop = null;

        Cursor cursor = db.query(STOPS_TABLENAME, new String[] { STOPS_STOPID,
                        STOPS_SHORTNAME, STOPS_LAT, STOPS_LNG }, STOPS_STOPID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        //Only populate stop if something returned.
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            stop = new BusStop(cursor.getString(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3));
        }

        cursor.close();
        db.close();
        return stop; //returns null if not found
    }

    /**
     * Goes through stops table and returns list of all bus stops
     * @return
     */
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
        cursor.close();
        db.close();

        return all_stops;
    }

    /**
     * Method to update given stop
     * @return int code if successful
     */
    public int updateStop(BusStop stop){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STOPS_STOPID, stop.getStopId());
        values.put(STOPS_SHORTNAME, stop.getShortName());
        values.put(STOPS_LAT, stop.getLat());
        values.put(STOPS_LNG, stop.getLng());

        int return_value =  db.update(STOPS_TABLENAME, values, STOPS_STOPID + " =?", new String[] {stop.getStopId()});
        db.close();
        return return_value;
    }

    /**
     * Delete stop passed in
     */
    public void deleteStop(BusStop stop){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(STOPS_TABLENAME, STOPS_STOPID + " =?", new String[]{stop.getStopId()});
        db.close();
    }
}
