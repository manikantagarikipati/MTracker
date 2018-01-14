package com.geekmk.mtracker.map;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.geekmk.mtracker.helper.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class LocationLiveData extends LiveData<Location> implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

  private GoogleApiClient googleApiClient;

  private static LocationLiveData instance;

  public static synchronized LocationLiveData getInstance(Context appContext) {
    if (instance == null) {
      instance = new LocationLiveData(appContext);
    }
    return instance;
  }

  private LocationLiveData(Context context) {
    googleApiClient =
        new GoogleApiClient.Builder(context, this, this)
            .addApi(LocationServices.API)
            .build();
  }

  @Override
  protected void onActive() {
    // Wait for the GoogleApiClient to be connected
    googleApiClient.connect();
  }

  @Override
  protected void onInactive() {
    if (googleApiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(
          googleApiClient, this);
    }
    googleApiClient.disconnect();
  }

  @SuppressLint("MissingPermission")
  @Override
  public void onConnected(Bundle connectionHint) {
    // Try to immediately find a location
    Location lastLocation = LocationServices.FusedLocationApi
        .getLastLocation(googleApiClient);

    LocationRequest request = new LocationRequest();
    request.setInterval(AppConstants.LOCATION_REQUEST_INTERVAL);
    request.setFastestInterval(AppConstants.LOCATION_REQUEST_INTERVAL_FASTEST);
    request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    if (lastLocation != null) {
      setValue(lastLocation);
    }
    // Request updates if there’s someone observing
    if (hasActiveObservers()) {
      LocationServices.FusedLocationApi.requestLocationUpdates(
          googleApiClient, request, this);
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    // Deliver the location changes
    setValue(location);
  }

  @Override
  public void onConnectionSuspended(int cause) {
    // Cry softly, hope it comes back on its own
  }

  @Override
  public void onConnectionFailed(
      @NonNull ConnectionResult connectionResult) {
    // Consider exposing this state as described here:
    // https://d.android.com/topic/libraries/architecture/guide.html#addendum
  }
}
