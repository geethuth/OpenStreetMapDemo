package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private MapView map;
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 1000.0f; // in meters
    int i = 0;
    public static Double[] lat, lng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialise osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        setContentView(R.layout.activity_main);
        map = (MapView) findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);

        ScaleBarOverlay scala = new ScaleBarOverlay(map);
        map.getOverlays().add(scala);

        lat = new Double[]{8.529535,
                8.522359,
                8.518448,
                8.513492,
                8.512261,
                8.509205,
                8.502966,
                8.494774,
                8.488111,
                8.487049
        };
        lng = new Double[]{76.93843,
                76.940983,
                76.942267,
                76.946672,
                76.948344,
                76.94916,
                76.950747,
                76.948001,
                76.948001,
                76.952633
        };

        getLocations();


    }

    private void getLocations() {

        new CountDownTimer(10 * 3000, 3000) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onTick(long millisUntilFinished) {
                IMapController mapController = map.getController();

                mapController.setZoom(14);
                 mapController.setCenter(new GeoPoint(lat[i], lng[i]));
                Marker startMarker = new Marker(map);
                startMarker.setPosition(new GeoPoint(lat[i], lng[i]));
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(startMarker);
                //createGeofence(new LatLng(8.529535, 76.93843),GEOFENCE_RADIUS);
                map.invalidate();
                i++;
            }

            @Override
            public void onFinish() {

            }
        }.start();


    }

    // Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }


    public void onResume() {
        super.onResume();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}