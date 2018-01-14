package com.geekmk.mtracker.journeydetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.database.location.MLocation;
import com.geekmk.mtracker.helper.AppConstants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.List;

public class JourneyDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_journey_detail);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {

    if (getIntent() != null && getIntent().getExtras() != null) {
      long journeyId = getIntent().getExtras().getLong(AppConstants.EXTRA_JOURNEY_ID);
      //todo show latlng info here
      JourneyLocationViewModel journeyListViewModel = ViewModelProviders.of(this)
          .get(JourneyLocationViewModel.class);
      journeyListViewModel.getLocationsForJourney(journeyId).observe(this,
          new Observer<List<MLocation>>() {
            @Override
            public void onChanged(@Nullable List<MLocation> mLocations) {
              //todo set poluline here and add start and end location
              String info = "asdfasd";
            }
          });
    }
  }
}
