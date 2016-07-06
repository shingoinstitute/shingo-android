package org.shingo.shingoapp.data;

/**
 * This interface is used as callback for
 * {@link android.os.AsyncTask}.
 *
 * @author Dustin Homan
 */
public interface OnTaskComplete {
    /**
     * Callback used when nothing is expected back
     */
    void onTaskComplete();

    /**
     * Callback used when a JSON string is expected back
     * @param response A JSON string to parse
     */
    void onTaskComplete(String response);

    void onTaskError(String error);
}