package com.wgt.mapintegration;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private List<String> listOfPermissions;
    private final int PERMISSION_REQUEST_CODE = 1;

    private TextView tv_longitude, tv_latitude;
    private Button btn_get_location, btn_view_map;
    private RadioGroup radio_type;
    private RadioButton radio_high, radio_medium, radio_low;

    private GoogleApiClient gac;
    private LocationRequest locationRequest;

    private final String TAG = "GPS";
    private final long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private final long FASTEST_INTERVAL = 2000;


    private Location loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUIComponents();

        listOfPermissions = new ArrayList<>();
        listOfPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        listOfPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);


        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }


    @Override
    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getLocation:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkUsesPermission()) {
                        requestPermission();
                    }
                }

                if (!isLocationEnabled()) {
                    showAlert();
                } else {
                    setPriority();
                    gac.connect();
                }
                break;

            case R.id.btn_view_map :
                if (loc != null) {
                    /*String link = "http://maps.google.com/maps?q=loc:" + String.format("%f,%f", loc.getLatitude(), loc.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);*/
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("location", loc);
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "Get location first", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initUIComponents() {
        tv_longitude = findViewById(R.id.tv_longitude_data);
        tv_latitude = findViewById(R.id.tv_latitude_data);
        btn_get_location = findViewById(R.id.btn_getLocation);
        btn_view_map = findViewById(R.id.btn_view_map);

        btn_get_location.setOnClickListener(this);
        btn_view_map.setOnClickListener(this);

        radio_type = findViewById(R.id.radio_loc_type);
        radio_high = findViewById(R.id.radio_high_accuracy);
        radio_medium = findViewById(R.id.radio_medium_accuracy);
        radio_low = findViewById(R.id.radio_low_accuracy);

    }

    //========================== Premission methods and callbacks============================

    private boolean checkUsesPermission() {
        for (String permission : listOfPermissions) {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            boolean status = (result == PackageManager.PERMISSION_GRANTED);
            if (!status) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        try {
            ActivityCompat.requestPermissions(this, listToStringArray(listOfPermissions), PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] listToStringArray(List<String> list) {
        String arr[] = new String[list.size()];
        for (int i=0;i<list.size();i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                boolean success = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (!success) {
                    //one of the permission is not granted
                    finish();
                    return;
                }
            }
        }
    }


    //================Location & Connection methods and callbacks=====================

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Toast.makeText(MainActivity.this, "Enable location service", Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.show();
    }

    private void setPriority() {
        if (radio_high.isChecked()) {
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else if (radio_medium.isChecked()) {
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else if (radio_low.isChecked()) {
            locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        }

    }

    private void call() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(Location location) {
        tv_latitude.setText("" + location.getLatitude());
        tv_longitude.setText("" + location.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        call();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "ERROR : " + connectionResult.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            loc = location;
            updateUI(location);
        }
    }
}
