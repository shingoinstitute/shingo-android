package org.shingo.shingoeventsapp.ui.events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        if(map != null)
            map.recycle();
    }

    private Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {

        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth(), sourceBitmap.getHeight());
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);

        return resultBitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Venue Map");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        progress = (ProgressBar) view.findViewById(R.id.map_progress);
        if(map == null){
            progress.setVisibility(View.VISIBLE);
            DownloadImageTask downloadImageTask = new DownloadImageTask(((TouchImageView)view.findViewById(R.id.map)));
            downloadImageTask.execute(mUrl);
        } else {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            Bitmap pinBitmap = changeBitmapColor(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_on, opt),40,40,false), getResources().getColor(R.color.colorPrimaryDark));

            TouchImageView touchImageView = (TouchImageView) view.findViewById(R.id.map);
            if(mPin != null){
                Bitmap copy = map.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(copy);
                canvas.drawBitmap(pinBitmap, (float)mPin[0], (float)mPin[1], null);
                touchImageView.setImageBitmap(copy);
            } else {
                touchImageView.setImageBitmap(map);
            }
            touchImageView.resetZoom();

            progress.setVisibility(View.GONE);
        }

        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        TouchImageView view;
        Bitmap marker;

        public DownloadImageTask(TouchImageView view) {
            this.view = view;
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            marker = changeBitmapColor(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_on, opt),40,40,false), getResources().getColor(R.color.colorPrimaryDark));
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
            if(mPin != null && result != null){
                Bitmap copy = result.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(copy);
                canvas.drawBitmap(marker, (float)mPin[0] - marker.getHeight() / 2, (float)mPin[1] - marker.getWidth() / 2, null);
                view.setImageBitmap(copy);
            } else {
                view.setImageDrawable(new BitmapDrawable(getResources(), result));
            }
            view.resetZoom();
            progress.setVisibility(View.GONE);
            map = result;
        }
    }

}
