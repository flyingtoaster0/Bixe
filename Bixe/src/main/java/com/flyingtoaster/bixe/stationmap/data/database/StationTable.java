package com.flyingtoaster.bixe.stationmap.data.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.flyingtoaster.bixe.stationmap.models.Station;

import java.util.ArrayList;
import java.util.List;

public class StationTable {
    public static final String TABLE_NAME = "station_table";

    public static class Columns {
        public static final String _ID = "_id";
        public static final String STATION_ID = "station_id";
        public static final String STATION_NAME = "station_name";
        public static final String AVAILABLE_BIKES = "available_bikes";
        public static final String AVAILABLE_DOCKS = "available_docks";
        public static final String TOTAL_DOCKS = "total_docks";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String IN_SERVICE = "in_service";
    }

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
//            + Columns._ID + " integer primary key autoincrement, "
            + Columns.STATION_ID + " integer primary key not null, "
            + Columns.STATION_NAME + " text,"
            + Columns.AVAILABLE_BIKES + " integer,"
            + Columns.AVAILABLE_DOCKS + " integer,"
            + Columns.TOTAL_DOCKS + " integer,"
            + Columns.LATITUDE + " real,"
            + Columns.LONGITUDE + " real,"
            + Columns.IN_SERVICE + " integer"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        Log.w("TEST", "***************************DB CREATED????");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(StationTable.class.getName(), "Upgrading database from version\n\n\n\n\n\n\n "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public static ContentValues[] getContentValues(List<Station> stations) {
        final ArrayList<ContentValues> values = new ArrayList<>();

        for (Station station : stations) {
            values.add(getContentValue(station));
        }

        return values.toArray(new ContentValues[values.size()]);
    }

    public static ContentValues getContentValue(Station station) {
        ContentValues values = new ContentValues();

        values.put(StationTable.Columns.STATION_ID, station.getId());
        values.put(StationTable.Columns.STATION_NAME, station.getStationName());
        values.put(StationTable.Columns.AVAILABLE_BIKES, station.getAvailableBikes());
        values.put(StationTable.Columns.AVAILABLE_DOCKS, station.getAvailableDocks());
        values.put(StationTable.Columns.TOTAL_DOCKS, station.getTotalDocks());
        values.put(StationTable.Columns.LATITUDE, station.getLatitude());
        values.put(StationTable.Columns.LONGITUDE, station.getLongitude());

        return values;
    }
}
