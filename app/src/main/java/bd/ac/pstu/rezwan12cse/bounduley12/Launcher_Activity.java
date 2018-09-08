package bd.ac.pstu.rezwan12cse.bounduley12;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase.MapView;
import bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase.PhoneLogin;
import bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase.SpinnerActivity;
import bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase.UserRegister;

public class Launcher_Activity extends AppCompatActivity {

    private static final int SPLASH_TIME = 3000;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_launcher);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            Intent intent = new Intent(Launcher_Activity.this, MapView.class);
            startActivity(intent);
            finish();
        }else {
            new BackgroundTask().execute();
        }
    }


    public class BackgroundTask extends AsyncTask {
        Intent intent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            intent = new Intent(Launcher_Activity.this, PhoneLogin.class); //spinneractivity.class hobey ekhaney

        }


        @Override
        protected Object doInBackground(Object[] params) {

            /*  Use this method to load background
            * data that your app needs. */

            try {
                Thread.sleep(SPLASH_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            Pass your loaded data here using Intent

//            intent.putExtra("data_key", "");
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }
}

