package com.geekmk.mtracker.base;

import com.geekmk.mtracker.database.journey.MJourney;
import com.geekmk.mtracker.database.location.MLocation;
import java.util.List;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public interface DataSourceInf {

  MJourney fetchJourney(long journeyId);

  long saveJourney(MJourney mJourney);

  List<MLocation> fetchLocations(long currentJourneyId);
}
