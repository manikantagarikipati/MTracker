package com.geekmk.mtracker.helper;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class AppConstants {

  public static final long LOCATION_REQUEST_INTERVAL = 1000;
  public static final long LOCATION_REQUEST_INTERVAL_FASTEST = 1000;
  public static final float LOCATION_MIN_DISTANCE_CHANGED = 1;
  public static final int MAX_STATUSES = 30;
  public static final String EXTRA_JOURNEY_ID = "journeyId";

  //no point in creating instance of constants
  private AppConstants() {
  }

}
