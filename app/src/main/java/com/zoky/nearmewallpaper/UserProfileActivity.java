package com.zoky.nearmewallpaper;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.zoky.nearmewallpaper.R;

public class UserProfileActivity extends AppCompatActivity{

    public static final String profileURL = "http://webshopappfoi.esy.es/dbGetUser.php?username=";
    public static final String SAVE_URL = "http://webshopappfoi.esy.es/volleyChangeUserDetails.php";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD= "password";
    public static final String KEY_NAME= "name";
    public static final String KEY_SURNAME= "surname";
    public static final String KEY_ADDRESS= "address";
    public static final String KEY_ID= "id";
    public static final String JSON_ARRAY = "result";


    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtName;
    private EditText edtSurname;
    private EditText edtAddress;


    private ProgressDialog loading;

    private TextView txtLogin;
    private Toolbar mToolbar;
    private Button btnLogout;
    private Button btnSave;
    SharedPreferences LoggedInUser;
    public static final String remoteId = "remoteId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mToolbar=(Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtLogin = (TextView) findViewById(R.id.txtLoginTime);

        edtName = (EditText) findViewById(R.id.editTextName);
        edtSurname = (EditText) findViewById(R.id.editTextSurname);
        edtAddress = (EditText) findViewById(R.id.editTextAddress);
        edtEmail = (EditText) findViewById(R.id.editTextEmail);
        edtPassword = (EditText) findViewById(R.id.editTextPassword);
        edtUsername = (EditText) findViewById(R.id.editTextUsername);
        edtUsername.setInputType(InputType.TYPE_NULL);

        btnLogout = (Button) findViewById(R.id.buttonLogOut);
        btnSave = (Button) findViewById(R.id.buttonSave);

        LoggedInUser = getSharedPreferences("SessionManager", MODE_PRIVATE);
        final SharedPreferences.Editor editLogged = LoggedInUser.edit();
        Date loginDatum = new Date(LoggedInUser.getLong("dateTimeKey", 0));
        txtLogin.setText(loginDatum.toString());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLogged.clear();
                editLogged.commit();
                finishscreen();
            }
        });
        GetUserData();

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });
    }

    public void GetUserData(){
        loading = ProgressDialog.show(this, "Please wait...", "Fetching...", false, false);

        String url = profileURL +LoggedInUser.getString("userNameKey", "").toString().trim();

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserProfileActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void showJSON(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            JSONObject userData = result.getJSONObject(0);
            edtUsername.setText(userData.getString(KEY_USERNAME).toString(), TextView.BufferType.EDITABLE);
            edtPassword.setText(userData.getString(KEY_PASSWORD).toString(), TextView.BufferType.EDITABLE);
            edtEmail.setText(userData.getString(KEY_EMAIL).toString(), TextView.BufferType.EDITABLE);
            edtAddress.setText(userData.getString(KEY_ADDRESS).toString(), TextView.BufferType.EDITABLE);
            edtName.setText(userData.getString(KEY_NAME).toString(), TextView.BufferType.EDITABLE);
            edtSurname.setText(userData.getString(KEY_SURNAME).toString(), TextView.BufferType.EDITABLE);

            LoggedInUser = getSharedPreferences("SessionManager", MODE_PRIVATE);
            SharedPreferences.Editor editor = LoggedInUser.edit();
            editor.putString(remoteId, (userData.getString(KEY_ID).toString()));
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void saveUser() {
        final String username = edtUsername.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String name = edtName.getText().toString().trim();
        final String surname = edtSurname.getText().toString().trim();
        final String address = edtAddress.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SAVE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(UserProfileActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserProfileActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){


            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_USERNAME,username);
                params.put(KEY_PASSWORD,password);
                params.put(KEY_EMAIL, email);
                params.put(KEY_NAME, name);
                params.put(KEY_SURNAME, surname);
                params.put(KEY_ADDRESS, address);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent1);
                finishscreen();
            }
        };


        Timer t = new Timer();
        t.schedule(task, 2000);

    }
    private void finishscreen(){
        this.finish();
    }

}