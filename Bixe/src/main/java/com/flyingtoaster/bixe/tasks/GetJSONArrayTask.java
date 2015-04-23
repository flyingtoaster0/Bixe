package com.flyingtoaster.bixe.tasks;

import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;
import android.os.AsyncTask;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.models.Station;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
public class GetJsonArrayTask extends AsyncTask<Void, Void, JsonArray> {

    public static final String API_URL = "http://www.bikesharetoronto.com/stations/json";
    public static final String TAG = "GetJsonArrayTask";
    private GetJSONArrayListener listener;

    private JsonArray jArray;
    private OkHttpClient client = new OkHttpClient();

    public GetJsonArrayTask(GetJSONArrayListener listener) {
        this.listener = listener;
    }

    protected JsonArray doInBackground(Void... params) {
        String result;

        try {
            result = makeRequest(API_URL);
        } catch (IOException e) {
            Log.i(TAG, e.toString());
            listener.onJSONArrayFailed();
            return null;
        }

        if (result == null) {
            return null;
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jObj = jsonParser.parse(result).getAsJsonObject();
        jArray = jObj.getAsJsonArray("stationBeanList");

        insertStationsIntoDatabase(jArray);

        return jArray;
    }

    protected String makeRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private void insertStationsIntoDatabase(JsonArray jsonArray) {
        ArrayList<Station> stationList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            Station station = new Station(jsonArray.get(i).getAsJsonObject());
            stationList.add(station);
        }

        StationDataSource stationDataSource = new StationDataSource(BixeApplication.getAppContext());
        stationDataSource.open();
        stationDataSource.createStations(stationList);
        stationDataSource.close();
    }

    @Override
    protected void onCancelled() {
        listener.onJSONArrayCancelled();
    }

    protected void onPostExecute(JsonArray jArray) {
        if (jArray == null) {
            Log.e(TAG, "onPostExecute() -> No Data found from API? jArray is null");
        } else {
            Log.i(TAG, "onPostExecute() Received JSON -> " + jArray.toString());


            if (listener != null) {
                listener.onJSONArrayPostExecute(jArray);
            }
        }
    }
}