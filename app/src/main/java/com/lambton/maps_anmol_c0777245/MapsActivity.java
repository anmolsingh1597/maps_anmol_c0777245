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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.lambton.maps_anmol_c0777245.volley.GetByVolley;
import com.lambton.maps_anmol_c0777245.volley.VolleySingleton;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MapsActivity";
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 3000;
    private static final int RADIUS = 1500;

    private FusedLocationProviderClient mClient;

    private LatLng userLocation;
    Location localityLocation;


    private static final int POLYGON_SIDES = 4;
    Polyline line;
    Polygon shape;
    List<Marker> markers = new ArrayList<>();
    //location with location manager and listner
    LocationManager locationManager;
    LocationListener locationListener;
    private Marker homeMarker;
    private Marker destMarker;
    Double finalDistance = 0.0;
    int lastIndexForVolleyResponse = -1;
    private boolean alertCalledValue = false;

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
        mMap.setOnMarkerDragListener(MapsActivity.this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
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
            public void onMapClick(final LatLng latLng) {
                if (!alertCalledValue) {
                    alertCalledValue = alertCall(latLng);

                } else {
                    assignMarker(latLng);
                }

            }

        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.d(TAG, "onPolylineClick: " + line.getPoints());

            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                final List<LatLng> polygonPoints = polygon.getPoints();
                int endIndex;

                for (int i = 0; i < polygonPoints.size(); i++) {
                    if (i + 1 == polygonPoints.size()) {
                        endIndex = 0;
                    } else {
                        endIndex = i + 1;
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getDirectionUrl(polygonPoints.get(i), polygonPoints.get(endIndex)), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String value = GetByVolley.getDirection(response, mMap).replace(" km", "");
                            value = value.replace("1 m", "0");
                            finalDistance += Double.parseDouble(value);
                            lastIndexForVolleyResponse +=1;
                            if(lastIndexForVolleyResponse == polygonPoints.size()-1){

                                distanceAlert();
                                lastIndexForVolleyResponse = -1;
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);




                }


            }
        });
    }

    private void distanceAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Total Distance: " + finalDistance.floatValue() +" km")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finalDistance = 0.0;
                    }
                }).show();
    }


    private boolean alertCall(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Mark 4 Cities of Canada")
                .setPositiveButton("Manually", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        assignMarker(latLng);
                    }
                })
                .setNegativeButton("Automatically", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        assignMarkers();
                    }
                }).show();

        return true;
    }

    private void assignMarkers() {
        HashMap<String, String> localityHashMap = new HashMap<>();
        localityHashMap = localityValue(new LatLng(43.7315, -79.7624));
        MarkerOptions bramptonOptions = new MarkerOptions().position(new LatLng(43.7315, -79.7624))
                .title(localityHashMap.get("thoroughfare") + ", " + localityHashMap.get("subThoroughfare") + ", " + localityHashMap.get("postalCode"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet(localityHashMap.get("locality") + ", " + localityHashMap.get("adminArea"))
                .draggable(true);
        localityHashMap.clear();

        localityHashMap = localityValue(new LatLng(43.8563, -79.5085));
        MarkerOptions vaughanOptions = new MarkerOptions().position(new LatLng(43.8563, -79.5085))
                .title(localityHashMap.get("thoroughfare") + ", " + localityHashMap.get("subThoroughfare") + ", " + localityHashMap.get("postalCode"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet(localityHashMap.get("locality") + ", " + localityHashMap.get("adminArea"))
                .draggable(true);
        localityHashMap.clear();

        localityHashMap = localityValue(new LatLng(43.6532, -79.3832));
        MarkerOptions torontoOptions = new MarkerOptions().position(new LatLng(43.6532, -79.3832))
                .title(localityHashMap.get("thoroughfare") + ", " + localityHashMap.get("subThoroughfare") + ", " + localityHashMap.get("postalCode"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet(localityHashMap.get("locality") + ", " + localityHashMap.get("adminArea"))
                .draggable(true);
        localityHashMap.clear();

        localityHashMap = localityValue(new LatLng(43.3255, -79.7990));
        MarkerOptions burlingtonOptions = new MarkerOptions().position(new LatLng(43.3255, -79.7990))
                .title(localityHashMap.get("thoroughfare") + ", " + localityHashMap.get("subThoroughfare") + ", " + localityHashMap.get("postalCode"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet(localityHashMap.get("locality") + ", " + localityHashMap.get("adminArea"))
                .draggable(true);
        localityHashMap.clear();

        if (markers.size() == POLYGON_SIDES) {
            clearMap();
        }
        markers.add(mMap.addMarker(bramptonOptions));
        markers.add(mMap.addMarker(vaughanOptions));
        markers.add(mMap.addMarker(torontoOptions));
        markers.add(mMap.addMarker(burlingtonOptions));


        if (markers.size() == POLYGON_SIDES) {
            drawShape();
        }
    }

    private void assignMarker(LatLng latLng) {

        HashMap<String, String> localityHashMap = new HashMap<>();
        localityHashMap = localityValue(latLng);
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title(localityHashMap.get("thoroughfare") + ", " + localityHashMap.get("subThoroughfare") + ", " + localityHashMap.get("postalCode"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet(localityHashMap.get("locality") + ", " + localityHashMap.get("adminArea"))
                .draggable(true);
        localityHashMap.clear();

        if (markers.size() == POLYGON_SIDES) {
            clearMap();
        }

        markers.add(mMap.addMarker(options));

        if (markers.size() == POLYGON_SIDES) {
            drawShape();
        }
    }

    private void drawShape() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x35377822)
                .strokeColor(Color.RED)
                .strokeWidth(5)
                .clickable(true);
        PolylineOptions options1 = new PolylineOptions()
                .color(Color.RED)
                .width(5)
                .clickable(true);

        for (int i = 0; i < POLYGON_SIDES; i++) {
            options.add(markers.get(i).getPosition());
            options1.add(markers.get(i).getPosition());
        }
        shape = mMap.addPolygon(options);
        line = mMap.addPolyline(options1);
    }


    private void clearMap() {
        for (Marker marker : markers) {
            marker.remove();
        }

        markers.clear();
        shape.remove();
        line.remove();
        shape = null;
        line = null;

    }

    private HashMap<String, String> localityValue(LatLng latLng) {

        HashMap<String, String> localityHashMap = new HashMap<>();

        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                String address = "";
                if (addresses.get(0).getAdminArea() != null) {
                    address += addresses.get(0).getAdminArea() + " ";
                    localityHashMap.put("adminArea", addresses.get(0).getAdminArea());
                }
                if (addresses.get(0).getLocality() != null) {
                    address += addresses.get(0).getLocality() + " ";
                    localityHashMap.put("locality", addresses.get(0).getLocality());
                }
                if (addresses.get(0).getPostalCode() != null) {
                    address += addresses.get(0).getPostalCode() + " ";
                    localityHashMap.put("postalCode", addresses.get(0).getPostalCode());
                }
                if (addresses.get(0).getThoroughfare() != null) {
                    address += addresses.get(0).getThoroughfare() + " ";
                    localityHashMap.put("thoroughfare", addresses.get(0).getThoroughfare());
                }
                if (addresses.get(0).getSubThoroughfare() != null) {
                    address += addresses.get(0).getSubThoroughfare() + " ";
                    localityHashMap.put("subThoroughfare", addresses.get(0).getSubThoroughfare());
                }
                if (addresses.get(0).getPremises() != null) {
                    address += addresses.get(0).getPremises() + " ";
                    localityHashMap.put("premises", addresses.get(0).getPremises());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localityHashMap;
    }

    private String getDirectionUrl(LatLng latLng1, LatLng latLng2) {
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin=" + latLng1.latitude + "," + latLng1.longitude);
        googleDirectionUrl.append("&destination=" + latLng2.latitude + "," + latLng2.longitude);
        googleDirectionUrl.append("&key=" + getString(R.string.google_maps_key));
        Log.d(TAG, "getdirectionalURL: " + googleDirectionUrl.toString());
        return googleDirectionUrl.toString();
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
        userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        HashMap<String, String> localityValueHashMap = new HashMap<>();
        localityValueHashMap = localityValue(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
        marker.setTitle(localityValueHashMap.get("thoroughfare") + ", " + localityValueHashMap.get("subThoroughfare") + ", " + localityValueHashMap.get("postalCode"));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        marker.setSnippet(localityValueHashMap.get("locality") + ", " + localityValueHashMap.get("adminArea"));


    }


}