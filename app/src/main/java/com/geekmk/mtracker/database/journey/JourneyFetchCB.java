package com.geekmk.mtracker.database.journey;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public interface JourneyFetchCB {

  void onJourneyLoaded(MJourney mJourney);

  void onError();
}
