package com.zoky.nearmewallpaper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zoky.nearmewallpaper.R;


public class LoginActivity extends AppCompatActivity{

    public static final String LOGIN_URL = "http://webshopappfoi.esy.es/volleyLogin.php";

    public static final String KEY_USERNAME="username";
    public static final String KEY_PASSWORD="password";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;

    public String username = null;
    public String password = null;

    private Toolbar mToolbar;

    SharedPreferences SessionManager;
    public static final String UserName = "userNameKey";
    public static final String dateTime = "dateTimeKey";
    public static final String remoteId = "remoteId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar=(Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);

        buttonLogin = (Button) findViewById(R.id.btnLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (editTextUsername.getText().length()!=0 && editTextPassword.getText().length()!=0)
                    userLogin();
                else
                    Toast.makeText(LoginActivity.this, "Enter your credentials or register", Toast.LENGTH_LONG).show();
            }
        });

        buttonRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        buttonRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                /*
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();*/
            }
        });
    }

    private void userLogin() {
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        final Date currDate = new Date();
        final Long timeDate = currDate.getTime();
        SessionManager = getSharedPreferences("SessionManager", MODE_PRIVATE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            SharedPreferences.Editor editorSession = SessionManager.edit();
                            editorSession.putString(UserName, username);
                            editorSession.putLong(dateTime, timeDate);
                            editorSession.putString(remoteId, "");
                            editorSession.apply();
                            Toast.makeText(LoginActivity.this, "Session details are saved on "+ currDate, Toast.LENGTH_LONG).show();
                            openProfile();
                        }else{
                            Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new HashMap();
                map.put(KEY_USERNAME,username);
                map.put(KEY_PASSWORD,password);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void openProfile(){
        /*Intent intent = new Intent(getApplicationContext(),
                UserProfileActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        startActivity(intent);
        */
    }


}