package com.geekmk.mtracker.map;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.base.BaseMapActivity;
import com.geekmk.mtracker.base.BaseMapActivity.MapPermissionsProvidedCB;
import com.geekmk.mtracker.helper.AppPreferences;
import com.geekmk.mtracker.helper.AppUtils;
import com.geekmk.mtracker.tracker.TrackerService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.List;

public class MapsActivity extends BaseMapActivity implements OnMapReadyCallback,
    MapPermissionsProvidedCB {

  private GoogleMap mMap;

  private Marker mCurrLocationMarker;
  private Polyline polyline;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    if (MapsActivity.super.checkLocationRequestPermissions()) {
      observeCurrentLocation();
    }
  }

  private void observeCurrentLocation() {
    LocationLiveData.getInstance(this).observe(this,
        new Observer<Location>() {
          @Override
          public void onChanged(@Nullable Location location) {
            displayCurrentLocationMarker(location);
          }
        });
    if (AppPreferences.getCurrentJourneyId(this) != 0) {
      //todo load latlng from db and also start service if it is killed/not recreated in any case
      if (!AppUtils.isServiceRunning(TrackerService.class, this)) {
        startService(new Intent(MapsActivity.this, TrackerService.class));
      }
    }
  }


  private void displayCurrentLocationMarker(Location location) {

    if (AppPreferences.getCurrentJourneyId(MapsActivity.this) != 0) {
      //this is a journey marker location hence just mark it as a dot etc.. as a path
      addLocationToPolyLine(location);
    } else {
      if (mCurrLocationMarker != null) {
        mCurrLocationMarker.remove();
      }
      mCurrLocationMarker = addMarker(location, "You!!");
    }
  }

  private void addLocationToPolyLine(Location location) {
    if (polyline == null) {
      PolylineOptions polylineOptions = new PolylineOptions();
      polylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
      polylineOptions.width(18);

      polylineOptions.color(ContextCompat.getColor(this, R.color.polylinecolor));
      polyline = mMap.addPolyline(polylineOptions);
    } else {
      List<LatLng> existingPOints = polyline.getPoints();
      existingPOints.add(new LatLng(location.getLatitude(), location.getLongitude()));
      polyline.setPoints(existingPOints);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    switch (requestCode) {
      case BaseMapActivity.REQUEST_ID_MULTIPLE_PERMISSIONS:
        boolean isPermissionsAdded = MapsActivity.super
            .checkLocationRequestProvidedStatus(permissions, grantResults);
        if (isPermissionsAdded) {
          observeCurrentLocation();
        } else {
          MapsActivity.super.handleLocationPermissionCallBack(this);
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void onMapPermissionsProvided() {
    observeCurrentLocation();
  }

  @Override
  public void onMapPermissionsDenied() {
    Log.e("Location Tracking", "Permissions denied");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_map, menu);
    // Get the action view used in your toggleservice item
    final MenuItem toggle = menu.findItem(R.id.menu_switch);
    SwitchCompat mSwitch = toggle.getActionView().findViewById(R.id.switchInActionBar);
    mSwitch.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (((SwitchCompat) v).isChecked()) {
          startJourney();
        } else {
          AppPreferences.setJourneyId(0, MapsActivity.this);
          setEndMarker();
          stopService(new Intent(MapsActivity.this, TrackerService.class));
        }
      }
    });
    setSwitchStatus(mSwitch);
    return super.onCreateOptionsMenu(menu);
  }

  private void startJourney() {
    if (polyline != null) {
      List<LatLng> points = polyline.getPoints();
      points.clear();
      polyline.setPoints(points);
    }
    AppPreferences.setJourneyId(AppUtils.getUniqueId(), MapsActivity.this);
    startService(new Intent(MapsActivity.this, TrackerService.class));
  }

  private void setEndMarker() {
    if (polyline != null) {
      List<LatLng> existingPoints = polyline.getPoints();
      LatLng latLng = existingPoints.get(existingPoints.size() - 1);
      Location location = new Location("");
      location.setLatitude(latLng.latitude);
      location.setLongitude(latLng.longitude);
      addMarker(location, "");
    }
  }

  public void setSwitchStatus(SwitchCompat mSwitch) {
    if (AppUtils.isServiceRunning(TrackerService.class, this) && mSwitch != null) {
      mSwitch.setChecked(true);
    }
  }

  private Marker addMarker(Location location, String title) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);
    markerOptions.title(title);
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    Marker marker = mMap.addMarker(markerOptions);
    //move map camera
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

    return marker;
  }

}
