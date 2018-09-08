package bd.ac.pstu.rezwan12cse.bounduley12;

import android.*;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarException;

import bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase.MapView;

public class MapsActivity2 extends FragmentActivity implements GeoQueryEventListener, GoogleMap.OnCameraChangeListener, OnMapReadyCallback {

        private static final GeoLocation INITIAL_CENTER = new GeoLocation(37.7789, -122.4017);
        private static final int INITIAL_ZOOM_LEVEL = 14;
        private static final String GEO_FIRE_DB = "https://bounduley12.firebaseio.com";
        private static final String GEO_FIRE_REF = GEO_FIRE_DB + "/_geofire";

        private GoogleMap map;
        private Circle searchCircle;
        private GeoFire geoFire;
        private GeoQuery geoQuery;
        FirebaseApp finestayApp;

        private Map<String, Marker> markers;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_maps2);

                // setup map and camera position
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);


                //FirebaseOptions options = new FirebaseOptions.Builder().setApplicationId("geofire").setDatabaseUrl(GEO_FIRE_DB).build();
                //FirebaseApp app = FirebaseApp.initializeApp(this, options);

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyDhAZ8EgIPJbvvb3p8vVlhylJ3j9odgLsk")
                        .setApplicationId("1:880234463224:android:6857ed63d38588c7")
                        .setDatabaseUrl("https://bounduley12.firebaseio.com")
                        .build();

                boolean hasBeenInitialized = false;
                List<FirebaseApp> firebaseApps = FirebaseApp.getApps(this);
                for (FirebaseApp app : firebaseApps) {
                        if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                                hasBeenInitialized = true;
                                finestayApp = app;
                        }
                }

                if (!hasBeenInitialized) {
                        finestayApp = FirebaseApp.initializeApp(this, options);
                }

                // setup GeoFire
                this.geoFire = new GeoFire(FirebaseDatabase.getInstance(finestayApp).getReferenceFromUrl(GEO_FIRE_DB));
                // radius in km
                this.geoQuery = this.geoFire.queryAtLocation(INITIAL_CENTER, 1);

                // setup markers
                this.markers = new HashMap<String, Marker>();
        }

        @Override
        protected void onStop() {
                super.onStop();
                // remove all event listeners to stop updating in the background
                this.geoQuery.removeAllListeners();
                for (Marker marker : this.markers.values()) {
                        marker.remove();
                }
                this.markers.clear();
        }

        @Override
        protected void onStart() {
                super.onStart();
                // add an event listener to start updating locations again
                this.geoQuery.addGeoQueryEventListener(this);
        }

        @Override
        public void onKeyEntered(String key, GeoLocation location) {
                // Add a new marker to the map
                Marker marker = this.map.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                this.markers.put(key, marker);
        }

        @Override
        public void onKeyExited(String key) {
                // Remove any old marker
                Marker marker = this.markers.get(key);
                if (marker != null) {
                        marker.remove();
                        this.markers.remove(key);
                }
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {
                // Move the marker
                Marker marker = this.markers.get(key);
                if (marker != null) {
                        this.animateMarkerTo(marker, location.latitude, location.longitude);
                }
        }

        @Override
        public void onGeoQueryReady() {
        }

        @Override
        public void onGeoQueryError(DatabaseError error) {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        }

        // Animation handler for old APIs without animation support
        private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
                final Handler handler = new Handler();
                final long start = SystemClock.uptimeMillis();
                final long DURATION_MS = 3000;
                final Interpolator interpolator = new AccelerateDecelerateInterpolator();
                final LatLng startPosition = marker.getPosition();
                handler.post(new Runnable() {
                        @Override
                        public void run() {
                                float elapsed = SystemClock.uptimeMillis() - start;
                                float t = elapsed / DURATION_MS;
                                float v = interpolator.getInterpolation(t);

                                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                                marker.setPosition(new LatLng(currentLat, currentLng));

                                // if animation is not finished yet, repeat
                                if (t < 1) {
                                        handler.postDelayed(this, 16);
                                }
                        }
                });
        }

        private double zoomLevelToRadius(double zoomLevel) {
                // Approximation to fit circle into view
                return 16384000 / Math.pow(2, zoomLevel);
        }

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
                // Update the search criteria for this geoQuery and the circle on the map
                LatLng center = cameraPosition.target;
                double radius = zoomLevelToRadius(cameraPosition.zoom);
                this.searchCircle.setCenter(center);
                this.searchCircle.setRadius(radius);
                this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
                // radius in km
                this.geoQuery.setRadius(radius / 1000);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                //googleMap.setOnMarkerClickListener(this);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                }
                map.setMyLocationEnabled(true);


            LatLng latLngCenter = new LatLng(INITIAL_CENTER.latitude, INITIAL_CENTER.longitude);
            this.searchCircle = this.map.addCircle(new CircleOptions().center(latLngCenter).radius(1000));
            this.searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
            this.searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
            this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, INITIAL_ZOOM_LEVEL));
            this.map.setOnCameraChangeListener(this);
    }
}