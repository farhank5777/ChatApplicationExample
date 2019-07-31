package com.example.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.chatapplication.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private TextView register;
    private Button buttonLogin;
    private EditText userName, password;
    private RequestQueue rQueue;
    ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
        rQueue = Volley.newRequestQueue(Login.this);
    }

    private void initView() {
        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        buttonLogin = findViewById(R.id.loginButton);
        buttonLogin.setOnClickListener(this);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.register:
                startActivity(new Intent(Login.this, Register.class));
                break;
            case R.id.loginButton:
                loginIntoApp();
                break;
            default:
        }

    }

    private void loginIntoApp() {
        final String user = binding.username.getText().toString();//userName.getText().toString();
        final String pass = binding.password.getText().toString();
        if (user.equals("")) {
            binding.username.setError("Please Enter Name");
            return;
        }
        if (pass.equals("")) {
            binding.password.setError("Please Enter Password");
            return;
        }
        String url = "https://chat-application-7cba7.firebaseio.com/user.json";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("null")) {
                    Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (!obj.has(user)) {
                            Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                        } else if (obj.getJSONObject(user).getString("password").equals(pass)) {
                            UserDetails.username = user;
                            UserDetails.password = pass;
                            startActivity(new Intent(Login.this, User.class));
                        } else {
                            Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "" + error.getMessage(), Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
        });

        rQueue.add(stringRequest);
    }
}
