package com.geekmk.mtracker.database.location;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public interface LocationInsertCB {

  void onLocationInserted();

  void onInsertError();
}
