package com.flyingtoaster.bixe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONArray;
import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import android.util.Log;
import android.os.AsyncTask;

/**
 * The first thing you'll notice is the stuff between the angle brackets. These are:
 * <Param type for doInBackground(),
 * Param type for onProgressUpdate() (We don't use this),
 * Return type of doInBackground() and argument type for onPostExecute()>
 * <br/>
 * When a new AsyncTask is executed, doInBackground() gets called with any params provided
 * (the params array is variable length). After a value is returned from doInBackground(),
 * onPostExecute() is called, passing in whatever value was returned. Note that the type of
 * argument in onPostExecute() must match the return type of doInBackground(); in this case,
 * it's a JSONArray.
 * <br/>
 * The OnTaskCompleted is on Object that implements a method called onTaskCompleted(). This
 * interface is used to facilitate a simple observer pattern. This allows us to pass objects
 * back into whatever created this AsyncTask. In this case, we pass the JSONArray we get
 * from the API call back into another object (most likely an Activity or Fragment).
 * <br/>
 * A NameValuePair is essentially just a group of two strings - one is a key, the other, a value.
 */
class GetJSONArrayTask extends AsyncTask<Void, Void, JSONArray> {

    public static final String TAG = "GetJsonArrayTask";
    //listener is the object that created this AsyncTask. It implements a callback function.
    private GetJSONArrayListener listener;

    //mUrl refers to the URL, excluding the address and the port. "/api/book", for example.
    String mUrl;
    JSONArray jArray;
    private static final String SERVER_ADDRESS = "http://107.170.7.58:4567";

    /**
     * Constructor
     * @param listener
     * @param url
     */
    public GetJSONArrayTask(GetJSONArrayListener listener, String url)
    {
        this.listener = listener;
        this.mUrl = url;
    }

    /**
     * This method actually executes the AsyncTask.
     * POST parameters are passed in as NameValuePairs.
     * @param params
     * @return A JSONArray as a response from the server.
     */
    protected JSONArray doInBackground(Void... params)
    {
        if (mUrl == null) return null;

        String result=new String();

        try
        {
            /*
            Note that the URI we use consists of a final String concatenated with the mUrl that
            is passed in through the constructor
            */
            URI uri = new URI(mUrl);

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(uri);

            //Execute the request and get a response
            Log.i(TAG, "doInBackground() -> " + "Sending out request: " + httpGet.toString());
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if(entity != null)
            {
                //Try to read the result from the HTTP request into a String.
                //Inside this try/catch, we just build a string from the result of our API call
                try
                {
                    InputStream is = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                }
                catch (Exception e) //Couldn't grab the string from HttpEntity for some reason
                {
                    listener.onJSONArrayFailed();
                    Log.e("Exceptions", "Error converting result -> " + e.toString());
                }

                // try create a JSONArray from the string we just got
                try
                {
                    JSONObject jObj = new JSONObject(result);
                    jArray = jObj.getJSONArray("stationBeanList");
                    return jArray;

                }
                catch (JSONException e) //malformed JSON, not JSON, possibly a null/empty String, etc
                {
                    Log.d("Debug","Error parsing JSON");
                    Log.e("Exceptions", e.toString());
                    listener.onJSONArrayFailed();
                }
            }
            else //The HttpEntity was null
            {
                listener.onJSONArrayFailed();
                return null;
            }
        }
        catch(Exception e) //URL can't be reached, bad params, etc
        {
            Log.d("Debug","Getting URL failed!");
            Log.e("Exceptions",e.toString());
            e.printStackTrace();
            listener.onJSONArrayFailed();
            return null;
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        listener.onJSONArrayCancelled();
    }

    //Callback for the activity/fragment.
    //Pass the JSONArray back to whatever created this AsyncTask.

    /**
     * This method executes a callback to the listener that originally created this AsyncTask.
     * The callback provides the listener with the JSONArray that was received from the POST request.
     * @param jArray
     */
    protected void onPostExecute(JSONArray jArray)
    {
        if(jArray == null)
        {
            Log.e(TAG, "onPostExecute() -> No Data found from API? jArray is null");
        }
        else
        {
            Log.i(TAG, "onPostExecute() Received JSON -> " + jArray.toString());
            listener.onJSONArrayPostExecute(jArray);
        }
    }
}