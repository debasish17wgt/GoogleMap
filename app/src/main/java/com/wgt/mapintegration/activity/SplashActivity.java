package com.wgt.mapintegration.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wgt.mapintegration.R;
import com.wgt.mapintegration.preference.UserPreference;

public class SplashActivity extends AppCompatActivity {
    private Handler handler;
    private UserPreference userPreference;
    private final int DELAY = 1000;
    private boolean backPressed;

    @Override
    protected void onResume() {
        backPressed = false;
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (!backPressed) {
                            userPreference = new UserPreference(SplashActivity.this);
                            boolean status = userPreference.getLoggedStatus();
                            if (status) {
                                //logged in, go to home
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            } else {
                                //go to login Activity
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    }
                },
                DELAY
        );
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        handler = new Handler();
    }

    @Override
    public void onBackPressed() {
        backPressed = true;
        super.onBackPressed();
    }
}
