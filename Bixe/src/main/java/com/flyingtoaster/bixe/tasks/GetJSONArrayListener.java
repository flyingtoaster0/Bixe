package com.flyingtoaster.bixe.tasks;

import com.google.gson.JsonArray;

/**
 * Created by tim on 6/7/14.
 */
public interface GetJSONArrayListener {
    public void onJSONArrayPreExecute();
    public void onJSONArrayProgressUpdate(String... params);
    public void onJSONArrayPostExecute(JsonArray jArray);
    public void onJSONArrayCancelled();
    public void onJSONArrayFailed();

}
