package com.geekmk.mtracker.helper;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class AppConstants {

  public static final long LOCATION_REQUEST_INTERVAL = 1000;
  public static final long LOCATION_REQUEST_INTERVAL_FASTEST = 1000;
  public static final float LOCATION_MIN_DISTANCE_CHANGED = 1;
  public static final int MAX_STATUSES = 30;

  //no point in creating instance of constants
  private AppConstants() {
  }

  public static final String LOCATION_INTENT = "LocationIntent";


  public static final String INTENT_TYPE_CURRENT_LOCATION = "currentLocIntent";

  public static final String EXTRA_CUR_LOC = "currentLoc";

}
