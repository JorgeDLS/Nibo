package com.alium.nibo.di;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

/**
 * Created by aliumujib on 05/05/2018.
 */

public class GoogleClientModule {


    private AppCompatActivity activity;
    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private final GoogleApiClient.OnConnectionFailedListener connectionFailedListener;

    private GoogleApiClient googleApiClient;
    private PlacesClient placesClient;
    private LocationRequest mLocationRequest;


    public GoogleClientModule(AppCompatActivity activity,
                              GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                              GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        this.activity = activity;
        this.connectionCallbacks = connectionCallbacks;
        this.connectionFailedListener = connectionFailedListener;
    }


    public LocationRequest getLocationRequest() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(20)
                .setMaxWaitTime(10 * 1000)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(2 * 1000);
        return mLocationRequest;
    }

    public GoogleApiClient getGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient
                    .Builder(activity)
                    .enableAutoManage(activity, 0, connectionFailedListener)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .build();
        }

        return googleApiClient;
    }

    public PlacesClient getPlacesClient() {
        if (placesClient == null) {
          Places.initialize(activity.getApplicationContext(), getMapsAPIKeyFromManifest());
          placesClient = Places.createClient(activity.getApplicationContext());
        }

        return placesClient;
    }

    private String getMapsAPIKeyFromManifest() {
        try {
            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("GoogleApiKey", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("GoogleApiKey", "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return "";
    }


}
