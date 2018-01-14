package com.geekmk.mtracker.helper;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

public class AppConstants {

  public static final long LOCATION_REQUEST_INTERVAL = 1000;
  public static final long LOCATION_REQUEST_INTERVAL_FASTEST = 1000;
  public static final float LOCATION_MIN_DISTANCE_CHANGED = 20;
  public static final int MAX_STATUSES = 30;
  public static final String EXTRA_JOURNEY_ID = "journeyId";
  public static final String LatLngSeparator = "-";

  //no point in creating instance of constants
  private AppConstants() {
  }

  public class JourneyStatus {

    public static final int ONGOING = 1;

    public static final int COMPLETED = 2;

  }
}
