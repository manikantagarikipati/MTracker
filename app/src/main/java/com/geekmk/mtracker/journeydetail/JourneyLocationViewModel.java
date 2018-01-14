package com.geekmk.mtracker.journeydetail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.geekmk.mtracker.database.AppDatabase;
import com.geekmk.mtracker.database.location.LocationDAO;
import com.geekmk.mtracker.database.location.MLocation;
import java.util.List;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class JourneyLocationViewModel extends AndroidViewModel {

  private LiveData<List<MLocation>> locationList;
  private LocationDAO locationDao;

  public JourneyLocationViewModel(
      @NonNull Application application) {
    super(application);
    locationDao = AppDatabase.getInstance(application).locationDAO();
  }

  public LiveData<List<MLocation>> getLocationsForJourney(long journeyId) {

    return locationDao.fetchLocationForJourney(journeyId);
  }
}
