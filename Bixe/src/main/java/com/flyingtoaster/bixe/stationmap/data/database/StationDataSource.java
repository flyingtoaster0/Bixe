package com.flyingtoaster.bixe.stationmap.data.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.flyingtoaster.bixe.stationmap.models.Station;

import javax.inject.Inject;

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

    @Inject
    public StationDataSource(Context context) {
        dbHelper = new StationDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void putStation(Station station) {
        ContentValues values = StationTable.getContentValue(station);

        database.insertWithOnConflict(StationTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void putStations(List<Station> stations) {
        ContentValues[] values = StationTable.getContentValues(stations);

        for (ContentValues value : values) {
            database.insertWithOnConflict(StationTable.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public void deleteStation(Station station) {
        long id = station.getId();

        database.delete(StationTable.TABLE_NAME, StationTable.Columns.STATION_ID + " = " + id, null);
    }

    public List<Station> getAllStations() {
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

        return station;
    }
} 