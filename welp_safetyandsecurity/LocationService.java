package com.abomicode.welp_safetyandsecurity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Rohan on 6/16/2017.
 *
 * Listens to the location at periodic intervals.
 */

public class LocationService extends Service{
    //Service created.

    static int isRunning = 0;

    LocationManager locationManager;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("LocationService","onStartCommand");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Log.i("Location Listener","onLocationChanged");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            isRunning = 1;

            //Log.i("isProviderEnabled","isProviderEnabled = true");
            Location location = null;

            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(locationManager!=null){

                Log.i("locationManager!=null","true");


                //Everything OK!

                double latitude = 0;
                double longitude = 0;

                if(location!=null) {

                    latitude = location.getLatitude();

                    longitude = location.getLongitude();
                }
                //Toast.makeText(this,  String.valueOf(latitude) + " " + String.valueOf(longitude), Toast.LENGTH_SHORT).show();

                CurrentUserDetails currentUserDetails = new CurrentUserDetails(getApplicationContext());
                String key = null;
                key = currentUserDetails.getKey();


                DatabaseReference longiReference;
                DatabaseReference latReference;

                if(key!=null) {
                    latReference = FirebaseDatabase.getInstance().getReference().child("users").child(key).child("latitude");
                    longiReference = FirebaseDatabase.getInstance().getReference().child("users").child(key).child("longitude");
                    if (latReference != null && longiReference != null) {
                        latReference.setValue(latitude);
                        longiReference.setValue(longitude);
                    }
                }
            }
        }
        else{
            isRunning =  0;
            stopSelf();
        }
        stopSelf();
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(isRunning==1) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (1000 * 30),
                    PendingIntent.getService(getApplicationContext(), 0, new Intent(getApplicationContext(), LocationService.class), 0));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
