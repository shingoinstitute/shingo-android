package org.shingo.shingoeventsapp.data;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * This class is used to make an asynchronous call to
 * the API to fetch data.
 * Extends {@link AsyncTask}
 *
 * @author Dustin Homan
 */
public class GetAsyncData extends AsyncTask<String, Void, Boolean> {

    private static final Object MUTEX = new Object();
    private static final String API_URL = "https://api.shingo.org";
    private static final String LOG_TAG = "GetAsyncData";
    private static boolean isWorking = false;
    private OnTaskCompleteListener mListener;
    private String output;
    private Exception exception;

    /**
     * Constructor
     * @param listener the callback to call when task is complete
     */
    public GetAsyncData(OnTaskCompleteListener listener) {
        mListener = listener;
        Log.d(LOG_TAG, "GetAsyncData created for " + listener.getClass().getName());
    }

    /**
     * This method makes the API call
     *
     * @param params a list of parameters. params[0] = api path. params[1] = query. params[2] = data for body
     * @return the success of the task
     */
    @Override
    protected Boolean doInBackground(String... params) {
        Log.d(LOG_TAG, "doInBackground(" + Arrays.toString(params) + ")");
        synchronized (MUTEX) {
            if (isWorking) {
                try {
                    MUTEX.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isWorking = true;
            }
        }

        try {
            URLConnection conn = new URL( API_URL + params[0] + (params.length > 1 ? "?" + params[1] : "") ).openConnection();
            if(params.length > 2){
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(params[2]);
                wr.flush();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            output = sb.toString();
        } catch (UnsupportedEncodingException e) {
            exception = e;
            return false;
        } catch (IOException e) {
            exception = e;
            return false;
        }

        return true;
    }

    /**
     * This method is called when {@link GetAsyncData#doInBackground(String...)} is finished.
     * Calls {@link OnTaskCompleteListener#onTaskComplete(String)} if API call was successful and
     * calls {@link OnTaskCompleteListener#onTaskError(String)} if there was an error.
     *
     * @param success the return value of {@link GetAsyncData#doInBackground(String...)}
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        synchronized (MUTEX) {
            isWorking = false;
            MUTEX.notifyAll();
        }
        if (success) {
            Log.d(LOG_TAG, "Output " + output);
            try {
                JSONObject jsonObject = new JSONObject(output);
                if(jsonObject.getBoolean("success")) {
                    mListener.onTaskComplete(output);
                } else {
                    Log.d(LOG_TAG, jsonObject.get("error").toString());
                    mListener.onTaskError("Error getting data! Please let us know!\n\nshingo.development@usu.edu");
                }
            } catch (JSONException e) {
                mListener.onTaskError("Error getting data! Please let us know!\n\nshingo.development@usu.edu");
            }
        } else {
            Log.d(LOG_TAG, exception.getMessage(), exception.getCause());
            mListener.onTaskError("Error getting data! Please let us know!\n\nshingo.development@usu.edu");
        }
    }
}
