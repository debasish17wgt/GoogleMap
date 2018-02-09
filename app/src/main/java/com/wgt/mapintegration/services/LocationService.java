package com.wgt.mapintegration.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wgt.mapintegration.utils.Constant;

public class LocationService extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private IBinder iBinder = new MyBinder();

    private GoogleApiClient gac;
    private LocationRequest locationRequest;

    private final String TAG = "GPS";
    private final long UPDATE_INTERVAL = 2 * 1000;
    private final long FASTEST_INTERVAL = 2000;

    private Location loc;


    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constant.ACTION.ACTION_START_LOCATION_SERVICE)) {
            gac.connect();
        } else if (intent.getAction().equals(Constant.ACTION.ACTION_STOP_LOCATION_SERVICE)) {
            stopForeground(true);
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gac.disconnect();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        call();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //stopSelf();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //stopForeground(true);
        stopSelf();
    }


    @Override
    public void onLocationChanged(Location location) {
        loc = location;
    }

    //location call
    private void call() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //binder class
    public class MyBinder extends Binder {
        LocationService getServiec() {
            return LocationService.this;
        }
    }
}
