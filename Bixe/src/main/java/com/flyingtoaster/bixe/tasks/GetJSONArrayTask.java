package com.flyingtoaster.bixe.tasks;

import java.io.IOException;
import android.util.Log;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
public class GetJSONArrayTask extends AsyncTask<Void, Void, JsonArray> {

    public static final String TAG = "GetJsonArrayTask";
    private GetJSONArrayListener listener;

    private String mUrl;
    private JsonArray jArray;
    private OkHttpClient client = new OkHttpClient();

    public GetJSONArrayTask(GetJSONArrayListener listener, String url) {
        this.listener = listener;
        this.mUrl = url;
    }

    String makeRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    protected JsonArray doInBackground(Void... params) {
        if (mUrl == null) return null;
        String result;

        try {
            result = makeRequest(mUrl);
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

        return jArray;
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
            listener.onJSONArrayPostExecute(jArray);
        }
    }
}