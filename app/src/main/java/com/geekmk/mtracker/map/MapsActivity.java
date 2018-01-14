package com.geekmk.mtracker.map;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
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
import com.geekmk.mtracker.base.MRepo;
import com.geekmk.mtracker.database.journey.JourneyFetchCB;
import com.geekmk.mtracker.database.journey.JourneyInsertCB;
import com.geekmk.mtracker.database.journey.MJourney;
import com.geekmk.mtracker.database.location.LocationFetchCB;
import com.geekmk.mtracker.database.location.MLocation;
import com.geekmk.mtracker.helper.AppConstants.JourneyStatus;
import com.geekmk.mtracker.helper.AppPreferences;
import com.geekmk.mtracker.helper.AppUtils;
import com.geekmk.mtracker.helper.CollectionUtils;
import com.geekmk.mtracker.helper.MapUtils;
import com.geekmk.mtracker.journey.JourneyListActivity;
import com.geekmk.mtracker.tracker.TrackerService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends BaseMapActivity implements OnMapReadyCallback,
    MapPermissionsProvidedCB {

  private GoogleMap mMap;
  private SwitchCompat mSwitch;
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
    //observe for user current location using live data model
    LocationLiveData.getInstance(this).observe(this,
        new Observer<Location>() {
          @Override
          public void onChanged(@Nullable Location location) {
            displayCurrentLocationMarker(location);
          }
        });
    displayCurrentJourneyPath();
  }

  ///If there is already an journey going on rebuild the path UI
  private void displayCurrentJourneyPath() {
    if ((AppPreferences.getCurrentJourneyId(this) != 0)) {
      if (!AppUtils.isServiceRunning(TrackerService.class, this)) {
        startService(new Intent(MapsActivity.this, TrackerService.class));
        if (mSwitch != null) {
          mSwitch.setChecked(true);
        }
      }

      MRepo.getLocationsForJourney(AppPreferences.getCurrentJourneyId(this), new LocationFetchCB() {
        @Override
        public void onLocationLoaded(List<MLocation> mLocations) {
          //when locations are avaiable plot those locations
          if (CollectionUtils.isNotEmpty(mLocations)) {
            MLocation startLocation = mLocations.get(0);
            if (mCurrLocationMarker != null) {
              mCurrLocationMarker.remove();
            }
            mCurrLocationMarker = MapUtils
                .addMarker(startLocation.getLatitude(), startLocation.getLongitude(), mMap,
                    "");
            polyline = MapUtils.displayPathInfo(mMap, mLocations,
                ContextCompat.getColor(MapsActivity.this, R.color.polylinecolor));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(startLocation.getLatitude(), startLocation.getLongitude()), 11));
          }
        }

        @Override
        public void onLocationEmpty() {
        }
      });
    }
  }


  private void displayCurrentLocationMarker(Location location) {
    //if journey is already going on then location is part of the polyline indeed
    if (AppPreferences.getCurrentJourneyId(MapsActivity.this) != 0) {
      //this is a journey marker location hence just mark it as a dot etc.. as a path
      addLocationToPolyLine(location);
    } else {
      //if no journey is going on then simply replace the current location marker
      if (mCurrLocationMarker != null) {
        mCurrLocationMarker.remove();
      }
      mCurrLocationMarker = MapUtils.addMarker(location, "You!!", mMap);
    }
  }

  private void addLocationToPolyLine(Location location) {
    if (polyline == null) {
      polyline = MapUtils
          .displayPathInfo(mMap, location, ContextCompat.getColor(this, R.color.polylinecolor));
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
    //once locations are provided start looking for location updates
    observeCurrentLocation();
  }

  @Override
  public void onMapPermissionsDenied() {
    Log.e("Location Tracking", "Permissions denied");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_map, menu);
    final MenuItem toggle = menu.findItem(R.id.menu_switch);
    mSwitch = toggle.getActionView().findViewById(R.id.switchInActionBar);
    mSwitch.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (((SwitchCompat) v).isChecked()) {
          startJourney();
        } else {
          stopJourney();
        }
      }
    });
    setSwitchStatus(mSwitch);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_journey_list:
        startActivity(new Intent(MapsActivity.this, JourneyListActivity.class));
        return false;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void stopJourney() {
    final LatLng latLng = setEndMarker();
    //perform operation in background
    MRepo.fetchJourney(AppPreferences.getCurrentJourneyId(MapsActivity.this), new JourneyFetchCB() {
      @Override
      public void onJourneyLoaded(MJourney journey) {
        if (latLng != null) {
          journey.setEndLatLng(AppUtils.buildLatLngString(latLng));
          journey.setEndTime(Calendar.getInstance().getTime());
          journey.setStatus(JourneyStatus.COMPLETED);
          MRepo.addJourney(journey, null);
          List<LatLng> points = polyline.getPoints();
          points.clear();
          polyline.setPoints(points);
          AppUtils.geoCode(MapsActivity.this, journey.getJourneyId(),
              latLng.latitude, latLng.longitude,
                  false);
          AppUtils.showToast(MapsActivity.this, R.string.msg_journey_completed);
          AppUtils.displayJourneyDetail(MapsActivity.this, journey.getJourneyId());
        }
      }
      @Override
      public void onError() {
      }
    });

    stopService(new Intent(MapsActivity.this, TrackerService.class));

    AppPreferences.setJourneyId(0, MapsActivity.this);
  }

  private void startJourney() {
    if (polyline != null) {
      List<LatLng> points = polyline.getPoints();
      points.clear();
      polyline.setPoints(points);
    }
    if (mCurrLocationMarker != null) {
      PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
      Intent intent = new Intent();
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
          intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
          intent.setData(Uri.parse("package:" + getPackageName()));
          startActivity(intent);
        }
      }
      MJourney mJourney = new MJourney();
      mJourney.setStartLatLng(AppUtils.buildLatLngString(mCurrLocationMarker.getPosition()));
      mJourney.setStartTime(Calendar.getInstance().getTime());
      mJourney.setStatus(JourneyStatus.ONGOING);
      MRepo.addJourney(mJourney, new JourneyInsertCB() {
        @Override
        public void onInsertSuccess(long id) {
          AppPreferences.setJourneyId(id, MapsActivity.this);
          startService(new Intent(MapsActivity.this, TrackerService.class));
          LatLng location = mCurrLocationMarker.getPosition();
          AppUtils.geoCode(MapsActivity.this, id, location.latitude, location.longitude, true);
        }

        @Override
        public void onInsertFailed() {
          AppUtils.showToast(MapsActivity.this, "Journey creation failed");
        }
      });

    } else {
      AppUtils.showToast(this, R.string.msg_cannot_identify_loc);
    }
  }

  private LatLng setEndMarker() {
    if (polyline != null) {
      List<LatLng> existingPoints = polyline.getPoints();
      LatLng latLng = existingPoints.get(existingPoints.size() - 1);
      Location location = new Location("");
      location.setLatitude(latLng.latitude);
      location.setLongitude(latLng.longitude);
      MapUtils.addMarker(location, "", mMap);
      return latLng;
    }
    return null;
  }

  public void setSwitchStatus(SwitchCompat mSwitch) {
    if (AppUtils.isServiceRunning(TrackerService.class, this) && mSwitch != null) {
      mSwitch.setChecked(true);
    }
  }
}
