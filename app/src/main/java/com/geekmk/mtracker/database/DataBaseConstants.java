package com.geekmk.mtracker.database;

/**
 * Created by manikanta.garikipati on 13/01/18.
 */

class DataBaseConstants {

  private DataBaseConstants() {
  }

  public static final String TABLE_LOCATION = "Location";

  public static final class LocationColumn {

    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String BATTERY_LEVEL = "batterylevel";
    public static final String JOURNEY_ID = "id";
    public static final String PLACE_INFO = "placeinfo";
    public static final String TIME_STAMP = "ts";
  }

}
