package com.flyingtoaster.bixe;

import org.json.JSONArray;

/**
 * Created by tim on 6/7/14.
 */
public interface GetJSONArrayListener {
    public void onJSONArrayPreExecute();
    public void onJSONArrayProgressUpdate(String... params);
    public void onJSONArrayPostExecute(JSONArray jArray);
    public void onJSONArrayCancelled();
    public void onJSONArrayFailed();

}
