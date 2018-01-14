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
import android.widget.Toast;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.base.BaseMapActivity;
import com.geekmk.mtracker.base.BaseMapActivity.MapPermissionsProvidedCB;
import com.geekmk.mtracker.database.AppDatabase;
import com.geekmk.mtracker.database.journey.MJourney;
import com.geekmk.mtracker.helper.AppConstants.JourneyStatus;
import com.geekmk.mtracker.helper.AppPreferences;
import com.geekmk.mtracker.helper.AppUtils;
import com.geekmk.mtracker.helper.MapUtils;
import com.geekmk.mtracker.journey.JourneyListActivity;
import com.geekmk.mtracker.tracker.TrackerService;
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
    SwitchCompat mSwitch = toggle.getActionView().findViewById(R.id.switchInActionBar);
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
    AppDatabase.getInstance(MapsActivity.this)
        .journeyDAO().fetchJourney(AppPreferences.getCurrentJourneyId(MapsActivity.this))
        .observe(MapsActivity.this, new Observer<MJourney>() {
          @Override
          public void onChanged(@Nullable MJourney journey) {
            if (journey != null) {
              journey.setEndLatLng(AppUtils.buildLatLngString(latLng));
              journey.setEndTime(Calendar.getInstance().getTime().getTime());
              journey.setStatus(JourneyStatus.COMPLETED);
              AppDatabase.getInstance(MapsActivity.this).journeyDAO().insertJourney(journey);
            }
            stopService(new Intent(MapsActivity.this, TrackerService.class));
          }
        });
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
      mJourney.setStartTime(Calendar.getInstance().getTime().getTime());
      mJourney.setStatus(JourneyStatus.ONGOING);
      long journeyId = AppDatabase.getInstance(this).journeyDAO().insertJourney(mJourney);
      AppPreferences.setJourneyId(journeyId, MapsActivity.this);
      startService(new Intent(MapsActivity.this, TrackerService.class));
    } else {
      Toast.makeText(this, "Cannot identify current location", Toast.LENGTH_SHORT).show();
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
