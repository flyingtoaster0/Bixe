package com.flyingtoaster.bixe.datasets;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

    public Station createStation(Station station) {
        ContentValues values = new ContentValues();

        values.put(StationTable.Columns.STATION_ID, station.getId());
        values.put(StationTable.Columns.STATION_NAME, station.getStationName());
        values.put(StationTable.Columns.AVAILABLE_BIKES, station.getAvailableBikes());
        values.put(StationTable.Columns.AVAILABLE_DOCKS, station.getAvailableDocks());
        values.put(StationTable.Columns.TOTAL_DOCKS, station.getTotalDocks());
        values.put(StationTable.Columns.LATITUDE, station.getLatitude());
        values.put(StationTable.Columns.LONGITUDE, station.getLongitude());
        values.put(StationTable.Columns.IN_SERVICE, station.isInService());

        long insertId = database.insert(StationTable.TABLE_NAME, null, values);
        Cursor cursor = database.query(StationTable.TABLE_NAME, allColumns, StationTable.Columns.STATION_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Station newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteStation(Station station) {
        long id = station.getId();

        database.delete(StationTable.TABLE_NAME, StationTable.Columns.STATION_ID + " = " + id, null);
    }

    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<Station>();

        Cursor cursor = database.query(StationTable.TABLE_NAME,
                allColumns, null, null, null, null, null);

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