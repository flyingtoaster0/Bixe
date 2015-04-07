package com.flyingtoaster.bixe.datasets;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.models.Station;

public class StationDataSource {

    // Database fields
    private SQLiteDatabase database;
    private StationDatabaseHelper dbHelper;
        private String[] allColumns = {StationTable.Columns.STATION_ID,
                StationTable.Columns.STATION_NAME,
                StationTable.Columns.AVAILABLE_BIKES,
                StationTable.Columns.AVAILABLE_DOCKS,
                StationTable.Columns.TOTAL_DOCKS,
                StationTable.Columns.LATITUDE,
                StationTable.Columns.LONGITUDE,
                StationTable.Columns.IN_SERVICE};

    public StationDataSource(Context context) {
        dbHelper = new StationDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createStation(Station station) {
        ContentValues values = StationTable.getContentValue(station);

        database.insertWithOnConflict(StationTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        BixeApplication.getAppContext().getContentResolver().notifyChange(BixeContentProvider.CONTENT_URI, null);
    }

    public void createStations(List<Station> stations) {
        ContentValues[] values = StationTable.getContentValues(stations);


        for (ContentValues value : values) {
            database.insertWithOnConflict(StationTable.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
        }

        BixeApplication.getAppContext().getContentResolver().notifyChange(BixeContentProvider.CONTENT_URI, null);
    }

    public void deleteStation(Station station) {
        long id = station.getId();

        database.delete(StationTable.TABLE_NAME, StationTable.Columns.STATION_ID + " = " + id, null);
    }

    public ArrayList<Station> getAllStations() {
        ArrayList<Station> stations = new ArrayList<Station>();

        Cursor cursor = database.query(StationTable.TABLE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Station station = cursorToComment(cursor);
            stations.add(station);
            cursor.moveToNext();
        }

        cursor.close();
        return stations;
    }

    private Station cursorToComment(Cursor cursor) {
        Station station = new Station();

        station.setId(cursor.getInt(0));
        station.setStationName(cursor.getString(1));
        station.setAvailableBikes(cursor.getInt(2));
        station.setAvailableDocks(cursor.getInt(3));
        station.setTotalDocks(cursor.getInt(4));
        station.setLatitude(cursor.getDouble(5));
        station.setLongitude(cursor.getDouble(6));
        station.setInService(cursor.getInt(7) == 1);

        return station;
    }
} 