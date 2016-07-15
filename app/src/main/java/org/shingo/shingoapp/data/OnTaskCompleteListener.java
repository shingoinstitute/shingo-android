package org.shingo.shingoapp.data;

/**
 * This interface is used as callback for
 * {@link android.os.AsyncTask}.
 *
 * @author Dustin Homan
 */
public interface OnTaskCompleteListener {

    /**
     * Callback used when a JSON string is expected back
     * @param response A JSON string to parse
     */
    void onTaskComplete(String response);

    /**
     * Callback used when an error happens during
     * the asynchronous task that results in no
     * response from the server.
     * @param error String to pass back to the user describing the error
     */
    void onTaskError(String error);
}