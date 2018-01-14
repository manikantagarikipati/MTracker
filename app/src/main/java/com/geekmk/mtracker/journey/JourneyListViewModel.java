package com.geekmk.mtracker.journey;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.geekmk.mtracker.database.AppDatabase;
import com.geekmk.mtracker.database.journey.JourneyDAO;
import com.geekmk.mtracker.database.journey.MJourney;
import java.util.List;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class JourneyListViewModel extends AndroidViewModel {

  private JourneyDAO journeyDAO;

  private LiveData<List<MJourney>> journeyList;


  public JourneyListViewModel(
      @NonNull Application application) {
    super(application);

    journeyDAO = AppDatabase.getInstance(application).journeyDAO();
    journeyList = journeyDAO.getAll();
  }

  public LiveData<List<MJourney>> getJourneyList() {
    return journeyList;
  }
}
