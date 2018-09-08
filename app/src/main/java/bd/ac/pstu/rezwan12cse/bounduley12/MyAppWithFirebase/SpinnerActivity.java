package bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bd.ac.pstu.rezwan12cse.bounduley12.PagerActivity;
import bd.ac.pstu.rezwan12cse.bounduley12.R;
import bd.ac.pstu.rezwan12cse.bounduley12.Utils;


public class SpinnerActivity extends AppCompatActivity {


    String[] colors = {"#96CC7A", "#EA705D", "#66BBCC"};

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;



    Spinner spinner;
    public static final String URL="http://rrsaikat.mydiscussion.net/FL/FriendList.php";
    private JSONArray result;
    ArrayList<String> items;
    List<String> List_Frame_Pwd, List_cnbid;
    int Int_Frame_Pos;

    public AlertDialog.Builder ex;

  //  Spinner Spinn_Frame;
    EditText Edit_Frame_Pwd;
    Button But_Go;
    String Str_Frame_Pwd, Str_Alert_Msg, Str_Alert_Title;

    String IP_Address;
    private SharedPreferences prefs;
    private String prefName = "report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //////
        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(SpinnerActivity.this, PREF_USER_FIRST_TIME, "true"));

        Intent introIntent = new Intent(SpinnerActivity.this, PagerActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        if (isUserFirstTime)
            startActivity(introIntent);

        ///////


        setContentView(R.layout.activity_spinner);

        boolean check = isNetworkAvailable();
        if (check == false) {
            android.support.v7.app.AlertDialog.Builder Alert_Conn_Error = new android.support.v7.app.AlertDialog.Builder(SpinnerActivity.this);
            Alert_Conn_Error.setMessage("Check your Internet Connection..");
            Alert_Conn_Error.setTitle("Connection Error");
            Alert_Conn_Error.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                    finish();
                }
            });

            Alert_Conn_Error.show();

        } else {


            //volley code starts here
            items = new ArrayList<>();
            List_Frame_Pwd = new ArrayList<String>();
            List_cnbid = new ArrayList<String>();
            spinner = (Spinner) findViewById(R.id.spinner2);
            Edit_Frame_Pwd = (EditText) findViewById(R.id.editText1);

            prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            IP_Address = prefs.getString("ip", "http://pstu.a0001.net/FL/");

            Int_Frame_Pos = 0;
            spinner.setSelection(Int_Frame_Pos);
//            Str_Frame_Pwd = List_Frame_Pwd.get(Int_Frame_Pos).toString();

            loadSpinnerData(URL);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    //String names = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                    //Toast.makeText(getApplicationContext(), names, Toast.LENGTH_LONG).show();
                    //Edit_Frame_Pwd.setText(getPassword(position));
                    Int_Frame_Pos = position;
                    Str_Frame_Pwd = List_Frame_Pwd.get(Int_Frame_Pos).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // DO Nothing here
                }
            });


            ex = new AlertDialog.Builder(this).setTitle("Exit ?").setMessage("Do You Want To Exit")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }


                    });

            Edit_Frame_Pwd = (EditText) findViewById(R.id.editText1);
            But_Go = (Button) findViewById(R.id.button1);
            But_Go.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if(Edit_Frame_Pwd.getText().toString().equalsIgnoreCase("")) {

                        Str_Alert_Msg = "Enter Password !!!";
                        Str_Alert_Title = "Error";
                        alert_method();
                    }
                    else {
                        if(Edit_Frame_Pwd.getText().toString().equals(Str_Frame_Pwd)) {

                            if(Int_Frame_Pos == 0) {

                                save();
                            }
                            if(Int_Frame_Pos == 1) {

                                save();
                            }
                            if(Int_Frame_Pos == 2) {

                                save();
                            }
                            if(Int_Frame_Pos == 3) {

                                save();
                            }
                        }
                        else {
                            Str_Alert_Msg = "Wrong Password !!!";
                            Str_Alert_Title = "Error";
                            alert_method();
                        }

                    }
                }

            });

        }
    }

    private void save() {

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //---save the values in the EditText view to preferences---
        editor.putInt("cnb_id", Integer.parseInt(List_cnbid.get(Int_Frame_Pos).toString()));

        //---saves the values---
        editor.commit();

        finish();

        Intent i = new Intent(SpinnerActivity.this, PhoneLogin.class);
        startActivity(i);
        Edit_Frame_Pwd.setText(null);
    }

    public void alert_method() {

        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(SpinnerActivity.this);
        alert.setMessage(Str_Alert_Msg);
        alert.setTitle(Str_Alert_Title);
        alert.setPositiveButton("OK", null);
        alert.show();
        Edit_Frame_Pwd.setText(null);
    }

    private void loadSpinnerData(String url) {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Result" , response.toString());
                JSONObject j = null;
                try{
                    j =new JSONObject(response);
                    //Parsing the fetched Json String to JSON Object
                   // j = new JSONObject(response);
                    //Storing the Array of JSON String to our JSON Array
                    result = j.getJSONArray("FL");
                    getItems(result);

                    prefs = getSharedPreferences(prefName, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("ip", IP_Address);

                    editor.commit();



                    // JSONObject jsonObject=new JSONObject(response);
                   // if(jsonObject.getInt("success")==1){
                       // JSONArray jsonArray=jsonObject.getJSONArray("Name");  // json array name "Name"
                   }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(SpinnerActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }



    private void getItems(JSONArray jsonArray) throws JSONException {
        //JSONArray jsonArray = new JSONArray(response);  //we have a json without array name , so have to use this line of code
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject1=jsonArray.getJSONObject(i);
            String name=jsonObject1.getString("name");

             List_Frame_Pwd.add(jsonObject1.getString("pwd"));
             List_cnbid.add(String.valueOf(jsonObject1.getInt("id")));
             items.add(name);
            //List_cnbid.add(String.valueOf(jsonObject1.getInt("cnb_id")));
        }
        //  }
        spinner.setAdapter(new ArrayAdapter<String>(SpinnerActivity.this, android.R.layout.simple_spinner_dropdown_item, items));

    }

    private String getPassword(int position){
        String name="";
        try {
            //Getting object of given index
            JSONObject json = result.getJSONObject(position);

            //Fetching name from that object
            name = json.getString("pwd");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return name;
    }











    @Override
    protected void onStart() {
        super.onStart();
        final TextView tx = (TextView) findViewById(R.id.textview2);

        tx.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tx.setSelected(true);
        tx.setSingleLine(true);
        tx.setText(R.string.pstu_heading);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        ex.show();
    }

}

