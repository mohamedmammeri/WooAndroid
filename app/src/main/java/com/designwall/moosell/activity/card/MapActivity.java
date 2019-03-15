package com.designwall.moosell.activity.card;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.designwall.moosell.R;
import com.designwall.moosell.util.Helper;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity {

    private static final int TAG_CODE_PERMISSION_LOCATION = 10;
    @BindView(R.id.mapView)
    MapView mapView;
    private double longitude;
    private double latitude;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Test", "State changed: " + provider + ", status: " + status);
        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Test", "Enabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Test","Disabled: " + provider);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);

        getCurrentLocation();
        longitude = getIntent().getDoubleExtra("longitude", longitude);
        latitude = getIntent().getDoubleExtra("latitude", latitude);

        // Move the Map
        IMapController mapController = mapView.getController();
        mapController.setZoom(13.0);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);

        MapEventsReceiver mReceive = new MapEventsReceiver(){
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
//                Log.d("Test", p.getLatitude() + " - "+p.getLongitude());
                Intent geoIntent = new Intent();
                geoIntent.putExtra("lat", p.getLatitude());
                geoIntent.putExtra("lon", p.getLongitude());
                geoIntent.putExtra("alt", p.getAltitude());
                setResult(RESULT_OK, geoIntent);
                finish();
                return true;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        mapView.getOverlays().add(new MapEventsOverlay(mReceive));
    }

    private void getCurrentLocation() {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(this, "Looks like we do not have permission to access GPS location.", Toast.LENGTH_SHORT).show();
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Helper.showDialog(this, "Request Permission", "GPS Location permission required.");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION },
                        TAG_CODE_PERMISSION_LOCATION);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TAG_CODE_PERMISSION_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    try {
                        LocationManager lm = (LocationManager)getSystemService(this.LOCATION_SERVICE);
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                        if (latitude != 0.0 && longitude != 0.0){
                            Log.d("Test", String.format("Current Location: %d,%d", latitude, longitude));
                        } else {
                            Log.e("Test",  "Location could not be found.");
                        }
                    } catch (SecurityException e) {
                        Log.e("Test", "Error permission: " + e.getMessage());
                    }
                } else {
                    // permission denied
                    Log.d("Test", "GPS Location permission denied.");
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
