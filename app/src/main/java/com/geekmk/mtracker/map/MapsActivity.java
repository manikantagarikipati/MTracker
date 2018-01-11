package com.geekmk.mtracker.map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.base.BaseMapActivity;
import com.geekmk.mtracker.base.BaseMapActivity.MapPermissionsProvidedCB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends BaseMapActivity implements OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    MapPermissionsProvidedCB {

  private GoogleMap mMap;

  private GoogleApiClient mGoogleApiClient;

  private com.google.android.gms.location.LocationRequest mLocationRequest;
  ;
  private Marker mCurrLocationMarker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }


  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    if (MapsActivity.super.checkLocationRequestPermissions()) {
      mMap.setMyLocationEnabled(true);
      startLocationTracking();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
//    if(mGoogleApiClient!=null){
//      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
//    }
  }

  /**
   * Starts location tracking by creating a Google API client, and
   * requesting location updates.
   */
  @SuppressLint("MissingPermission")
  private void startLocationTracking() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(mLocationRequestCallback)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();
  }

  private GoogleApiClient.ConnectionCallbacks mLocationRequestCallback = new GoogleApiClient
      .ConnectionCallbacks() {

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
      displayCurrentLocationMarker(LocationServices.
          FusedLocationApi.getLastLocation(mGoogleApiClient));
      mLocationRequest = new LocationRequest();
      mLocationRequest.setInterval(1000);
      mLocationRequest.setFastestInterval(1000);
      mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
      LocationServices.FusedLocationApi
          .requestLocationUpdates(mGoogleApiClient, mLocationRequest,
              new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                  displayCurrentLocationMarker(location);
                }
              });
    }

    @Override
    public void onConnectionSuspended(int reason) {
      // TODO: Handle gracefully
    }
  };

  private void displayCurrentLocationMarker(Location location) {
    if (mCurrLocationMarker != null) {
      mCurrLocationMarker.remove();
    }

    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);
    markerOptions.title("You!!");
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
    mCurrLocationMarker = mMap.addMarker(markerOptions);

    //move map camera
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
  }


  @Override
  public void onConnected(@Nullable Bundle bundle) {

  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    switch (requestCode) {
      default:
      case BaseMapActivity.REQUEST_ID_MULTIPLE_PERMISSIONS:
        boolean isPermissionsAdded = MapsActivity.super
            .checkLocationRequestProvidedStatus(permissions, grantResults);
        if (isPermissionsAdded) {
          startLocationTracking();
        } else {
          MapsActivity.super.handleLocationPermissionCallBack(this);
        }
        break;
    }
  }

  @Override
  public void onMapPermissionsProvided() {
    startLocationTracking();
  }

  @Override
  public void onMapPermissionsDenied() {
    Log.e("Location Tracking", "Permissions denied");
  }
}
