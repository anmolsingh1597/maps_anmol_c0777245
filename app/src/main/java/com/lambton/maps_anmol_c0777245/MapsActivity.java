package com.lambton.maps_anmol_c0777245;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MapsActivity";
//    Fused location provider client
    private FusedLocationProviderClient mClient;
//    private LocationRequest locationRequest;
//    private LocationCallback locationCallback;

    private static final int POLYGON_SIDES = 4;
    Polyline line;
    Polygon shape;
    List<Marker> markers = new ArrayList<>();
    //location with location manager and listner
    LocationManager locationManager;
    LocationListener locationListener;
    private Marker homeMarker;
    private Marker destMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mClient = LocationServices.getFusedLocationProviderClient(this);

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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location) {
                setHomeMarker(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (!hasLocationPermission()) {
            requestLocationPermission();
        } else {
            startUpdateLocations();
        }

        // apply tap gesture
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                assignMarkers();
            }

            private void assignMarkers() {
                MarkerOptions bramptonOptions = new MarkerOptions().position(new LatLng(43.7315,-79.7624))
                        .title("Brampton")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .snippet("Ontario");
                MarkerOptions vaughanOptions = new MarkerOptions().position(new LatLng(43.8563,-79.5085))
                        .title("Vaughan")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .snippet("Ontario");
                MarkerOptions torontoOptions = new MarkerOptions().position(new LatLng(43.6532,-79.3832))
                        .title("Toronto")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .snippet("Ontario");
                MarkerOptions burlingtonOptions = new MarkerOptions().position(new LatLng(43.3255,-79.7990))
                        .title("Burlington")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .snippet("Ontario");
                if(markers.size() == POLYGON_SIDES){ clearMap(); }
                markers.add(mMap.addMarker(bramptonOptions));
                markers.add(mMap.addMarker(vaughanOptions));
                markers.add(mMap.addMarker(torontoOptions));
                markers.add(mMap.addMarker(burlingtonOptions));

                if(markers.size() ==  POLYGON_SIDES){ drawShape(); }

            }
            private void drawShape() {
                PolygonOptions options = new PolygonOptions()
                        .fillColor(0x35377822)
                        .strokeColor(Color.RED)
                        .strokeWidth(5);

                for(int i = 0; i<POLYGON_SIDES; i++){
                    options.add(markers.get(i).getPosition());
                }
                shape = mMap.addPolygon(options);
            }

            private void clearMap() {
              /*  if(destMarker!=null){
                    destMarker.remove();
                    destMarker = null;
                }
                line.remove();*/
                for(Marker marker: markers){
                    marker.remove();
                }

                markers.clear();
                shape.remove();
                shape = null;

            }
        });
    }

    //MARK: start update location
    private void startUpdateLocations() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setHomeMarker(lastKnownLocation);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, (float) 9.3));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);
            }
        }
    }
}