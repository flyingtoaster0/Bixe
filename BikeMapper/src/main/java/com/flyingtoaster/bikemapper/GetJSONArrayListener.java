package com.flyingtoaster.bikemapper;

import org.json.JSONArray;

import java.util.Vector;

/**
 * Created by tim on 6/7/14.
 */
public interface GetJSONArrayListener {
    public void onJSONArrayPreExecute();
    public void onJSONArrayProgressUpdate(String... params);
    public void onJSONArrayPostExecute(JSONArray jArray);
    public void onJSONArrayCancelled();

}
