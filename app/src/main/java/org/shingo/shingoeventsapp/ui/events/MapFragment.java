package org.shingo.shingoeventsapp.ui.events;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.ui.TouchImageView;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    private static final String ARG_URL = "url";
    private static final String ARG_PIN = "pin";

    private String mUrl;
    private double[] mPin;

    private Bitmap map;
    private ProgressBar progress;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url URL to map image
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(String url) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url URL to map image
     * @param pin XY coord to drop pin
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(String url, double[] pin) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putDoubleArray(ARG_PIN, pin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_URL);
            if(getArguments().containsKey(ARG_PIN))
                mPin = getArguments().getDoubleArray(ARG_PIN);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        map.recycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        progress = (ProgressBar) view.findViewById(R.id.map_progress);
        if(map == null){
            DownloadImageTask downloadImageTask = new DownloadImageTask(((TouchImageView)view.findViewById(R.id.map)));
            downloadImageTask.execute(mUrl);
        } else {
            progress.setVisibility(View.GONE);
            ((TouchImageView)view.findViewById(R.id.map)).setImageBitmap(map);
        }

        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        TouchImageView view;

        public DownloadImageTask(TouchImageView view) {
            this.view = view;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            view.setImageBitmap(result);
            progress.setVisibility(View.GONE);
            map = result;
        }
    }

}
