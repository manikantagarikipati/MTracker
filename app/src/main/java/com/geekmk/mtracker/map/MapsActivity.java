package com.geekmk.mtracker.map;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.base.BaseMapActivity;
import com.geekmk.mtracker.base.BaseMapActivity.MapPermissionsProvidedCB;
import com.geekmk.mtracker.constants.AppConstants;
import com.geekmk.mtracker.constants.AppUtils;
import com.geekmk.mtracker.tracker.TrackerService;
import com.geekmk.mtracker.tracker.TrackerService.LocalBinder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends BaseMapActivity implements OnMapReadyCallback,
    MapPermissionsProvidedCB {

  private GoogleMap mMap;

  private Marker mCurrLocationMarker;

  private boolean registerReceiver;

  private boolean mBound = false;

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
    if (AppUtils.isServiceRunning(TrackerService.class, this)) {
      // If service already running, simply update UI.
      //todo get the current journey and plot markers on map
      bindToTrackerService();
    } else {
      // First time running - check for inputs pre-populated from build.
      if (MapsActivity.super.checkLocationRequestPermissions()) {
        startLocationService();
      }
    }
  }

  private void bindToTrackerService() {
    Intent intent = new Intent(this, TrackerService.class);
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }


  @Override
  protected void onPause() {
    super.onPause();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
  }

  @Override
  protected void onResume() {
    super.onResume();
    LocalBroadcastManager.getInstance(this)
        .registerReceiver(mMessageReceiver, new IntentFilter(AppConstants.LOCATION_INTENT));
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mBound) {
      unbindService(mConnection);
    }
  }

  /**
   * Defines callbacks for service binding, passed to bindService()
   */
  private ServiceConnection mConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className,
        IBinder service) {
      mBound = true;
      // We've bound to LocalService, cast the IBinder and get LocalService instance
      LocalBinder binder = (LocalBinder) service;
      TrackerService trackerService = binder.getService();
      if (trackerService.getCurrentLocation() != null) {
        displayCurrentLocationMarker(trackerService.getCurrentLocation());
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      mBound = false;
    }
  };

  /**
   * Receives location info from the tracking service.
   */
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent != null && intent.getExtras() != null) {
        displayCurrentLocationMarker(
            (Location) intent.getExtras().getParcelable(AppConstants.EXTRA_CUR_LOC));
      }
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
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    mCurrLocationMarker = mMap.addMarker(markerOptions);

    //move map camera
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
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
          startLocationService();
        } else {
          MapsActivity.super.handleLocationPermissionCallBack(this);
        }
        break;
    }
  }

  @Override
  public void onMapPermissionsProvided() {
    startLocationService();
  }

  @Override
  public void onMapPermissionsDenied() {
    Log.e("Location Tracking", "Permissions denied");
  }

  private void startLocationService() {
//    // Before we start the service, confirm that we have extra power usage privileges.
//    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//    Intent intent = new Intent();
//    if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
//      intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//      intent.setData(Uri.parse("package:" + getPackageName()));
//      startActivity(intent);
//    }
    startService(new Intent(this, TrackerService.class));
  }

  private void stopLocationService() {
    if (mBound) {
      unbindService(mConnection);
    }
    stopService(new Intent(this, TrackerService.class));
  }

}
