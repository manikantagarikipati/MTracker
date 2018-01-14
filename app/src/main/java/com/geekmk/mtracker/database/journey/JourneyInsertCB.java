package com.geekmk.mtracker.database.journey;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public interface JourneyInsertCB {

  void onInsertSuccess(long id);

  void onInsertFailed();
}
