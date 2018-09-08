package bd.ac.pstu.rezwan12cse.bounduley12;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 12195;
    private static final int PLAY_SERVICE_RESOLUTION_REQUEST = 10128;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private  Location mLastLocation;

    private static int UPDATE_INTERVAL = 3000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    Marker mCmarker;



    //end here



    String UserId;
    Marker marker;
    public DatabaseReference mUsers;
    public AlertDialog.Builder ex;
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    SupportMapFragment mapFragment;
    SweetAlertDialog sweetAlertDialog,sweetAlertDialogp;

    VerticalSeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
       // mAuth = FirebaseAuth.getInstance();


        sweetAlertDialog = new SweetAlertDialog(this,SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetAlertDialogp = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);

        if(isNetworkAvailable()){
            sweetAlertDialog.setTitleText(" Welcome Back")
                    .setContentText("Find your classmates and get connected for forever!")
                    .setCustomImage(R.drawable.ic_action_name)
                    .show();
        }else{
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("No internet connection!")
                    .show();
        }



        Switch ls_Switch = (Switch) findViewById(R.id.workingSwitch);
        ls_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setupLocation();
                }else{
                    disconnect();
                    mMap.clear();
                }
            }
        });







        ex = new AlertDialog.Builder(this).setTitle("Exit ?").setMessage("Do You Want To Exit")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }


                });


        mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //setupLocation();

        //String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsers = FirebaseDatabase.getInstance().getReference().child("profiles");
        mUsers.push().setValue(marker);

        seekBar = (VerticalSeekBar)findViewById(R.id.verticalSeekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(i), 2000, null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSION_REQUEST_CODE);
        }else {
            if (checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();

            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mMap.clear();
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("AvailableFriends");
            GeoFire geoFire1 = new GeoFire(ref1);
            geoFire1.setLocation(uid, new GeoLocation(latitude, longitude),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            //Add marker
                            LatLng latLng = new LatLng(latitude,longitude);
                            mCmarker = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("Your location : "+ String.valueOf(latLng))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10.0f));

                            //update children latitude longitude
                            UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("profiles").child(UserId);

                            Map userInfo = new HashMap();
                            userInfo.put("latitude",latitude);
                            userInfo.put("longitude",longitude);
                            userInfo.put("timestamp", getCurrentTimestamp());
                            mref.updateChildren(userInfo);

                            showUsers();
                        }
                    });

            Log.d("REZWAN", String.format("Your location was changed: %f / %f",latitude,longitude));
        }else {
            Log.d("REZWAN","Can't get your location");
        }
    }


    private String getCurrentTimestamp() {
        //Long timestamp = System.currentTimeMillis()/1000;
        //return timestamp;

        String mytime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
        String mydate =java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        return mytime;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_RESOLUTION_REQUEST).show();
            else {
                Toast.makeText(this,"This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;

        }
        return true;
    }


    private void connect() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void disconnect() {
        mGoogleApiClient.disconnect();
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String currentUserId = firebaseUser.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("profiles").child(currentUserId);
            Map userInfo = new HashMap();
            userInfo.put("latitude",null);
            userInfo.put("longitude",null);
            userInfo.put("CurrentStatus", "offline");
            // userInfo.put("timestamp", getCurrentTimestamp());
            myRef.updateChildren(userInfo);

            //zoom out to zoom level 1.0, animating with a duration of 1
            mMap.animateCamera(CameraUpdateFactory.zoomOut(),1000,null);
        }
    }


    @Override
    public void onMapReady (GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    private void sendNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.bounduley)
                .setContentTitle(title)
                .setContentText(content);
        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 ,intent,PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;

        manager.notify(new Random().nextInt(),notification);

    }

    private void showUsers() {
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    final Map<String, Object> map = (Map<String, Object>) s.getValue();
                    if (map.get("CphotoUrl") != null) {
                        String mProfileImageUrl = map.get("CphotoUrl").toString();
                        Glide.with(getApplication())
                                .load(mProfileImageUrl)
                                .asBitmap()
                                .dontTransform()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        if (map.get("latitude") != null  && map.get("longitude") != null) {
                                            String latitude = map.get("latitude").toString();
                                            String longitude = map.get("longitude").toString();
                                            LatLng location = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));

                                            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                                            int pixels = (int) (50 * scale + 0.5f);
                                            Bitmap bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true);

                                            mMap.addMarker(new MarkerOptions()
                                                    .position(location)
                                                    .title(map.get("Ausername").toString())
                                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))));

                                        }
                                    }

                                    @Override
                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                        if (map.get("latitude") != null  && map.get("longitude") != null) {
                                            String latitude = map.get("latitude").toString();
                                            String longitude = map.get("longitude").toString();
                                            LatLng location = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(location)
                                                    .title(map.get("Ausername").toString())
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        }
                                    }
                                });
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {

        mMarkerImageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //ex.show();
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Exit!")
                .setContentText("Are you sure?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        finish();
                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }


    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String currentUserId = firebaseUser.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("profiles").child(currentUserId);
            Map userInfo = new HashMap();
            userInfo.put("CurrentStatus", "active");
            myRef.updateChildren(userInfo);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case MY_PERMISSION_REQUEST_CODE:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;

            }
        }
    }





    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
