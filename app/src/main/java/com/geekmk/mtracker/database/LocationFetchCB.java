package com.geekmk.mtracker.database;

import java.util.List;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public interface LocationFetchCB {

  void onLocationLoaded(List<MLocation> locationList);

  void onLocationEmpty();
}
