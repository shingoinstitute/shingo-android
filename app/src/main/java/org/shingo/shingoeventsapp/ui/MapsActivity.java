package org.shingo.shingoeventsapp.ui;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.shingo.shingoeventsapp.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String ARG_PIN = "pin";
    public static final String ARG_TITLE = "title";
    private GoogleMap mMap;
    private double[] mPin;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(getIntent().getExtras().containsKey(ARG_PIN))
            mPin = getIntent().getExtras().getDoubleArray(ARG_PIN);
        if(getIntent().getExtras().containsKey(ARG_TITLE))
            mTitle = getIntent().getExtras().getString(ARG_TITLE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(mPin[0], mPin[1]);
        mMap.addMarker(new MarkerOptions().position(location).title(mTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
    }
}
