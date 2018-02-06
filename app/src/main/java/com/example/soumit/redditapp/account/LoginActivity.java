package com.example.soumit.redditapp.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.soumit.redditapp.FeedAPI;
import com.example.soumit.redditapp.R;
import com.example.soumit.redditapp.URLs;
import com.example.soumit.redditapp.model.Feed;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Soumit on 2/6/2018.
 */

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";
    private URLs urLs = new URLs();

    private ProgressBar progressBar;
    private EditText mUsername;
    private EditText mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_login);
        Log.d(TAG, "onCreate: Started");

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        mUsername = (EditText) findViewById(R.id.input_username);
        mPassword = (EditText) findViewById(R.id.input_password);
        progressBar = (ProgressBar) findViewById(R.id.loginRequestLoadingProgressbar);
        progressBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to Login.");
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if(!username.equals("") && !password.equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    //do the login staff
                    login(username, password);
                }
            }
        });
    }

    private void login(final String username, String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urLs.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Call<CheckLogin> call = feedAPI.signIn(headerMap, username, username, password, "json");
        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {
//                Log.d(TAG, "onResponse: feed : " + response.body().toString());
                try {
                    Log.d(TAG, "onResponse: Server Response : " + response.toString());

                    String modhash = response.body().getJson().getData().getModhash();
                    String cookie = response.body().getJson().getData().getCookie();

                    Log.d(TAG, "onResponse: Modhash: " + modhash);
                    Log.d(TAG, "onResponse: Cookie : " + cookie);

                    if(!modhash.equals("")){
                        setSessionParams(username, modhash, cookie);
                        progressBar.setVisibility(View.GONE);
                        mUsername.setText("");
                        mPassword.setText("");
                        Toast.makeText(LoginActivity.this, "Login Successfull!", Toast.LENGTH_SHORT).show();

                        //navigate back to previous activity
                        finish();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onResponse: NullPointerException " + e.getMessage() );
                }

            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: Unable to receive RSS: " + t.getMessage() );
                Toast.makeText(LoginActivity.this, "An error occured!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setSessionParams(String username, String modhash, String cookie){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        Log.d(TAG, "setSessionParams: Storing session variables : \n" +
                    "Username : " + username + "\n" +
                    "Modhash : " + modhash + "\n" +
                    "Cookie : " + cookie + "\n");

        editor.putString(getString(R.string.SessionUsername), username);
        editor.commit();

        editor.putString(getString(R.string.SessionModhash), modhash);
        editor.commit();

        editor.putString(getString(R.string.SessionCookie), cookie);
        editor.commit();


    }


}























