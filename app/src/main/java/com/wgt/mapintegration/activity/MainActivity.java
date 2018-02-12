package com.wgt.mapintegration.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.wgt.mapintegration.R;
import com.wgt.mapintegration.database.AppDatabase;
import com.wgt.mapintegration.model.LocationModel;
import com.wgt.mapintegration.services.LocationService;
import com.wgt.mapintegration.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private List<String> listOfPermissions;
    private final int PERMISSION_REQUEST_CODE = 1;

    private TextView tv_longitude, tv_latitude;
    private Button btn_get_location, btn_view_map, btn_loc_histry;
    private RadioGroup radio_type;
    private RadioButton radio_high, radio_medium, radio_low;

    private LocationService locationService;
    private boolean locServiceStatus;
    private Intent locationServiceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUIComponents();

        // initialize permissions
        listOfPermissions = new ArrayList<>();
        listOfPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        listOfPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        //initialize intent
        locationServiceIntent = new Intent(this, LocationService.class);


    }

    @Override
    protected void onResume() {

        // check for previously running service, if running bound to it.
        if (isMyServiceRunning()) {
            bindService(locationServiceIntent, locationServiceConnection, BIND_AUTO_CREATE);
            btn_get_location.setText("Stop Service");
        } else {
            btn_get_location.setText("Start Service");
        }

        //register broadcast for listening to incoming location data from service
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(
                        locationReceiver,
                        new IntentFilter(Constant.INTENT.INTENT_LOCATION_BROADCAST)
                );

        //register broadcast to listen for service to stop
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(
                        serviceStoppedReceiver,
                        new IntentFilter(Constant.INTENT.INTENT_LOCATION_SERVICE_STOPPED)
                );

        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceStoppedReceiver);
        if (isMyServiceRunning()) {
            unbindService(locationServiceConnection);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        //unbindService(locationServiceConnection);

        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getLocation:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkUsesPermission()) {
                        requestPermission();
                        return;
                    }
                }

                if (!isLocationEnabled()) {
                    showAlert();
                    return;
                } else {
                    //call service to start and bind
                    //and get data from that service

                    if (btn_get_location.getText().toString().equals("Start Service")) {
                        locationServiceIntent.setAction(Constant.ACTION.ACTION_START_LOCATION_SERVICE);
                        startService(locationServiceIntent);
                        boolean b = bindService(locationServiceIntent, locationServiceConnection, BIND_AUTO_CREATE);
                        if (b) {
                            //Toast.makeText(this, "++++++SERVICE BOUND++++++", Toast.LENGTH_SHORT).show();
                            btn_get_location.setText("Stop Service");
                        } else {
                            Toast.makeText(this, "++++++SERVICE NOT BOUND++++++", Toast.LENGTH_SHORT).show();
                        }
                    } else if (btn_get_location.getText().toString().equals("Stop Service")) {
                        locationServiceIntent.setAction(Constant.ACTION.ACTION_STOP_LOCATION_SERVICE);
                        startService(locationServiceIntent);
                        btn_get_location.setText("Start Service");
                    }


                }
                break;

            case R.id.btn_view_map:
                /*if (loc != null) {
                    *//*String link = "http://maps.google.com/maps?q=loc:" + String.format("%f,%f", loc.getLatitude(), loc.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);*//*
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("location", loc);
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "Get location first", Toast.LENGTH_SHORT).show();
                }*/
                if (AppDatabase.getDatabase(this).locationDao().getAllLocations().size() > 0) {
                    Intent intent = new Intent(this, MapsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(locationService, "No data to show", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_loc_histry:
                List<LocationModel> list = AppDatabase.getDatabase(this).locationDao().getAllLocations();
                String data = "";
                if (list == null || list.size() == 0) {
                    Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(LocationModel loc : list) {
                    data += "LAT : "+loc.getLatitude()+" LON : "+loc.getLongitude()+"\n";
                    /*data.concat("LAT : "+loc.getLatitude()+" LON"+loc.getLongitude());
                    data.concat("\n");*/
                }
                showLocation(data);
                break;
        }
    }

    private void initUIComponents() {
        tv_longitude = findViewById(R.id.tv_longitude_data);
        tv_latitude = findViewById(R.id.tv_latitude_data);
        btn_get_location = findViewById(R.id.btn_getLocation);
        btn_view_map = findViewById(R.id.btn_view_map);
        btn_loc_histry = findViewById(R.id.btn_loc_histry);

        btn_get_location.setOnClickListener(this);
        btn_view_map.setOnClickListener(this);
        btn_loc_histry.setOnClickListener(this);

        radio_type = findViewById(R.id.radio_loc_type);
        radio_high = findViewById(R.id.radio_high_accuracy);
        radio_medium = findViewById(R.id.radio_medium_accuracy);
        radio_low = findViewById(R.id.radio_low_accuracy);

    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
        for (int i = 0; i < list.size(); i++) {
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


    //================LocationModel & Connection methods and callbacks=====================

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

    private void showLocation(String data) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Location History")
                .setMessage(data)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }


    private void updateUI(Location location) {
        if (location != null) {
            tv_latitude.setText("" + location.getLatitude());
            tv_longitude.setText("" + location.getLongitude());
        }
    }


    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService = ((LocationService.MyBinder) iBinder).getServiec();
            locServiceStatus = true;

            Location location = locationService.getLocation();
            if (location != null) {
                //Toast.makeText(MainActivity.this, "Location received", Toast.LENGTH_SHORT).show();
                updateUI(location);
            } else {
                //Toast.makeText(MainActivity.this, "Service started but location not received", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
            locServiceStatus = false;
            Toast.makeText(MainActivity.this, "MAIN++++++LocationService Stopped++++++", Toast.LENGTH_SHORT).show();
        }
    };

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                double lat = intent.getDoubleExtra(Constant.INTENT.INTENT_LOCATION_LAT, 0);
                double lon = intent.getDoubleExtra(Constant.INTENT.INTENT_LOCATION_LONG, 0);
                tv_latitude.setText("" + lat);
                tv_longitude.setText("" + lon);
            }
        }
    };

    private BroadcastReceiver serviceStoppedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unbindService(locationServiceConnection);
            btn_get_location.setText("Start Service");
        }
    };
}
