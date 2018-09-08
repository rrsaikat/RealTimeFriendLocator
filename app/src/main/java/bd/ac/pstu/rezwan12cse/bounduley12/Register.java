package bd.ac.pstu.rezwan12cse.bounduley12;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    EditText Name;
    EditText Email;
    EditText Mobile;
    EditText Password;
    EditText la;
    EditText lo;
    ImageButton imageButton;
    CircleImageView circleImageView;
    //private String la,lo;
    Button btnSignup;
    TextView login_here;
    private static String SIGNUP_URL = "http://pstu.a0001.net/FL/register.php";
    private static String IMG_URL = "http://pstu.a0001.net/FL/uploadimage.php";
    Constants1 constants;
    CheckBox tAndC;
    ProgressDialog pd;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LocationManager lm;
    private LocationListener locationListener;

    static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    boolean FINE_LOCATION_PERMISSION_GRANTED = false;

    private static final String TAG = Register.class.getSimpleName();
    Bitmap bitmap;
    private static final int  PERMISSION_REQUEST_CODE = 2;
    private static final int  GALLERY_REQUEST = 99;
    String selectedPhoto,ext,encodeImage;


    GalleryPhoto galleryPhoto;
    String photoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        getSupportActionBar().hide();
        Name = (EditText) findViewById(R.id.et_name);
        Email = (EditText) findViewById(R.id.et_email);
        Mobile = (EditText) findViewById(R.id.et_mobile);
        Password = (EditText) findViewById(R.id.et_password);
        imageButton = (ImageButton)findViewById(R.id.upImg);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);


        galleryPhoto = new GalleryPhoto(getApplicationContext());

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);

                /*
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),GALLERY_REQUEST);
                */
            }

        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    try {
                        if(photoPath == "" || photoPath == null){
                            Toast.makeText(getApplicationContext(),"Select Image ",Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(512, 512).getBitmap();
                            encodeImage = ImageBase64.encode(bitmap);

                            sendRequest();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }


        });

        if (Build.VERSION.SDK_INT >= 23){
            if (checkPermission()){

            }else {
                requestPermissions();
            }
        }



        la =  findViewById(R.id.lat);
        lo =  findViewById(R.id.longi);

        buildGoogleApiClient();
        displayLocation();


        login_here=(TextView)findViewById(R.id.tv_signup_loginhere);
        login_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });

        tAndC=(CheckBox)findViewById(R.id.terms_conditions);

        btnSignup=(Button)findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(Register.this);
                pd.setMessage("Please wait..."); // Setting Message
                pd.setTitle("Data insertion occurs in the server"); // Setting Title
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                pd.show(); // Display Progress Dialog
                pd.setCancelable(true);

                String userName = Name.getText().toString();
                String userEmail = Email.getText().toString();
                String userMobile = Mobile.getText().toString();
                String userPassword= Password.getText().toString();
                String userLatitude = la.getText().toString();
                String userLongitude = lo.getText().toString();





                // Pattern match for email id
                Pattern p = Pattern.compile(Constants1.regEx);
                Matcher m = p.matcher(userEmail);


                //    Pattern match for Mobile No
                Pattern mobi = Pattern.compile(Constants1.mobregEx);
                Matcher mob = mobi.matcher(userMobile);





                if(userName.equalsIgnoreCase("")||userName.length() ==3 ||  userEmail.equalsIgnoreCase("")||userEmail.length()==8||
                        userMobile.equalsIgnoreCase("")||userMobile.length()==11||userPassword.equalsIgnoreCase("")||userPassword.length()==5)
                {

                    if(userName.equalsIgnoreCase("")){
                        Name.setError("Please Enter Name ");
                        pd.dismiss();
                    }

                    else if(userEmail.equalsIgnoreCase("")||!m.find()){
                        Email.setError("Pleas Enter Valid Email ");
                        pd.dismiss();
                    }


                    else if(userMobile.equalsIgnoreCase("")||!mob.find()){
                        Mobile.setError("Please Enter Valid Mobile Number ");
                        pd.dismiss();
                    }

                    else if (userPassword.equalsIgnoreCase("")){
                        Password.setError("Please Enter Password ");
                        pd.dismiss();
                    }

                    else if(!tAndC.isChecked()){
                        Toast.makeText(getApplicationContext(),"Please Check Terms and Conditions", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }

                    else {
                        if(Constants1.isOnline(getApplicationContext())){

                            register(SIGNUP_URL, userName, userEmail,userMobile, userPassword , userLatitude , userLongitude);
                            //uploaduserImg();

                        } else {
                            Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Entries are Wrong",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }


            }
        });

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Register.this , Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Register.this , Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(Register.this , "Please allow this permission in the settings.", Toast.LENGTH_LONG).show();
        }else {
            ActivityCompat.requestPermissions(Register.this , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} , PERMISSION_REQUEST_CODE);
        }
    }




    private void register(String signupUrl, final String getuserName, final String getuserEmail, final String getuserMobile, final String getuserPassword , final String getuserLatitude , final String getuserLongitude) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest  =new StringRequest(Request.Method.POST, signupUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getInt("success")==0)
                    {

                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        pd.dismiss();

                    }
                    else if(jsonObject.getInt("success")==1){
                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        Intent intent=new Intent(Register.this, Login.class);
                        startActivity(intent);

                    }

                    else

                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pd.dismiss();


            }
        })
        {
            @Override
            protected Map<String , String> getParams() {
                Map<String , String> param = new HashMap<String, String>();

                param.put("name",getuserName);
                param.put("email", getuserEmail);
                param.put("mobile", getuserMobile);
                param.put("password", getuserPassword);
                param.put("latitude",getuserLatitude);
                param.put("longitude",getuserLongitude);
                return param;
            }
        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    private void displayLocation() {
        //if (FINE_LOCATION_PERMISSION_GRANTED) return;
        //if (Build.VERSION.SDK_INT < 23) return;
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if ( permissionCheck != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION);
        //if (FINE_LOCATION_PERMISSION_GRANTED)
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            la.setText(Double.toString(latitude));
            lo.setText(Double.toString(longitude));
            //updateServers(latitude, longitude);

        } else {
            la.setText("Couldn't get the location. Make sure location is enabled on the device");
            lo.setText("Couldn't get the location. Make sure location is enabled on the device");
        }
    }


    private void sendRequest(){
        //progressBar.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String response = null;

        final String finalResponse = response;

        StringRequest postRequest = new StringRequest(Request.Method.POST, IMG_URL, new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        //progressBar.hide();
                        circleImageView.setImageDrawable(null);
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), " Uploaded File to the Server ", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", finalResponse);
                        //progressBar.hide();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                //String images = getStringImage(bitmap);
                params.put("image", encodeImage);
               // params.put("map", location.getText().toString());
                params.put("ext", ext);
                //params.put("text", feedstatus.getText().toString());


                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);




    }


    public void uploaduserImg(){

    RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
    StringRequest stringRequest=new StringRequest(Request.Method.POST, IMG_URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("JSONResult" , response.toString());
            Toast.makeText(Register.this, ""+response, Toast.LENGTH_SHORT).show();
            try {
                JSONObject jsonObject = new JSONObject(response);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
            Toast.makeText(Register.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }) {
        protected Map<String,String> getParams() throws AuthFailureError{
            Map<String, String> param = new HashMap<String, String>();
            //String images = getStringImage(bitmap);
            //param.put("image", images);

            return param;
        }

    };


    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

}
/*
    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100 , baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
/*
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filepath = data.getData();
            try {
                //get the img
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , filepath);
                Toast.makeText(this,""+bitmap, Toast.LENGTH_SHORT).show();
                circleImageView.setImageBitmap(bitmap);
            } catch (IOException e ){
                e.printStackTrace();
            }
        }
*/
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri uri = data.getData();

            galleryPhoto.setPhotoUri(uri);
            photoPath = galleryPhoto.getPath();
            ext = photoPath.substring(photoPath.lastIndexOf(".") + 1, photoPath.length());
            selectedPhoto = photoPath;
            try {

                bitmap = ImageLoader.init().from(photoPath).requestSize(100, 100).getBitmap();
                circleImageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {

                Toast.makeText(getApplicationContext(),
                        "Something Wrong while choosing photos", Toast.LENGTH_SHORT).show();
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FINE_LOCATION_PERMISSION_GRANTED = true;
                    displayLocation();
                } else {
                    FINE_LOCATION_PERMISSION_GRANTED = false;
                }
                break;


            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Register.this , "Permission Granted Successfully! ", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Register.this , "Permission Denied :( ", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

}