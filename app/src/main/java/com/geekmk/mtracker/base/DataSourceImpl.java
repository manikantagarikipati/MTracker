package com.geekmk.mtracker.base;

import android.content.Context;
import com.geekmk.mtracker.database.AppDatabase;
import com.geekmk.mtracker.database.journey.JourneyDAO;
import com.geekmk.mtracker.database.journey.MJourney;
import com.geekmk.mtracker.database.location.LocationDAO;
import com.geekmk.mtracker.database.location.MLocation;
import java.util.List;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class DataSourceImpl implements DataSourceInf {

  private JourneyDAO journeyDAO;
  private LocationDAO locationDAO;

  private static DataSourceImpl INSTANCE;

  private DataSourceImpl(JourneyDAO journeyDAO, LocationDAO locationDAO) {
    this.journeyDAO = journeyDAO;
    this.locationDAO = locationDAO;
  }

  public static DataSourceImpl getINSTANCE(Context context) {
    if (INSTANCE == null) {
      synchronized (DataSourceImpl.class) {
        if (INSTANCE == null) {
          AppDatabase appDatabase = AppDatabase.getInstance(context);
          INSTANCE = new DataSourceImpl(appDatabase.journeyDAO(), appDatabase.locationDAO());
        }
      }
    }
    return INSTANCE;
  }


  @Override
  public MJourney fetchJourney(long journeyId) {
    return journeyDAO.fetchJourney(journeyId);
  }

  @Override
  public long saveJourney(MJourney mJourney) {
    return journeyDAO.insertJourney(mJourney);
  }

  @Override
  public List<MLocation> fetchLocations(long currentJourneyId) {
    return locationDAO.fetchLocation(currentJourneyId);
  }
}
