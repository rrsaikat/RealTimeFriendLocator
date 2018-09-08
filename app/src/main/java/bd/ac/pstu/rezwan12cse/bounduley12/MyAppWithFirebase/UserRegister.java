package bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import bd.ac.pstu.rezwan12cse.bounduley12.Constants1;
import bd.ac.pstu.rezwan12cse.bounduley12.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserRegister extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    EditText uname, uemail, umobile, upassword, uLa, uLo;
    String UserId;
    ImageButton iBtn;
    CircleImageView cIV;
    Button btnSUp;
    TextView log_here;
    private String userID;
    private static final String TAG = UserRegister.class.getSimpleName();

    public DatabaseReference mFirebasedatabase;
    public FirebaseDatabase mfirebaseInstance;

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    final int LOCATION_REQUEST =2;
    boolean FINE_LOCATION_PERMISSION_GRANTED = false;
    //LocationRequest mLocationRequest;

    Constants1 constants;
    CheckBox tAndC;
    ProgressDialog pd;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private FirebaseAuth mAuth;

    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        log_here = (TextView)findViewById(R.id.signup_loginhere);
        uname = (EditText) findViewById(R.id.F_name);
        uemail = (EditText) findViewById(R.id.F_email);
        umobile = (EditText) findViewById(R.id.F_mobile);
        //upassword = (EditText) findViewById(R.id.F_password);
        btnSUp = (Button) findViewById(R.id.btn_signup);
        uLa = (EditText) findViewById(R.id.la);
        uLo = (EditText) findViewById(R.id.lo);
        iBtn = (ImageButton) findViewById(R.id.F_upImg);
        cIV = (CircleImageView)findViewById(R.id.profile_image);
        cIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mfirebaseInstance = FirebaseDatabase.getInstance();
        mFirebasedatabase = mfirebaseInstance.getReference("AuthUsers");

        btnSUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                pd = new ProgressDialog(UserRegister.this);
                pd.setMessage("Please wait..."); // Setting Message
                pd.setTitle("Data insertion occurs in the server"); // Setting Title
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                pd.show(); // Display Progress Dialog
                pd.setCancelable(true);


                String name = uname.getText().toString().trim();
                String email = uemail.getText().toString().trim();
                Double userMobile = Double.valueOf(umobile.getText().toString().trim());
                //String userPassword = upassword.getText().toString().trim();

                Double userLatitude = Double.valueOf(uLa.getText().toString().trim());
                Double userLongitude = Double.valueOf(uLo.getText().toString().trim());
*/
                UpdateUserInformation();
                addUserChangeListener();
/*
                //check for already exists
                if (TextUtils.isEmpty(userID)) {

                    createUser(name, email, userMobile,userLatitude, userLongitude);
                    Intent intent = new Intent(UserRegister.this, MapView.class);
                    startActivity(intent);
                    return;

                } else {
                    //updateUser(name, email, userMobile,userLatitude, userLongitude);
                    //UpdateUserInformation();
                }
*/
            }
        });
        togleButton();
        buildGoogleApiClient();
        displayLocation();
    }

    private void UpdateUserInformation() {
        UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("AuthUsers").child(UserId);

        String mName = uname.getText().toString();
        String mEmail = uemail.getText().toString();
        String mPhone = umobile.getText().toString();
        Double userLatitude = Double.valueOf(uLa.getText().toString());
        Double userLongitude = Double.valueOf(uLo.getText().toString());

        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("mobile", mPhone);
        userInfo.put("email", mEmail);
        userInfo.put("latitude",userLatitude);
        userInfo.put("longitude",userLongitude);
        userInfo.put("timestamp", getCurrentTimestamp());
        mDatabase.updateChildren(userInfo);

        if (filePath != null) {
            pd = new ProgressDialog(this);
            pd.setTitle("Uploading...");
            pd.show();

            StorageReference ref = storageReference.child("profile_images").child(UserId);
            //StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl.toString());
                    mDatabase.updateChildren(newImage);

                    Toast.makeText(UserRegister.this, "Succesfully uploaded", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserRegister.this, MapView.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(UserRegister.this, "Failed to upload" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (10.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    pd.setMessage("Uploaded " + (int) progress + "%");
                }
            });

        }
    }

    private String getCurrentTimestamp() {
        //Long timestamp = System.currentTimeMillis()/1000;
        //return timestamp;

        String mytime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
        String mydate =java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        return mytime;
    }


    private void createUser(String name, String email, Double userMobile,Double userLatitude, Double userLongitude) {
        if (TextUtils.isEmpty(userID)) {
            mAuth = FirebaseAuth.getInstance();       //eta kaj korbey tokhon e jokhon user authenticated hobey
            userID = mAuth.getCurrentUser().getUid();
        }
        User mUser = new User(name, email, userMobile, userLatitude, userLongitude);
        mFirebasedatabase.child(userID).setValue(mUser);
       // pd.dismiss();
        addUserChangeListener();
    }

    private void addUserChangeListener() {
        String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("AuthUsers").child(UserId);
        mFirebasedatabase.child("AuthUsers").child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Log.e(TAG, "User data is null");
                    log_here.setText("No data found, Please register!");
                    return;
                }else {
                    Log.e(TAG, "User data is changed!");
                    log_here.setText("User is Registered, Please update your data!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read user", databaseError.toException());
            }
        });
    }

    private void togleButton() {
        if (!TextUtils.isEmpty(userID)) {
            btnSUp.setText("UPDATE");
        } else {
            btnSUp.setText("REGISTER");
        }
    }

    private void displayLocation() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if ( permissionCheck != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            uLa.setText(Double.toString(latitude));
            uLo.setText(Double.toString(longitude));

        } else {
            //uLa.setText("Error! Make sure location is enabled on the device");
            Toast.makeText(getApplicationContext(), "Error! Make sure location is enabled on the device", Toast.LENGTH_LONG).show();;
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
            case PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FINE_LOCATION_PERMISSION_GRANTED = true;
                    displayLocation();
                } else {
                    FINE_LOCATION_PERMISSION_GRANTED = false;
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
               // byte[] data = baos.toByteArray();
               // UploadTask uploadTask = filePath.putBytes(data);
                cIV.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        getUserInfo();
        displayLocation();
        togleButton();


    }
    private void getUserInfo(){
        UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("AuthUsers").child(UserId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        uname.setText(map.get("name").toString());
                    }
                    if (map.get("mobile") != null) {
                        umobile.setText(map.get("mobile").toString());
                    }
                    if (map.get("email") != null) {
                        uemail.setText(map.get("email").toString());
                    }
                    if(map.get("profileImageUrl")!=null){
                        String mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(cIV);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
*/

    @Override
    public void onConnected(Bundle arg0) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

}