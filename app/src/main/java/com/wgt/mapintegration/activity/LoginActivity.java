package com.wgt.mapintegration.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.wgt.mapintegration.R;
import com.wgt.mapintegration.database.AppDatabase;
import com.wgt.mapintegration.model.UserModel;
import com.wgt.mapintegration.preference.UserPreference;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Button btn_login, btn_google_login;
    private ImageView iv_pic;
    private TextView tv_name, tv_email;

    private static final String TAG = "GPlusFragent";
    private int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUIComponents();
        btn_login.setVisibility(View.INVISIBLE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

    }

    private void initUIComponents() {
        btn_login = findViewById(R.id.btn_login);
        btn_google_login = findViewById(R.id.btn_google_login);

        iv_pic = findViewById(R.id.iv_pic);
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);

        btn_login.setOnClickListener(this);
        btn_google_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                String email = tv_email.getText().toString();
                String name = tv_name.getText().toString();
                if (email == null || email.equals("")) {
                    Toast.makeText(this, "email not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name == null || name.equals("")) {
                    Toast.makeText(this, "name not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserModel userModel = new UserModel(email, name, null, true);
                UserPreference userPreference = new UserPreference(this);
                userPreference.saveUser(userModel);
                AppDatabase.getDatabase(this).userDao().addUser(userModel);

                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

            case R.id.btn_google_login :
                if (btn_google_login.getText().toString().equals("Sign in with Google")) {
                    login();
                } else if (btn_google_login.getText().toString().equals("Logout")) {
                    logout();
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            btn_login.setVisibility(View.VISIBLE);
            btn_google_login.setText("Logout");
            tv_name.setText(acct.getDisplayName());
            tv_email.setText(acct.getEmail());
            Glide.with(this)
                    .load(acct.getPhotoUrl())
                    .error(R.drawable.user)
                    .into(iv_pic);
        } else {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void logout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //updateUI(false);
                        if (status.isSuccess()) {
                            //TODO: logout successful
                            Toast.makeText(LoginActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                            btn_google_login.setText("Sign in with Google");
                            btn_login.setVisibility(View.INVISIBLE);
                        } else {
                            //TODO: logout failed
                            Toast.makeText(LoginActivity.this, "Failed to logout", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void login() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed : "+connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
