package bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bd.ac.pstu.rezwan12cse.bounduley12.Launcher_Activity;
import bd.ac.pstu.rezwan12cse.bounduley12.Model.FireModel;
import bd.ac.pstu.rezwan12cse.bounduley12.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MapView extends FragmentActivity implements OnMapReadyCallback,
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
    DatabaseReference ref;
    GeoFire geoFire;

    private static int UPDATE_INTERVAL = 3000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    Marker mCmarker;



    //end here




    GeoQuery geoQuery;
    private Map<String, Marker> markers;
    private static final GeoLocation INITIAL_CENTER = new GeoLocation(22, 45);



    String UserId;
    TextView UName,UEmail,UMobile;
    private ChildEventListener mchildEventListener;
    Marker marker;
    Button mFriend,mUpfateProfile, refresh;
    CircleImageView mImg;
    private static final String TAG = "UserProfile";
    private FirebaseUser muser;
    private DatabaseReference mDatabase;
    public DatabaseReference mUsers;
    private FirebaseAuth mAuth;
    //private DatabaseReference mUserReference;
    private ValueEventListener mMessageListener;

    public AlertDialog.Builder ex;
    private AnimatedCircleLoadingView animatedCircleLoadingView;
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    SupportMapFragment mapFragment;
    SweetAlertDialog sweetAlertDialog,sweetAlertDialogp;

    VerticalSeekBar seekBar;
    private  BottomSheetBehavior mbottomSheetBehavior;

    private LinearLayout userinfo;
    private Button close;
    EditText lat,longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        mAuth = FirebaseAuth.getInstance();

        close = (Button)findViewById(R.id.close_btn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userinfo.setVisibility(View.INVISIBLE);
            }
        });
        userinfo = (LinearLayout)findViewById(R.id.userinfo);

        UName = (TextView) findViewById(R.id.t_name);
        UEmail = (TextView) findViewById(R.id.t_email);
        UMobile = (TextView) findViewById(R.id.t_mob);
        mImg = (CircleImageView) findViewById(R.id.t_image);
        lat = findViewById(R.id.lat);
        longi = findViewById(R.id.longi);

        sweetAlertDialog = new SweetAlertDialog(this,SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetAlertDialogp = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);

                 if(isNetworkAvailable()){
                            /*
                            //sweetAlertDialogp.setTitleText("").setCustomImage(R.drawable.hhlogo).show();
                            // sweetAlertDialogp.hide();
                             SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                             pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                             pDialog.setTitleText("Loading");
                             pDialog.setCancelable(false);
                             pDialog.show();
                            */

                            sweetAlertDialog.setTitleText(" Welcome Back")
                                    .setContentText("Find your classmates and get connected for forever!")
                                    .setCustomImage(R.drawable.userimage)
                                    .show();
                        }else{

                    // Toast.makeText(getApplicationContext(),"No Intrnet Available",Toast.LENGTH_SHORT).show();
                     new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                             .setTitleText("Oops...")
                             .setContentText("No internet connection!")
                             .show();
                 }



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

        setupLocation();


        mUsers = FirebaseDatabase.getInstance().getReference().child("AuthUsers");
        mUsers.push().setValue(marker);

/*
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CircularProgressBar cb = findViewById(R.id.pb);
                //cb.setProgress(30f);
                //cb.setVisibility(View.GONE);


            }
        });

        Switch ls_Switch = (Switch) findViewById(R.id.workingSwitch);
        ls_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                   // connect();
                }else{
                    //disconnect();
                }
            }
        });




/*
        DatabaseReference friendsLocation = FirebaseDatabase.getInstance().getReference("AvailableFriends");
        GeoFire geoFire = new GeoFire(friendsLocation);
        geoQuery = geoFire.queryAtLocation(INITIAL_CENTER , 2);


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

  */
    }

    private void setupLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapView.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSION_REQUEST_CODE);
        }else {
            if (checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();

            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("AuthUsers").child(UserId);

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

    private void getUserInfo(){
        userinfo.setVisibility(View.VISIBLE);
       close.setVisibility(View.VISIBLE);

        UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String id = FirebaseAuth.getInstance().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("AuthUsers").child(id);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("name") != null) {
                        UName.setText(map.get("name").toString());
                    }
                    if (map.get("mobile") != null) {
                        UMobile.setText(map.get("mobile").toString());
                    }
                    if (map.get("email") != null) {
                        UEmail.setText(map.get("email").toString());
                    }
                    if(map.get("profileImageUrl")!=null){
                        String mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mImg);
                    }
                    if (map.get("latitude") != null) {
                        lat.setText(map.get("latitude").toString());
                    }
                    if (map.get("longitude") != null) {
                        longi.setText(map.get("longitude").toString());
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void connect() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapView.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void disconnect() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AvailableFriends");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        mMap.clear();
    }


    @Override
        public void onMapReady (GoogleMap googleMap) {
            mMap = googleMap;
            googleMap.setOnMarkerClickListener(this);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6850, 90.3563), 6.0f));

            //showUsers();
/*
            //create dengerous area
            LatLng d_area = new LatLng(23.6850, 90.3563);
            mMap.addCircle(new CircleOptions()
                            .center(d_area)
                            .radius(500)
                            .strokeColor(Color.BLUE)
                            .fillColor(0x220000FF)
                            .strokeWidth(5.0f));  //0.5f = .5 km = 500 m

            //Geoquery here
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("AvailableFriends");
            GeoFire geoFire2 = new GeoFire(ref1);
            GeoQuery geoQuery = geoFire2.queryAtLocation(new GeoLocation(d_area.latitude, d_area.longitude), 0.5f);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    sendNotification("CSE12", String.format("%s is available now!", key));
                }

                @Override
                public void onKeyExited(String key) {
                    sendNotification("CSE12", String.format("%s is no longer available!", key));
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Log.d("MOVE", String.format("%s is moved within the dangerous area [%f/%f]", key, location.latitude ,location.longitude));
                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    Log.e("ERROR",""+ error);
                }
            });

*/
        }

    private void sendNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content);
        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MapView.class);
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
                    //final List<Object> map1 = (List<Object>) dataSnapshot.getValue();
                    //LatLng location = new LatLng(Double.valueOf(map.get("latitude").toString()), Double.valueOf(map.get("longitude").toString()));

                    if (map.get("profileImageUrl") != null) {
                        String mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication())
                                .load(mProfileImageUrl)
                                .asBitmap()
                                .dontTransform()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            /*
                                            double locationLat = 0;
                                            double locationLng = 0;
                                            if(map1.get(0) != null){
                                                locationLat = Double.parseDouble(map1.get(0).toString());
                                            }
                                            if(map1.get(1) != null){
                                                locationLng = Double.parseDouble(map1.get(1).toString());
                                            }
                                            LatLng location = new LatLng(locationLat, locationLng);
                                            */
                                        LatLng location = new LatLng(Double.valueOf(map.get("latitude").toString()), Double.valueOf(map.get("longitude").toString()));

                                        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                                        int pixels = (int) (50 * scale + 0.5f);
                                        Bitmap bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true);

                                        mMap.addMarker(new MarkerOptions()
                                                .position(location)
                                                .title(map.get("name").toString() +" :" + location)
                                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))));
/*
                                            try {
                                                List<LatLng> list = decodePoly(location);
                                                Polyline line = mMap.addPolyline(new PolylineOptions()
                                                        .addAll(list)
                                                        .width(12)
                                                        .color(Color.parseColor("#05b1fb"))
                                                        .geodesic(true));
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
*/
                                    }

                                    @Override
                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                        //super.onLoadFailed(e, errorDrawable);
                                            /*
                                            double locationLat = 0;
                                            double locationLng = 0;
                                            if(map1.get(0) != null){
                                                locationLat = Double.parseDouble(map1.get(0).toString());
                                            }
                                            if(map1.get(1) != null){
                                                locationLng = Double.parseDouble(map1.get(1).toString());
                                            }
                                            LatLng location = new LatLng(locationLat, locationLng);
                                            */
                                        LatLng location = new LatLng(Double.valueOf(map.get("latitude").toString()), Double.valueOf(map.get("longitude").toString()));
                                        mMap.addMarker(new MarkerOptions()
                                                .position(location)
                                                .title(map.get("name").toString())
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    }
                                });
                    }


                }
            }

                /*
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    User user= s.getValue(User.class);
                    LatLng location = new LatLng(Double.valueOf(user.latitude) ,Double.valueOf(user.longitude));
                    mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(user.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }
            */

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
/*
    private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<LatLng>();
            int index =0, len = encoded.length();
            int lat = 0, lng =0;
            while(index<len){
                int b, shift =0, result=0;

                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f)<< shift;
                    shift +=5;
                }while (b >= 0x20);
                int dlat = ((result & 1)!= 0 ? ~(result>>1) : (result >> 1));

                lat+= dlat;

                shift = 0;
                result =0;

                do {
                    b= encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift +=5;
                }while (b>= 0x20);
                int dlng = ((result & 1)!= 0 ? ~(result >> 1): (result >> 1));

                lng += dlng;

                LatLng p = new LatLng((((
                        double) lat / 1E5)),
                        (((double) lng  / 1E5)));

                poly.add(p);
            }
            return  poly;
    }
    */

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
/*
           // mMap.clear();
            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.6850,90.3563), 6.0f));
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


            //mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("AvailableFriends");
            //DatabaseReference mDB = FirebaseDatabase.getInstance().getReference().child("AuthUsers").child(UserId);

            GeoFire geoFireAvailable = new GeoFire(mDatabase);
            //GeoFire geoFire = new GeoFire(mDB);
            geoFireAvailable.setLocation(UserId, new GeoLocation(location.getLatitude(), location.getLongitude()));
            //geoFire.setLocation(UserId, new GeoLocation(location.getLatitude(), location.getLongitude()));


            //testShowUsers();
*/
    }
private void showBottomsheetDialog(){
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
    BottomSheetDialog dialog = new BottomSheetDialog(this);
    dialog.setContentView(view);
    dialog.show();
}

    private void showBottomsheetDialogFragment(){
       // View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
       BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
       bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public boolean onMarkerClick(Marker marker) {
        //showBottomsheetDialogFragment();
        //showBottomsheetDialog();
        getUserInfo();
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
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
        //getUserInfo();

        // add an event listener to start updating locations again
        //geoQuery.addGeoQueryEventListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //geoQuery.removeAllListeners();
/*
        String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("AvailableFriends");

        GeoFire geoFireAvailable = new GeoFire(mDatabase);
        geoFireAvailable.removeLocation(UserId);
*/

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
