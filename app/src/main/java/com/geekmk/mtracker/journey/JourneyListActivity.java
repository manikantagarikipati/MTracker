package com.geekmk.mtracker.journey;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.geekmk.mtracker.R;
import com.geekmk.mtracker.database.journey.MJourney;
import com.geekmk.mtracker.helper.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

public class JourneyListActivity extends AppCompatActivity {

  private JourneyAdapter journeyAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_journey_list);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    initComponents();
    observeForJourneys();
  }

  private void initComponents() {
    RecyclerView rvJourneys = findViewById(R.id.rv_journey);
    rvJourneys.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    rvJourneys.setHasFixedSize(true);
    journeyAdapter = new JourneyAdapter();
    rvJourneys.setAdapter(journeyAdapter);
  }

  private void observeForJourneys() {
    JourneyListViewModel journeyListViewModel = ViewModelProviders.of(this)
        .get(JourneyListViewModel.class);

    journeyListViewModel.getJourneyList().observe(this, new Observer<List<MJourney>>() {
      @Override
      public void onChanged(@Nullable List<MJourney> mJourneys) {
        List<ViewIdentifier> viewIdentifiers = new ArrayList<>();
        if (CollectionUtils.isEmpty(mJourneys)) {
          viewIdentifiers.add(new EmptyJourneyView());
        } else {
          for (MJourney journey : mJourneys) {
            viewIdentifiers.add(new JourneyViewIdentifier(journey));
          }
        }
        if (journeyAdapter != null) {
          journeyAdapter.resetView(viewIdentifiers);
        }
      }
    });
  }

}
